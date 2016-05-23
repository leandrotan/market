package com.hdp.ssh.source;
import java.nio.charset.Charset;

public class SshSpoolDirectorySourceConstants {
  public static final String USER_NAME          = "userName";
  public static final String USER_PASS          = "userPass";
  public static final String HOST_NAME          = "hostName";

  public static final String REMOTE_DIR_PATH    = "remotePath";
  public static final String LOCAL_PERSIST_PATH = "localPersistPath";
  public static final String LOCAL_TEMP_DIR = "localTempDir";
  public static final String FILE_PATTERN = "pattern";
  public static final String HDFS_URI = "hdfsURI";
  public static final String HDFS_DIR = "hdfsdir";
  public static final String LOG_DIR = "logdir";
  public static final String PROTOCOL = "protocol";
  public static final String IGNORE_STATE="ignore_state";

  public static final String RECORD_DELIMITER = "\n";
  public static final Charset FILE_CHARSET    = Charset.forName( "UTF-8" );
  
  public static final String SRC_POST_LOAD_ACTION = "source.file.action";
  public static final String HDFS_USER = "hdfs.user";
}
