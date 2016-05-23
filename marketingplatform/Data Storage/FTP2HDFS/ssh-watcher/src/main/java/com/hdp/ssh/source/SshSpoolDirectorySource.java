package com.hdp.ssh.source;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hdp.ssh.source.SshSpoolStateManager.FilePostLoadAction;
import com.hdp.sshHelper.SshClientFTP;
import com.hdp.sshHelper.SshClientI;
import com.hdp.sshHelper.SshClientSFTP;
import com.hdp.sshHelper.SshLogger;

public class SshSpoolDirectorySource
{
  private static final Logger logger = LoggerFactory.getLogger(SshSpoolDirectorySource.class);
  private String hostName, userName, userPass;
  private String remoteSpoolPath;
  private String localPersistPath;
  private String localTempDir;
  private String pattern;
  private String hdfsURI;
  private String hdfsDir;
  private String logDir;
  private SshLogger sshLog;
  private SshClientI sshClient ;
  private SshSpoolStateManager  filesState;
  private Properties properties = new Properties();
  private String protocol;
  private String ignoreState;
  private FilePostLoadAction srcFileAction;
  private String hdfsUser;
  
	public SshSpoolDirectorySource(String confpath) {
		try (FileReader reader = new FileReader(confpath)) {
			this.properties.load(reader);
			this.hostName = properties
					.getProperty(SshSpoolDirectorySourceConstants.HOST_NAME);
			this.userName = properties
					.getProperty(SshSpoolDirectorySourceConstants.USER_NAME);
			this.userPass = properties
					.getProperty(SshSpoolDirectorySourceConstants.USER_PASS);
			this.remoteSpoolPath = properties
					.getProperty(SshSpoolDirectorySourceConstants.REMOTE_DIR_PATH);
			this.localPersistPath = properties
					.getProperty(SshSpoolDirectorySourceConstants.LOCAL_PERSIST_PATH);
			this.localTempDir = properties
					.getProperty(SshSpoolDirectorySourceConstants.LOCAL_TEMP_DIR);
			this.pattern = properties
					.getProperty(SshSpoolDirectorySourceConstants.FILE_PATTERN);
			this.hdfsURI = properties
					.getProperty(SshSpoolDirectorySourceConstants.HDFS_URI);
			this.hdfsDir = properties
					.getProperty(SshSpoolDirectorySourceConstants.HDFS_DIR);
			this.logDir = properties
					.getProperty(SshSpoolDirectorySourceConstants.LOG_DIR);
			this.sshLog = new SshLogger(this.logDir);
			this.protocol = properties
					.getProperty(SshSpoolDirectorySourceConstants.PROTOCOL);
			this.filesState = new SshSpoolStateManager(this.localPersistPath);
			this.ignoreState = properties
					.getProperty(SshSpoolDirectorySourceConstants.IGNORE_STATE);
			if (this.protocol.equalsIgnoreCase("SSH")) {
				this.sshClient = new SshClientSFTP(hostName, userName, userPass);
			} else {
				this.sshClient = new SshClientFTP(hostName, userName, userPass);
			}
			this.srcFileAction = FilePostLoadAction
					.valueOf(properties
							.getProperty(SshSpoolDirectorySourceConstants.SRC_POST_LOAD_ACTION));
			this.hdfsUser = properties
					.getProperty(SshSpoolDirectorySourceConstants.HDFS_USER);
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
  
  public String copyFileToHDFS(final File tempFile, final String outputFileName) throws Exception {
      try {
          UserGroupInformation ugi
              = UserGroupInformation.createRemoteUser(this.hdfsUser);

          ugi.doAs(new PrivilegedExceptionAction<Void>() {

              public Void run() throws Exception {

            	  //1. Get the instance of Configuration
                  Configuration configuration = new Configuration();
                  configuration.addResource(new Path(hdfsURI+"/etc/hadoop/conf.empty/core-site.xml"));
                  configuration.addResource(new Path(hdfsURI+"/etc/hadoop/conf.empty/hdfs-site.xml"));
                  
                  configuration.set("hadoop.job.ugi", hdfsUser);
                  
                  //2. Create an InputStream to read the data from local file
                  FileSystem hdfs = FileSystem.get(new URI(hdfsURI), configuration);
                  //4. Open a OutputStream to write the data, this can be obtained from the FileSytem
                  try(
                	  InputStream inputStream = new BufferedInputStream(new FileInputStream(tempFile));
                	  OutputStream outputStream = hdfs.create(new Path(hdfsURI+hdfsDir+"/"+outputFileName));	  
                  ) {
                	  IOUtils.copyBytes(inputStream, outputStream, 4096, false);  
                  } catch (Exception e) {
                	  logger.error(ExceptionUtils.getStackTrace(e));
                	  throw new Exception("Error while loading contents in HDFS");
                  }
                  return null;
              }
          });
      } catch (Exception e) {
    	  logger.error(ExceptionUtils.getStackTrace(e));
    	  throw new Exception("Error while loading contents in HDFS");
      }
    return  outputFileName;
  }
  
	private ArrayList<String> getPendingFilesToProcess() {
		ArrayList<String> pendingFiles = null;
		try {
			ArrayList<String> files = sshClient.getFilesInPath(remoteSpoolPath,
					pattern);
			filesState.addProcessingList(files);
			if (ignoreState.equalsIgnoreCase("true")) {
				pendingFiles = files;
			} else {
				pendingFiles = filesState.getPending();
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return pendingFiles;
	}

	private void applyPostLoadActionOK(String file) {
		switch (srcFileAction) {
			case NOTHING:
				break;
			case DELETE:
				logger.info("Deleting file - " + file);
				sshClient.deleteFile(file);
				break;
			case RENAME:
				logger.info("Renaming file - " + file);
				sshClient.renameRemoteFile(file, file + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_OK");
				break;
		}
	}

	private void applyPostLoadActionERROR(String file) {
		switch (srcFileAction) {
			case NOTHING:
				break;
			case DELETE:
				break;
			case RENAME:
				logger.info("Renaming file - " + file);
				sshClient.renameRemoteFile(file, file  + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_ERROR");
				break;
		}
	}
  
  public void process()  {
    // Get pending files
    ArrayList< String > pendingFiles = getPendingFilesToProcess();
    String extension;
    String fileNameWithoutExtension;
    String outputFileName;
    
    // Start transaction
    for( String file: pendingFiles )
    { 	
    	try {
	        filesState.markInProcess( file );
	        File tempFile = sshClient.getTempLocalInstance( file,localTempDir);  
	        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd-mm:ss");
	        if( tempFile == null ) {
	        	logger.error( "Unable to retrieve contents: " + file );
	            logger.error( "Marking file in error state: " + file );
	            filesState.markError( file );
	            sshLog.log("FTPtoLOCAL",file.substring(file.lastIndexOf("/")+1),file.substring(file.lastIndexOf("/")+1),df1.format(new Date()),"FAILED","Unable to retrive file from remote server");
	            continue;
	        }
	        sshLog.log("FTPtoLOCAL",file.substring(file.lastIndexOf("/")+1),file.substring(file.lastIndexOf("/")+1),df1.format(new Date()),"SUCCEEDED","File successfully loaded to temporary storage");
	       
	        extension=tempFile.getName().substring(tempFile.getName().lastIndexOf("."));
	        fileNameWithoutExtension=tempFile.getName().substring(0,tempFile.getName().lastIndexOf("."));
	        outputFileName=fileNameWithoutExtension+"_"+df.format(new Date())+extension;
	        try {
	        	copyFileToHDFS(tempFile,outputFileName);
	        	applyPostLoadActionOK(file);
	            sshLog.log("LOCALtoHDFS",tempFile.getName(), outputFileName, df1.format(new Date()), "SUCCEDED", "File successfully loaded to HDFS");
	        } catch (Exception e) {
	        	applyPostLoadActionERROR(file);
	        	sshLog.log("LOCALtoHDFS",tempFile.getName(), outputFileName, df1.format(new Date()), "Failed", ExceptionUtils.getMessage(e));
	        }
	        filesState.markFinished(file);
	        logger.info("Successfully parsed: " + file );
      } catch (Throwable t) {
	        logger.error( "While processing: " + file + " - " + ExceptionUtils.getMessage(t));
	        filesState.markError( file );
      } 
    }
  }
}
