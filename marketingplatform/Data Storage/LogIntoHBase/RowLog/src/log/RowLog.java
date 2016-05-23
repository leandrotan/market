package log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.*;

public class RowLog {
	 public static void main(String[] args) throws Exception {
	      // logRow(args[0].toString(),args[1].toString(),args[2].toString()); 
		 String[] S = {"Hello", "World","Something"};
         LogFile(S);
	 }
	 
	public static void logRow (String t, String CFAM, String row) throws MasterNotRunningException, ZooKeeperConnectionException, IOException
	{
		Configuration conf = HBaseConfiguration.create();
        conf.addResource(new Path("/usr/local/hbase/hbase/conf/hbase-site.xml"));
        HBaseConfiguration.addHbaseResources(conf);
        HBaseAdmin admin = new HBaseAdmin(conf);
        String[] logArr = null;
        HTable table = null;
        //check if table exists and if not create it with the added column name
        if (admin.tableExists(t) == false) {

            HTableDescriptor tableDescriptor = new HTableDescriptor(t);
            HColumnDescriptor columnDescriptor = new HColumnDescriptor(CFAM);
            tableDescriptor.addFamily(columnDescriptor);
            admin.createTable(tableDescriptor);
        }
     
        table = new HTable(conf, t); 
        String Row = row;
        if (Row.contains(","))
        {
        logArr = Row.split(",");
        }
        else {
        	System.out.println("Log Row is in incorrect format. ");
        	}
        try {
        	Timestamp currentTS = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
            Put put = new Put(Bytes.toBytes(currentTS.toString()));
            for(int i=0 ;i < logArr.length ;i++){
            put.add(Bytes.toBytes(CFAM), Bytes.toBytes(logArr[i].split(":")[0]), Bytes.toBytes(logArr[i].split(":")[1]));
            }
            table.put(put);
        } finally {
            admin.close();
        }
	}
	
	public static boolean LogFile(String[] S)
	{   Properties prop = new Properties();
	    boolean flag = true;
	    InputStream input = null;
	    try {
	    	input = RowLog.class.getClassLoader().getResourceAsStream("File.properties");
			prop.load(input);
		} catch (Exception e1) {
			e1.printStackTrace();
			flag= false;
		}
	    
		BufferedWriter writer = null;
        try {
            //create a temporary file
            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS").format(Calendar.getInstance().getTime());
            String Directory = prop.getProperty("File.Directory");
            File logFile = new File(Directory + "Log_" + timeLog + ".txt");
            if(logFile.exists()){
    			if ( (logFile.length() /1048576) > 10 )
    			{
    				int fileNb = (int) ((logFile.length() /1048576) / 10);
    				logFile = new File(Directory + "Log_" + timeLog + "-" + fileNb +".txt");
    			}
            }
            // This will output the full path where the file will be written to...
            writer = new BufferedWriter(new FileWriter(logFile,true));
            writer.write(timeStamp);
            for (int i=0;i<S.length;i++)
            {
            writer.write(" , " + S[i]);
            }
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
            flag= false;
        } finally {
            try {
                // Close the writer
                writer.close();
            } catch (Exception e) {
            	flag = false;
            }
        }
    return flag;
	}
		
	}
