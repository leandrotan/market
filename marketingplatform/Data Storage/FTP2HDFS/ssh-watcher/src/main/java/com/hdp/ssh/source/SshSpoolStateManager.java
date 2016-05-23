package com.hdp.ssh.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshSpoolStateManager
{
  private static final Logger logger =
      LoggerFactory.getLogger(SshSpoolDirectorySource.class);

  public enum FileProcessingState {
    PENDING, IN_PROCESS, FAILED, SUCCEDED
  }
  
  public enum FilePostLoadAction {
	    RENAME, DELETE, NOTHING
  }

  protected HashMap< String, FileProcessingState > stateMap;
  protected String filePath;

  public SshSpoolStateManager( String filePath ) 
  {
    this.filePath = filePath + "/ssh-watcher-state.out";
    this.stateMap = new HashMap< String, FileProcessingState >();
    loadMap();
    
    // Mark any in-process files as errornoues
    markUnprocessedAsError();
  }
  
  public ArrayList< String > getAll() 
  {
    ArrayList< String > pending = new ArrayList< String >();
    for( Map.Entry< String, FileProcessingState > entry: stateMap.entrySet() )
    {
        pending.add( entry.getKey() );
    }
    
    return pending;
  }
  
  public ArrayList< String > getPending() 
  {
    ArrayList< String > pending = new ArrayList< String >();
    for( Map.Entry< String, FileProcessingState > entry: stateMap.entrySet() )
    {
      if( entry.getValue() == FileProcessingState.PENDING )
      {
        pending.add( entry.getKey() );
      }
    }
    
    return pending;
  }

  public void addProcessingList( ArrayList< String > paths ) 
  {
    for( String file: paths ) 
    {
      if( !stateMap.containsKey( file ) ) //Mark files as pending if they are not already included in the state file
      {
        stateMap.put( file, FileProcessingState.PENDING );
      }
    }
    saveState();
  }

  public void markFinished( String file ) 
  {
    stateMap.put( file, FileProcessingState.SUCCEDED );
    saveState();
  }

  public void markError( String file ) 
  {
    stateMap.put( file, FileProcessingState.FAILED );
    saveState();
  }

  public void markInProcess( String file ) 
  {
    stateMap.put( file, FileProcessingState.IN_PROCESS );
    saveState();
  }

  public void markUnprocessedAsError() 
  {
    for( Map.Entry< String, FileProcessingState > entry: stateMap.entrySet() )
    {
      if( entry.getValue() == FileProcessingState.IN_PROCESS )
      {
        logger.info( entry.getKey() + " is marked as in_process, updating to error " );
        entry.setValue( FileProcessingState.FAILED );
      }
    }
    saveState();
  }

  public void saveState() 
  {
    try {
      saveMap();
    } catch( IOException e ) {
      logger.error(ExceptionUtils.getStackTrace(e));
      logger.error( "SshSpoolStateManager: Unable to persist state to disk. Crashing..." );
      System.exit(-1);
    }
  }

  protected void saveMap() throws IOException {
    ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream( filePath ));
    oos.writeObject(stateMap);
    oos.close();
  }

  protected void loadMap() 
  {
    try{ 
      File fp=new File(filePath);
      if(!fp.getParentFile().exists()){
    	  fp.getParentFile().mkdirs();
      }
      ObjectInputStream ois = new ObjectInputStream( new FileInputStream( fp ));
      Object readMap = ois.readObject();
      if(readMap != null && readMap instanceof HashMap) {
        stateMap.putAll((HashMap) readMap);
      }
      ois.close();
    } catch ( FileNotFoundException e ) {
    } catch ( IOException e ) {
    } catch ( ClassNotFoundException e ) {
      // indicates the file doesnt exist. 
      // no worries, we'll start from scratch
    }
  }

}
