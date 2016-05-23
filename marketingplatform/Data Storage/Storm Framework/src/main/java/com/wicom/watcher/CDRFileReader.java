package com.wicom.watcher;

import backtype.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Charbel Hobeika on 3/19/2015.
 */
public class CDRFileReader implements Function {
    static Logger LOGGER = LoggerFactory.getLogger(CDRFileReader.class);
    String CDRfilePath;
    Integer[] indices;
    BufferedReader br;
    String streamFileDelimiter;
    private int partitionIndex;

    public CDRFileReader(Integer[] indices)
    {
        this.indices = indices;
    }

    @Override
    public void prepare(Map map, TridentOperationContext tridentOperationContext)
    {
        this.CDRfilePath = (String) map.get(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY);
        this.partitionIndex=tridentOperationContext.getPartitionIndex();
        if (((String) map.get(GenericConstants.STREAM_FILE_DELIMITER)).equalsIgnoreCase("|") )
        {
            this.streamFileDelimiter = "\\|";
        }
        else
            this.streamFileDelimiter = (String) map.get(GenericConstants.STREAM_FILE_DELIMITER);

    }


    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        String CDRfileName = tridentTuple.getString(0);
        String auditSid = tridentTuple.getString(1);

        LOGGER.warn("Reading file {} from partition {}",CDRfileName,partitionIndex);
        String fullFilePath;
        String input;
        int read = 0;
        int eofFlag=0;

        if (CDRfilePath.endsWith("/") || CDRfilePath.endsWith("\\"))
            fullFilePath = CDRfilePath + CDRfileName;
        else fullFilePath = CDRfilePath+"/"+CDRfileName;

        //read the file use file.separator
        try {
            java.io.FileReader file = new java.io.FileReader(fullFilePath);
            br = new BufferedReader(file);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        //add the lines to the queue
        try {
            while((input = br.readLine()) != null )
            {
                String[] msgArray = input.split(streamFileDelimiter);
                ArrayList<String> values = new ArrayList<String>();

                for (Integer index : indices)
                {
                    values.add(msgArray[index]);
                }

                tridentCollector.emit(new Values(values.toArray()));
                read++;
            }
            /*Logic for signalizing the End of File*/
            if(input==null && eofFlag<1)
            {//make sure that we reached the eof for the first time
                eofFlag++;
                ArrayList<String> values = new ArrayList<String>();
                for(Integer index : indices){
                    values.add("File End");
                }
                tridentCollector.emit(new Values(values.toArray()));
            }
            /*End of Logic for signalizing the End of File*/
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        try {
            br.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.warn("Finished reading '{}' from partition {} . {} rows were read",CDRfileName,partitionIndex,read);

    }


    @Override
    public void cleanup() {

    }

}
