package com.hdp.sshHelper;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

import com.hdp.sshHelper.PatternValidator;

public class SshClientSFTP implements SshClientI {

	private static final Logger logger = LoggerFactory
			.getLogger(SshClientSFTP.class);

	public static final int SSH_PORT = 22;

	protected String hostName, userName, userPass, protocol;

	public SshClientSFTP(String hostName, String userName, String userPass) {
		this.hostName = hostName;
		this.userName = userName;
		this.userPass = userPass;
	}

	private void initSSHClient(SSHClient cl) throws IOException {
		// required if host is not in knownLocalHosts
		cl.addHostKeyVerifier(new PromiscuousVerifier());
		cl.connect(hostName);
		cl.authPassword(userName, userPass);
	}
	
	public ArrayList<String> getFilesInPath(String path, String pattern) {
		ArrayList<String> ret = new ArrayList<String>();
		PatternValidator pv = new PatternValidator(pattern);

		try (SSHClient client = new SSHClient()) {
			initSSHClient(client);

			SFTPClient sftp = client.newSFTPClient();
			List<RemoteResourceInfo> files = sftp.ls(path);

			for (RemoteResourceInfo file : files) {
				if (!file.isDirectory()) {
					if (pv.validate(file.getName())) {
						ret.add(file.getPath());
					}
				}
			}
			sftp.close();
			client.disconnect();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return ret;
	}

	public File getTempLocalInstance(String remoteFilePath, String localTempDir) {
		File tempFile = null;

		String localFilePath = localTempDir + File.separator
				+ remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
		logger.debug(localFilePath);
		
		try(SSHClient client = new SSHClient()) {
			initSSHClient(client);

			tempFile = new File(localFilePath);
			if (!tempFile.getParentFile().exists()) {
				tempFile.getParentFile().mkdirs();
			}
			tempFile.deleteOnExit();

			SFTPClient sftp = client.newSFTPClient();
			try {
				sftp.get(remoteFilePath, new FileSystemFile(tempFile));
			} catch (Exception e) {
				logger.error("Error while getting file from SFTP - "
						+ e.toString());
			}
			sftp.close();
			client.disconnect();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return null;
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
		try(SSHClient client = new SSHClient()) {
			initSSHClient(client);
			SFTPClient sftp = client.newSFTPClient();
			sftp.rename(oldPath, newPath);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public void deleteFile(String filePath) {
		try(SSHClient client = new SSHClient()) {
			initSSHClient(client);
			SFTPClient sftp = client.newSFTPClient();
			sftp.rm(filePath);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
}
