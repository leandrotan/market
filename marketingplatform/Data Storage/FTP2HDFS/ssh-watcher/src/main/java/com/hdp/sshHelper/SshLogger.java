package com.hdp.sshHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;

public class SshLogger {
	  private File logFile;

	  public SshLogger(String logpath){
			this.logFile=new File(logpath+File.separator+"ssh-watcher-log.csv");
			if(!logFile.exists()){
				if(!this.logFile.getParentFile().exists()){
					this.logFile.getParentFile().mkdirs();
				}
			}

	  }
	  
	  public void log(String stage,String remoteFileName,String destFileName,String timestamp, String status, String description ){
		    
		  String [] logging =null;
		  String logline;
		  try {
			CSVWriter logwriter = new CSVWriter(new FileWriter(this.logFile,true), ',');
			logline=stage+","+remoteFileName+","+destFileName+","+timestamp+","+status+","+description;
	        logging=logline.split(",");
	        try{
	        	logwriter.writeNext(logging);
	        	logwriter.close();
	        } catch(Exception e)
	        {
	        	System.out.println(e.toString());
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
}
