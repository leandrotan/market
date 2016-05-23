package com.wicom.storm.trident;

/**
 * Created by Charbel Hobeika on 3/5/2015.
 */
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
//import DataSource;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class FileReaderSpout implements IBatchSpout {

    private LinkedBlockingDeque<String> queue;
    private DataSource dataSource;
    int batchSize;
    private Fields outputFields;
    private Integer[] indices;
    BufferedReader br;


    public FileReaderSpout(DataSource dataSource, int batchSize, Fields outputFields, Integer[] indices) {
        this.dataSource = dataSource;
        this.batchSize = batchSize;
        this.outputFields = outputFields;
        this.indices = indices;
    }
//D:\My Files\My Projects\Storm\TridentStormCDRreader\src\main\java\com\wicom\storm\trident\TopologyWithTimeStamp.java
    @Override
    public void open(Map map, TopologyContext topologyContext) {
        queue = new LinkedBlockingDeque<String>();
        try {
            String path  = "src/main/resources/";

            FileReader file = new FileReader(path + dataSource.filename);
            br = new BufferedReader(file);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void emitBatch(long batchId, TridentCollector tridentCollector) {
        String input;

        int read = 0;

        try {
            while((input = br.readLine()) != null && read < batchSize) {
                queue.offer(input);
                read++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < batchSize; i++) {
            String message = queue.poll();
            if(message == null)
                Utils.sleep(50);
            else {
                String[] msgArray = message.split("\\|");

                ArrayList<String> values = new ArrayList<String>();
                for (Integer index : indices) {
                    values.add(msgArray[index]);
                }

                tridentCollector.emit(new Values(values.toArray()));
            }
        }
    }

    @Override
    public void ack(long l) {

    }

    @Override
    public void close() {

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