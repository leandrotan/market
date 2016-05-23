package com.wicom.flume.utils;

import java.io.*;
import java.util.Scanner;

public class FileStoreHelper {

	private String filename;
	File f;
	public FileStoreHelper(String filename){
		this.filename=filename;
		f = new File(this.filename);
	}
	
	public FileStoreHelper(String folder, String filename){
		this.filename=filename;
		f = new File(folder, filename);
	}
	
	/** Write content to the given file. */
	public  void write(String token) throws IOException  {
	    Writer out = new OutputStreamWriter(new FileOutputStream(f));
	    try {
	      out.write(token);
	    }
	    finally {
	      out.close();
	    }
	  }
	  
	  /** Read the contents of the given file. */
	  public String read() throws IOException {
	    StringBuilder text = new StringBuilder();
	    Scanner scanner = new Scanner(new FileInputStream(f));
	    try {
	      while (scanner.hasNextLine()){
	        text.append(scanner.nextLine());
	      }
	    }
	    finally{
	      scanner.close();
	    }
	    return text.toString();
	  }
	  
	  public boolean isFileExists(){
		  return f.exists();
	  }
}
