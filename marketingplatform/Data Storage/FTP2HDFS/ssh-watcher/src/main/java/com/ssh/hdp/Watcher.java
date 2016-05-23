package com.ssh.hdp;

import com.hdp.ssh.source.*;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Watcher {
	private static final Logger logger =
		      LoggerFactory.getLogger(Watcher.class);
	public static void main(String[] args)
	{
		String confPath = args[0];
		logger.debug(confPath);
		SshSpoolDirectorySource sshspooldir=new SshSpoolDirectorySource(confPath);
		try{
			sshspooldir.process();
		} catch(Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
}
