package com.wicom.watcher.DB;

import backtype.storm.tuple.Values;
import com.wicom.watcher.GenericConstants;
import com.wicom.watcher.audit.HbaseAudit;
import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Charbel Hobeika on 3/19/2015.
 */
public class transformerFileReader implements Function {
    static Logger LOGGER = LoggerFactory.getLogger(transformerFileReader.class);
    String filePath;
    Integer[] indices;
    BufferedReader br;
    String streamFileDelimiter;
    private int partitionIndex;
    Integer tblsize;
    private String auditDbAuditTable;
    HbaseAudit HbA;

    @Override
    public void prepare(Map map, TridentOperationContext tridentOperationContext)
    {
        this.auditDbAuditTable= (String) map.get(GenericConstants.AUDIT_DB_AUDITTABLE);
        this.HbA = new HbaseAudit(this.auditDbAuditTable);
        this.filePath = (String) map.get(GenericConstants.TRANSFORMER_FILE_INCOMING_DIRECTORY);
        this.partitionIndex=tridentOperationContext.getPartitionIndex();
        //  add csv file delimiter
        if (((String) map.get(GenericConstants.STREAM_FILE_DELIMITER)).equalsIgnoreCase("|") )
        {
            this.streamFileDelimiter = "\\|";
        }
        else
            this.streamFileDelimiter = (String) map.get(GenericConstants.STREAM_FILE_DELIMITER);
        this.tblsize = Integer.parseInt((String) map.get(GenericConstants.HIVE_DB_TBL_COLUMNS));
    }


    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        String CDRfileName = tridentTuple.getStringByField("csvFileName");
        //String CDRfileName = "";
       String auditSid="";

        LOGGER.warn("Reading file {} from partition {}",CDRfileName,partitionIndex);
        String fullFilePath;
        String input;
        int read = 0;
        int eofFlag=0;

        if (filePath.endsWith("/") || filePath.endsWith("\\"))
            fullFilePath = filePath + CDRfileName;
        else fullFilePath = filePath +"/"+CDRfileName;

        //fullFilePath = "D:\\Data\\Transformer\\incoming\\C20150408.0800+0300-20150409.0900+0300_NBSC7_1000.csv";
        //fullFilePath = filePath+"C20150408.0800+0300-2015040777.0900+0300_NBSC7_1000_test.csv";

        //read the file use file.separator
        try {
            LOGGER.warn("Reading file {}",fullFilePath);
            FileReader file = new FileReader(fullFilePath);
            br = new BufferedReader(file);
            auditSid=HbA.logAudit("csv Reading",fullFilePath);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (MasterNotRunningException e) {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //add the lines to the queue
        try {
            //Integer factTableSize = 1112;
            LOGGER.warn("Fact Table size {}",tblsize);

            while((input = br.readLine()) != null )
            {
                String[] msgArray = input.split(",");

                // initialising an ArrayList of empty string values, have same size of destination table
                ArrayList<String> inputarray =
                        new ArrayList<String>(Collections.<String>nCopies(tblsize, " "));

                for (int i=0;i<msgArray.length;i++) {
                 if (i == 4)
                     inputarray.set(i, auditSid);
                    else
                    inputarray.set(i, msgArray[i].trim());
                }
                tridentCollector.emit(new Values(inputarray.toArray()));

                read++;

            }
            /*Logic for signalizing the End of File*/
            LOGGER.warn("Emitting File End");
            if(input==null && eofFlag<1)
            {//make sure that we reached the eof for the first time
                eofFlag++;
                ArrayList<String> values =
                        new ArrayList<String>(Collections.nCopies(tblsize,"File End"));

                tridentCollector.emit(new Values(values.toArray()));

                HbA.updateAudit(auditSid, String.valueOf(read), "Processed");

                LOGGER.warn("Finished reading '{}' from partition {} . {} rows were read", CDRfileName, partitionIndex, read);
            }
            /*End of Logic for signalizing the End of File*/
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        try {
            br.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            try {
                HbA.updateAudit(auditSid,"0","ERROR");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


    }


    @Override
    public void cleanup() {

    }

}
