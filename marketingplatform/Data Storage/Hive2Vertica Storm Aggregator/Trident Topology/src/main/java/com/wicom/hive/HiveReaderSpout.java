package com.wicom.hive;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by albert.elkhoury on 3/5/2015.
 */
public class HiveReaderSpout implements IBatchSpout {

    private static final long serialVersionUID = 10L;
    private LinkedBlockingDeque<String> queue;
    //int batchSize;
    private Fields outputFields;
    Connection con;
    Statement stmt;
    ResultSet res;
    final Logger logger = LoggerFactory.getLogger(HiveReaderSpout.class);

    public HiveReaderSpout(Fields outputFields/*,int batchSize*/){
        //this.batchSize=batchSize;
        this.outputFields=outputFields;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext){
        queue = new LinkedBlockingDeque<String>();
        try{
            String driverName= (String) map.get(HiveReaderTopologyConstants.HIVE_DRIVER);
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String server=(String) map.get(HiveReaderTopologyConstants.HIVE_SERVER);
            String port=(String) map.get(HiveReaderTopologyConstants.HIVE_PORT);
            String db=(String) map.get(HiveReaderTopologyConstants.HIVE_DB);
            String user=(String) map.get(HiveReaderTopologyConstants.HIVE_USER);
            String pass=(String) map.get(HiveReaderTopologyConstants.HIVE_PASS);
            String query=(String) map.get(HiveReaderTopologyConstants.HIVE_READER_QUERY);
            con= DriverManager.getConnection("jdbc:hive2://"+server+":"+port+"/"+db,user,pass);
            stmt=con.createStatement();
            //logger.warn("Executing Query");
            res=stmt.executeQuery(query);
            int columnsNumber=res.getMetaData().getColumnCount();
            //int rowCount=0;
            //logger.warn("Query Executed");
            while (res.next()) {
                String row="";
                for(int i=1;i<=columnsNumber;i++){
                    if(i>1)
                    row=row+"|"+res.getString(i);
                    else
                        row=row+res.getString(i);
                }
                //logger.warn("Logging from prepare: row=" + row);
                queue.offer(row);
                //rowCount++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void emitBatch(long batchId,TridentCollector tridentCollector){
        String row;
        row=queue.poll();
        int rowCount=1;
        int queueSize=queue.size();
        //logger.error("Queue Size: "+String.valueOf(queue.size()));
        while(rowCount<=queueSize/*&& read<batchSize*/){
            String[] msgArray = row.split("\\|");
            List<Object> tuple=new ArrayList<Object>();
            for(int i=0;i<msgArray.length;i++ ){
                tuple.add(msgArray[i]);

            }
            //logger.error("Emitting tuple number: "+rowCount);
            tridentCollector.emit(tuple);
            try {
                row = queue.poll();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            //logger.error("Polling row number  : "+rowCount);
            //logger.error("Polling row data  : "+row);
            rowCount++;
        }
    }

    @Override
    public void ack(long l){}

    @Override
    public void close(){
    }

    @Override
    public Map getComponentConfiguration() {
        return null;
    }
    @Override
    public Fields getOutputFields() {
        return outputFields;
    }
}
