package com.hdp.sshHelper;

import java.io.File;
import java.util.ArrayList;

public interface SshClientI {
	public ArrayList< String > getFilesInPath( String path, String pattern );
	
	public File getTempLocalInstance(String remoteFilePath,String localTempDir);
	
	public byte[] getContents(String remoteFilePath, String localTempDir);
	
	public void renameRemoteFile(String oldPath, String newPath);
	
	public void deleteFile(String filePath);
}
