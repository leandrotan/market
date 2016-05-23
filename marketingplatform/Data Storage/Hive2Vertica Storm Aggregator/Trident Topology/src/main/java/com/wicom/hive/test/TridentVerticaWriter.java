package com.wicom.hive.test;

import com.vertica.jdbc.VerticaCopyStream;
import com.wicom.hive.HiveReaderTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

/**
 * Created by albert.elkhoury on 3/11/2015.
 */

public class TridentVerticaWriter extends BaseFunction {
    final Logger logger = LoggerFactory.getLogger(HiveReaderTopology.class);
    Connection conn=null;
    Statement stmt=null;
    VerticaCopyStream stream;
    FileOutputStream fos;
    FileInputStream fis;
    int rowCount;
    File tempFile;

    @Override
    public void prepare(Map conf, TridentOperationContext context){

        rowCount=0;
        try {
            tempFile=File.createTempFile("cdr_data_aggregated",".tmp");
            fos = new FileOutputStream(tempFile,false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String driverName= (String) conf.get("verticaDriverName");
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            logger.error("Could not find the JDBC driver class.");
            e.printStackTrace();
        }

        String server= (String) conf.get("verticaServer");
        String port= (String) conf.get("verticaPort");
        String db= (String) conf.get("verticaDb");
        String user= (String) conf.get("verticaUser");
        String pass= (String) conf.get("verticaPass");
        Properties myProp = new Properties();
        myProp.put("user", user);
        myProp.put("password", pass);
        myProp.put("AutoCommit", "false");
        try {
            conn = DriverManager.getConnection("jdbc:vertica://" + server + ":" + port + "/" + db, myProp);
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String row = new String();
        int i=0;
        for (String field :tuple.getFields())
        {
            //if(!tuple.getStringByField(field).isEmpty()){
            row=i>0?row+"|"+String.valueOf(tuple.getValue(i)):row+String.valueOf(tuple.getValue(i));
            //}
            i++;
        }
        logger.warn("Appending row #"+rowCount+" - content:"+row);
        try {
            fos.write(row.getBytes());
            fos.write(System.getProperty("line.separator").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        rowCount++;
        if(rowCount==100){
            try {
                logger.warn("Initializing Stream...");
                fos.close();
                boolean result=stmt.execute("COPY storm_cdr_data FROM LOCAL '"+tempFile.getAbsolutePath()+"' DIRECT ENFORCELENGTH");
                if(result) {
                    logger.warn("COPY Statement executed successfully...");
                }
                else{
                    //logger.error("Error executing copy statement..");
                }
                fos=new FileOutputStream(tempFile,false);
                rowCount=0;
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

