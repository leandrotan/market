package com.hdp.test;

import com.hdp.ssh.source.*;

public class TestSSH {

	public static void main(String[] args)
	  {
		String confPath="C:\\Users\\albert.elkhoury\\workspace\\FLUME\\ssh-watcher\\ssh-watcher.properties";
		//System.out.println(confPath);
		
		SshSpoolDirectorySource sshspooldir=new SshSpoolDirectorySource(confPath);
		try{
			sshspooldir.process();
		}catch(Exception e){
			System.out.println(e.toString());
		}
		
		
		/*
		
	    SshClientJ sc = new SshClientJ( "localhost", "user", "pass" );
	    ArrayList< String > files = sc.getFilesInPath( "/remote-path/","pattern" );
	    
	    System.out.println( "Size of files array: " + files.size() ); 
	    for( String file: files )
	    {
	      byte[] b = sc.getContents( file );
	      System.out.println( "filename: " + file + ", size: " + b.length );
	    }
*/
	  }
}
