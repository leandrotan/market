package com.wicom.watcher;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.wicom.watcher.Ericsson.*;
import com.wicom.watcher.hive.HiveStateFactoryCDR;
import com.wicom.watcher.hive.HiveUpdaterCDR;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.storm.hive.bolt.mapper.DelimitedRecordHiveMapper;
import org.apache.storm.hive.common.HiveOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;

import java.io.*;
import java.io.FileReader;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Charbel Hobeika on 3/19/2015.
 */

public class CDRTopology {

    public static Logger LOGGER = LoggerFactory.getLogger(CDRTopology.class);
    public static TridentTopology topology;
    public static Map<String,String> props = new HashMap<>();

    /*Default constructor*/
    public CDRTopology() throws IOException {
        this.topology=new TridentTopology();
        this.props.put(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY,"D:/Data");
        this.props.put(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY,"D:/Data/queued");
        this.props.put(GenericConstants.WATCHER_FILE_EXTENSION,".dwh");
        this.props.put(GenericConstants.STREAM_FILE_DELIMITER,"\\|");
        this.props.put(GenericConstants.AUDIT_DB_AUDITTABLE,"dim_audit");
        this.props.put(GenericConstants.TOPOLOGY_PROCESS_ID,"my process topology specific");
        this.props.put(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS,"60");
        this.props.put(GenericConstants.TOPOLOGY_PARALLELISM_READER_HINT,"3");
        this.props.put(GenericConstants.TOPOLOGY_PARALLELISM_PROCESSING_HINT,"5");
        this.props.put(GenericConstants.TOPOLOGY_PARALLELISM_LOADER_HINT,"3");
        this.props.put(GenericConstants.HIVE_DB_METASTOREURI,"thrift://nex-hdp-14.nexius.com:9083");
        this.props.put(GenericConstants.HIVE_DB_DBNAME,"default");
        this.props.put(GenericConstants.HIVE_DB_TBLNAME,"raw_cdr_data");
        this.props.put(GenericConstants.HIVE_DB_BATCH_SIZE,"500");
        this.props.put(GenericConstants.HIVE_DB_TX_PER_BATCH,"2");
        this.props.put(GenericConstants.HIVE_DB_IDLE_TIMEOUT,"10");
        this.props.put(GenericConstants.DEBUG_MODE,"true");
        this.props.put(GenericConstants.TRANSFORMER_FILE_INCOMING_DIRECTORY,"D:/Data/Transformer/incoming/");
        this.props.put(GenericConstants.TRANSFORMER_FILE_QUEUED_DIRECTORY,"D:/Data/Transformer/enqueued/");
        this.props.put(GenericConstants.TRANSFORMER_FILE_PROCESSED_DIRECTORY,"D:/Data/Transformer/processed/");
        this.props.put(GenericConstants.TRANSFORMER_FILE_ERROR_DIRECTORY,"D:/Data/Transformer/error/");


    }

    /*Constructor*/
    public CDRTopology(String propFile) throws IOException {
        this.topology=new TridentTopology();
        this.props = initializeProperties(propFile);
    }

    public static Map<String,String> initializeProperties(String filePath) throws IOException {
        Map<String,String> props = new HashMap<String, String>();
        Properties prop= new Properties();
        FileReader propReader=new FileReader(filePath);
        prop.load(propReader);
        props.put(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY, prop.getProperty(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY));
        props.put(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY,prop.getProperty(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY));
        props.put(GenericConstants.WATCHER_FILE_EXTENSION,prop.getProperty(GenericConstants.WATCHER_FILE_EXTENSION));
        props.put(GenericConstants.STREAM_FILE_DELIMITER,prop.getProperty(GenericConstants.STREAM_FILE_DELIMITER));
        props.put(GenericConstants.AUDIT_DB_AUDITTABLE,prop.getProperty(GenericConstants.AUDIT_DB_AUDITTABLE));
        props.put(GenericConstants.TOPOLOGY_PROCESS_ID,prop.getProperty(GenericConstants.TOPOLOGY_PROCESS_ID));
        props.put(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS,prop.getProperty(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS));
        props.put(GenericConstants.TOPOLOGY_PARALLELISM_READER_HINT,prop.getProperty(GenericConstants.TOPOLOGY_PARALLELISM_READER_HINT));
        props.put(GenericConstants.TOPOLOGY_PARALLELISM_PROCESSING_HINT,prop.getProperty(GenericConstants.TOPOLOGY_PARALLELISM_PROCESSING_HINT));
        props.put(GenericConstants.TOPOLOGY_PARALLELISM_LOADER_HINT,prop.getProperty(GenericConstants.TOPOLOGY_PARALLELISM_LOADER_HINT));
        props.put(GenericConstants.HIVE_DB_METASTOREURI,prop.getProperty(GenericConstants.HIVE_DB_METASTOREURI));
        props.put(GenericConstants.HIVE_DB_DBNAME,prop.getProperty(GenericConstants.HIVE_DB_DBNAME));
        props.put(GenericConstants.HIVE_DB_TBLNAME,prop.getProperty(GenericConstants.HIVE_DB_TBLNAME));
        props.put(GenericConstants.HIVE_DB_BATCH_SIZE,prop.getProperty(GenericConstants.HIVE_DB_BATCH_SIZE));
        props.put(GenericConstants.HIVE_DB_TX_PER_BATCH,prop.getProperty(GenericConstants.HIVE_DB_TX_PER_BATCH));
        props.put(GenericConstants.HIVE_DB_IDLE_TIMEOUT,prop.getProperty(GenericConstants.HIVE_DB_IDLE_TIMEOUT));
        props.put(GenericConstants.DEBUG_MODE,prop.getProperty(GenericConstants.DEBUG_MODE));
        props.put(GenericConstants.TRANSFORMER_FILE_INCOMING_DIRECTORY,prop.getProperty(GenericConstants.TRANSFORMER_FILE_INCOMING_DIRECTORY));
        props.put(GenericConstants.TRANSFORMER_FILE_QUEUED_DIRECTORY,prop.getProperty(GenericConstants.TRANSFORMER_FILE_QUEUED_DIRECTORY));
        props.put(GenericConstants.TRANSFORMER_FILE_PROCESSED_DIRECTORY,prop.getProperty(GenericConstants.TRANSFORMER_FILE_PROCESSED_DIRECTORY));
        props.put(GenericConstants.TRANSFORMER_FILE_ERROR_DIRECTORY,prop.getProperty(GenericConstants.TRANSFORMER_FILE_ERROR_DIRECTORY));
        return props;
    }

    public  StormTopology buildTopology(Map<String, String> props) throws IOException {

        /*Adding Hive Configuration and state*/
        HiveOptions hiveOptions;
        hiveOptions = initializeHiveOptions(props);
        int readerHint = Integer.parseInt(props.get(GenericConstants.TOPOLOGY_PARALLELISM_READER_HINT));
        int processingHint = Integer.parseInt(props.get(GenericConstants.TOPOLOGY_PARALLELISM_PROCESSING_HINT));
        int loaderHint = Integer.parseInt(props.get(GenericConstants.TOPOLOGY_PARALLELISM_LOADER_HINT));

        StateFactory factory = new HiveStateFactoryCDR().withOptions(hiveOptions);

        Stream spout = topology.newStream("listOfFiles",
                new SpoolDirectoryBatchSpout()).name("Spooling Spout").parallelismHint(readerHint).shuffle();
        Stream stream1=spout.each(
                new Fields("filename", "auditsid"),
                new CDRFileReader(FileNeededFields.getIndices()),
                FileNeededFields.getFields()
        ).name("File Reading Bolt");
        Stream stream2=stream1.each(
                FileNeededFields.getFields(),
                new ExtractDayKey(),
                new Fields("day_key")
        ).name("Day Key Extractor Bolt").parallelismHint(processingHint)
                //.each(FileNeededFields.getFieldsWithAuditSidFileName(), new PrintFunction(), new Fields())
                ;

        TridentState state =stream2.partitionPersist(factory,
                        FileNeededFields.getFieldsWithAuditSidFileName(),
                        new HiveUpdaterCDR(), new Fields()).parallelismHint(loaderHint);

        return topology.build();
    }


    public static HiveOptions initializeHiveOptions(Map<String,String> props){
        HiveOptions hvOpt;

        // Hive connection configuration
        String metaStoreURI = props.get(GenericConstants.HIVE_DB_METASTOREURI);
        String dbName = props.get(GenericConstants.HIVE_DB_DBNAME);
        String tblName = props.get(GenericConstants.HIVE_DB_TBLNAME);
        int batchSize = Integer.parseInt(props.get(GenericConstants.HIVE_DB_BATCH_SIZE));
        int txnsPerBatch =Integer.parseInt(props.get(GenericConstants.HIVE_DB_TX_PER_BATCH));
        int idleTimeout = Integer.parseInt(props.get(GenericConstants.HIVE_DB_IDLE_TIMEOUT));
        // Fields for possible partition
        List<String> partNames = DBFields.getPartNames();
        // Fields for possible column data
        List<String> colNames =DBFields.getColNames();

        // Record Writer configuration
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper()
                .withColumnFields(new Fields(colNames))
                .withPartitionFields(new Fields(partNames));

        hvOpt = new HiveOptions(metaStoreURI, dbName, tblName, mapper)
                .withTxnsPerBatch(txnsPerBatch)
                .withBatchSize(batchSize)
                .withIdleTimeout(idleTimeout);

        return hvOpt;
    }


    public static void main(final String[] args){

        final CDRTopology cdrTopology;

        try{
            if(args.length>=1)
            {
                LOGGER.warn("Topology Started reading from properties files passed as argument");
                 cdrTopology = new CDRTopology(args[0]);
            }
            else
            {
                LOGGER.warn("Topology Started reading default properties, no properties file passed");
                cdrTopology = new CDRTopology();
            }

            LOGGER.warn("Starting Directory Watching on: " + cdrTopology.props.get(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY) + " for files filtered with \""+ cdrTopology.props.get(GenericConstants.WATCHER_FILE_EXTENSION) +"\"");
            /*Launch the topology as sm_user */
            UserGroupInformation ugi= UserGroupInformation.createRemoteUser("sm_user");
            ugi.doAs(new PrivilegedExceptionAction<Void>(){
                public Void run() throws Exception{
                    Config config = new Config();
                    config.putAll(cdrTopology.props); // allow all properties to be accessible across all topology components through the map
                    config.setDebug(Boolean.parseBoolean(props.get(GenericConstants.DEBUG_MODE)));
                    config.setMessageTimeoutSecs(Integer.parseInt(cdrTopology.props.get(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS)));
                    //config.setMaxSpoutPending(100);
                    config.setStatsSampleRate(1.0);//Decrease performance but shows exact figures in Storm UI
                    if(args.length>1){
                        //config.setNumWorkers(3);
                        StormSubmitter.submitTopology(args[1], config, cdrTopology.buildTopology(cdrTopology.props));
                    }
                    else{
                        LocalCluster cluster = new LocalCluster();
                        cluster.submitTopology("my-topology", config, cdrTopology.buildTopology(cdrTopology.props));
                    }

                    return null;
                }
            });

        }catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }

}