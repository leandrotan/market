package com.wicom.watcher;

import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import com.wicom.watcher.audit.HbaseAudit;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

//import java.nio.channels.FileChannel;
//import java.nio.file.Files;

/**
 * Created by Charbel Hobeika on 3/19/2015.
 */
public class SpoolDirectoryBatchSpout_backup implements IBatchSpout {
    private int taskIndex;
    private String baseDirectory;
    private String destinationDirectoryPath;
    private File destinationDirectory;
    private String fileExtensionFilter;
    private String auditDbAuditTable;
    private String topologyProcessId;
    private LinkedBlockingDeque<String> queue;
    private HbaseAudit hba;
    static Logger LOGGER = LoggerFactory.getLogger(SpoolDirectoryBatchSpout_backup.class);
    HashMap<Long, List<List<Object>>> batches = new HashMap<Long, List<List<Object>>>();
    int maxBatchSize;

    //Constructor
    public SpoolDirectoryBatchSpout_backup() {
        this.maxBatchSize = 1;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext) {
        this.taskIndex = topologyContext.getThisTaskIndex();//Get the index of the instance of the spout (for parallelism)
        this.baseDirectory = (String) map.get(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY);
        this.destinationDirectoryPath = (String) map.get(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY);
        this.fileExtensionFilter = (String) map.get(GenericConstants.WATCHER_FILE_EXTENSION);
        this.destinationDirectory = new File(this.destinationDirectoryPath);
        this.auditDbAuditTable = (String) map.get(GenericConstants.AUDIT_DB_AUDITTABLE);
        this.topologyProcessId = (String) map.get(GenericConstants.TOPOLOGY_PROCESS_ID);
        this.hba = new HbaseAudit(auditDbAuditTable);
        DirectorySpool();
    }

    @Override
    public void emitBatch(long batchId, TridentCollector tridentCollector) {
        List<List<Object>> batch = this.batches.get(batchId);
        if (batch == null) {
            batch = new ArrayList<List<Object>>();
            for (int i = 0; i < maxBatchSize; i++) {
                String fileName = queue.poll();
                List<Object> fileObject = new ArrayList<Object>();
                try {
                    if (fileName != null) {
                        // audit the file
                        String s = hba.logAudit(topologyProcessId, fileName);

                        // add filename and the auditSid so that i can use both in the CDRreader
                        fileObject.add(fileName);
                        fileObject.add(s);
                        batch.add(fileObject);
                    } else {

                        if (taskIndex == 0)//Only first instance spools the directory, other instances will emit by consuming from queue
                        {

                            DirectorySpool();
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
            this.batches.put(batchId, batch);
        }
        for (List<Object> list : batch) {
            tridentCollector.emit(list);
            LOGGER.warn("File {} is emitted for reading from spout #{}.", list.get(0), taskIndex);
        }

    }

    @Override
    public void ack(long batchId) {

        this.batches.remove(batchId);
    }

    @Override
    public void close() {

    }

    @Override
    public Map getComponentConfiguration() {
        Config conf = new Config();
        //conf.setMaxTaskParallelism(1);
        return conf;
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("filename", "auditsid");
    }


    public void DirectorySpool() {
        queue = new LinkedBlockingDeque<String>();
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(fileExtensionFilter.equalsIgnoreCase(".*"))
                {
                    if(name.toLowerCase().contains("."))
                        return true;
                    else return false;
                }
                else {
                    if (name.toLowerCase().endsWith(fileExtensionFilter))
                        return true;
                    else return false;
                }
            }
        };

        File[] listOfFiles = new File(baseDirectory).listFiles(filter);

        if (listOfFiles.length > 0) {
            LOGGER.warn("Filtering Files by: " + fileExtensionFilter);
            LOGGER.warn("Files found: " + listOfFiles.length + " ...");

            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        moveFile(file.getAbsoluteFile(), destinationDirectory);
                        queue.offer(file.getName());
                        LOGGER.warn(file.getName() + " is moved to " + destinationDirectory.getAbsolutePath());
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage());
                    }
                }

            }
        }
    }

    public static void moveFile(File baseDirectory, File destinationDirectory)
            throws IOException {

        FileUtils.moveFileToDirectory(baseDirectory, destinationDirectory, true);

    }

}