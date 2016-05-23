package com.hdp.sshHelper;

import java.util.ArrayList;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.io.FileUtils; 
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hdp.sshHelper.PatternValidator;

public class SshClientFTP implements SshClientI {

	private static final Logger logger = LoggerFactory
			.getLogger(SshClientFTP.class);

	public static final int SSH_PORT = 21;

	protected String hostName, userName, userPass, protocol;

	public SshClientFTP(String hostName, String userName, String userPass) {
		this.hostName = hostName;
		this.userName = userName;
		this.userPass = userPass;
	}

	private void initFtpClient(FTPClient cl) throws SocketException, IOException {
		cl.connect(hostName, SshClientFTP.SSH_PORT);
		cl.login(userName, userPass);
		cl.enterLocalPassiveMode();
		cl.setFileType(FTP.BINARY_FILE_TYPE);
	}
	
	private void closeFtpClient(FTPClient cl) {
		if (cl.isConnected()) {
			try {
				cl.logout();
				cl.disconnect();
			} catch (IOException e) {
				logger.warn(ExceptionUtils.getMessage(e));
			}
		}
	}
	
	public ArrayList<String> getFilesInPath(String path, String pattern) {
		ArrayList<String> ret = new ArrayList<String>();
		PatternValidator pv = new PatternValidator(pattern);

		FTPClient ftpClient = new FTPClient();
		try {
			initFtpClient(ftpClient);

			FTPFile[] files = ftpClient.listFiles(path);

			for (FTPFile file : files) {
				if (!file.isDirectory()) {
					if (pv.validate(file.getName())) {
						ret.add(path + "/" + file.getName());
					}
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} finally {
			closeFtpClient(ftpClient);
		}
		return ret;
	}

	public File getTempLocalInstance(String remoteFilePath, String localTempDir) {
		File tempFile = null;

		String localFilePath = localTempDir + File.separator
				+ remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
		FTPClient ftpClient = new FTPClient();
		
		tempFile = new File(localFilePath);
		if (!tempFile.getParentFile().exists()) {
			tempFile.getParentFile().mkdirs();
		}
		tempFile.deleteOnExit();
		
		try (OutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(tempFile))) {
			initFtpClient(ftpClient);

			boolean success = ftpClient.retrieveFile(remoteFilePath,outputStream);
			if (!success) {
				return null;
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return null;
		} finally {
			closeFtpClient(ftpClient);
		}
		return tempFile;
	}

	public byte[] getContents(String remoteFilePath, String localTempDir) {
		byte[] buffer = null;

		// TODO: stream instead of copy entire file
		try {
			File tempFile = getTempLocalInstance(remoteFilePath, localTempDir);
			buffer = FileUtils.readFileToByteArray(tempFile);
		} catch (Exception e) {
			// TODO: this needs to be more robust than just catching all
			// exceptions
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return buffer;
	}

	public void renameRemoteFile(String oldPath, String newPath) {
		FTPClient ftpClient = new FTPClient();
		try {
			initFtpClient(ftpClient);
			ftpClient.rename(oldPath, newPath);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} finally {
			closeFtpClient(ftpClient);
		}
	}
	
	public void deleteFile(String filePath) {
		FTPClient ftpClient = new FTPClient();
		try {
			initFtpClient(ftpClient);
			ftpClient.deleteFile(filePath);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} finally {
			closeFtpClient(ftpClient);
		}
	}
}
