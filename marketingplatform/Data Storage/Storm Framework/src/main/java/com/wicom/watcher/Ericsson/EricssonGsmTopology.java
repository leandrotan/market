package com.wicom.watcher.Ericsson;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.wicom.watcher.AsnParser.AsnToXmlParser;
import com.wicom.watcher.DB.VerticaCopyWriter;

import com.wicom.watcher.Ericsson.StAXparser.EricssonGsmXMLParser;
import com.wicom.watcher.Ericsson.StAXparser.EricssonGsmXMLParser_withMerger;
import com.wicom.watcher.Ericsson.StAXparser.factDBFields;
import com.wicom.watcher.GenericConstants;
import com.wicom.watcher.CDRTopology;
import com.wicom.watcher.SpoolDirectoryBatchSpout;
import com.wicom.watcher.hdfs.HDFSUpload;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.storm.hive.bolt.mapper.DelimitedRecordHiveMapper;
import org.apache.storm.hive.common.HiveOptions;
import storm.trident.Stream;
import storm.trident.TridentTopology;

import java.io.FileReader;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by albert.elkhoury on 4/15/2015.
 */
public class EricssonGsmTopology extends CDRTopology {
    public EricssonGsmTopology() throws IOException {
        this.topology=new TridentTopology();
        this.props.put(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY,"/home/sm_user/Storm Framework/Ericsson_Data");
        this.props.put(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY,"/home/sm_user/Storm Framework/Ericsson_Data/queued");
        this.props.put(GenericConstants.WATCHER_FILE_PROCESSED_DIRECTORY,"/home/sm_user/Storm Framework/Ericsson_Data/processed");
        this.props.put(GenericConstants.WATCHER_FILE_ERROR_DIRECTORY,"/home/sm_user/Storm Framework/Ericsson_Data/error");
        this.props.put(GenericConstants.WATCHER_FILE_EXTENSION,".*");
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
        this.props.put(GenericConstants.ASN_PARSER_PATH,"C:/asn2txt/bin/");
        this.props.put(GenericConstants.ASN_PARSER_SCHEMA_PATH,"/home/sm_user/Storm Framework/Ericsson_Data/Schema/3GPP_TS_32.104_V3.4.0_file_format.txt");
        this.props.put(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY,"/home/sm_user/Storm Framework/Ericsson_Data/queued/asnParsed/");
        this.props.put(GenericConstants.STAX_PARSER_OUTPUT_DIRECTORY,"/Transformer/incoming");
        this.props.put(GenericConstants.VERTICA_DRIVER,"com.vertica.jdbc.Driver");
        this.props.put(GenericConstants.VERTICA_SERVER,"10.104.5.28");
        this.props.put(GenericConstants.VERTICA_PORT,"5433");
        this.props.put(GenericConstants.VERTICA_DB,"verticadst");
        this.props.put(GenericConstants.VERTICA_USER,"alfxpldev");
        this.props.put(GenericConstants.VERTICA_PASS,"xpl123");
        this.props.put(GenericConstants.VERTICA_DB_FACT_TABLE,"storm_stg_fact");
        this.props.put(GenericConstants.TRANSFORMER_FILE_INCOMING_DIRECTORY,"D:/Data/Transformer/incoming/");
        this.props.put(GenericConstants.TRANSFORMER_FILE_QUEUED_DIRECTORY,"D:/Data/Transformer/enqueued/");
        this.props.put(GenericConstants.TRANSFORMER_FILE_PROCESSED_DIRECTORY,"D:/Data/Transformer/processed/");
        this.props.put(GenericConstants.TRANSFORMER_FILE_ERROR_DIRECTORY,"D:/Data/Transformer/error/");
        this.props.put(GenericConstants.TRANSFORMER_FILE_NETWORKANDDICTIONARY_POPULATE,"D:/Data/Transformer/populateNetworkAndDictionary.sql");
        this.props.put(GenericConstants.DB_TYPE,"hive");
        this.props.put(GenericConstants.HIVE_DB_TBL_COLUMNS,"1112");
        this.props.put(GenericConstants.HDFS_URI,"hdfs://nex-hdp-14.nexius.com:8020");
        this.props.put(GenericConstants.HDFS_DESTINATION_DIRECTORY,"/user/sm_user/testing/");
    }

    public EricssonGsmTopology(String propFile) throws IOException {
        this.topology=new TridentTopology();

        this.props = initializeProperties(propFile);
    }
    public static Map<String,String> initializeProperties(String filePath) throws IOException {
        Map<String,String> props = new HashMap<String, String>();
        Properties prop= new Properties();
        FileReader propReader=new FileReader(filePath);
        prop.load(propReader);
        props.put(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY, prop.getProperty(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY));
        props.put(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY, prop.getProperty(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY));
        props.put(GenericConstants.WATCHER_FILE_EXTENSION,prop.getProperty(GenericConstants.WATCHER_FILE_EXTENSION));
        props.put(GenericConstants.WATCHER_FILE_PROCESSED_DIRECTORY,prop.getProperty(GenericConstants.WATCHER_FILE_PROCESSED_DIRECTORY));
        props.put(GenericConstants.WATCHER_FILE_ERROR_DIRECTORY,prop.getProperty(GenericConstants.WATCHER_FILE_ERROR_DIRECTORY));
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
        props.put(GenericConstants.ASN_PARSER_PATH,prop.getProperty(GenericConstants.ASN_PARSER_PATH));
        props.put(GenericConstants.ASN_PARSER_SCHEMA_PATH,prop.getProperty(GenericConstants.ASN_PARSER_SCHEMA_PATH));
        props.put(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY,prop.getProperty(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY));
        props.put(GenericConstants.STAX_PARSER_OUTPUT_DIRECTORY,prop.getProperty(GenericConstants.STAX_PARSER_OUTPUT_DIRECTORY));
        props.put(GenericConstants.VERTICA_DRIVER,prop.getProperty(GenericConstants.VERTICA_DRIVER));
        props.put(GenericConstants.VERTICA_SERVER,prop.getProperty(GenericConstants.VERTICA_SERVER));
        props.put(GenericConstants.VERTICA_PORT,prop.getProperty(GenericConstants.VERTICA_PORT));
        props.put(GenericConstants.VERTICA_DB,prop.getProperty(GenericConstants.VERTICA_DB));
        props.put(GenericConstants.VERTICA_USER,prop.getProperty(GenericConstants.VERTICA_USER));
        props.put(GenericConstants.VERTICA_PASS,prop.getProperty(GenericConstants.VERTICA_PASS));
        props.put(GenericConstants.VERTICA_DB_FACT_TABLE,prop.getProperty(GenericConstants.VERTICA_DB_FACT_TABLE));
        props.put(GenericConstants.TRANSFORMER_FILE_INCOMING_DIRECTORY,prop.getProperty(GenericConstants.TRANSFORMER_FILE_INCOMING_DIRECTORY));
        props.put(GenericConstants.TRANSFORMER_FILE_QUEUED_DIRECTORY,prop.getProperty(GenericConstants.TRANSFORMER_FILE_QUEUED_DIRECTORY));
        props.put(GenericConstants.TRANSFORMER_FILE_PROCESSED_DIRECTORY,prop.getProperty(GenericConstants.TRANSFORMER_FILE_PROCESSED_DIRECTORY));
        props.put(GenericConstants.TRANSFORMER_FILE_ERROR_DIRECTORY,prop.getProperty(GenericConstants.TRANSFORMER_FILE_ERROR_DIRECTORY));
        props.put(GenericConstants.TRANSFORMER_FILE_NETWORKANDDICTIONARY_POPULATE,prop.getProperty(GenericConstants.TRANSFORMER_FILE_NETWORKANDDICTIONARY_POPULATE));
        props.put(GenericConstants.DB_TYPE,prop.getProperty(GenericConstants.DB_TYPE));
        props.put(GenericConstants.HIVE_DB_TBL_COLUMNS,prop.getProperty(GenericConstants.HIVE_DB_TBL_COLUMNS));
        props.put(GenericConstants.HDFS_URI,prop.getProperty(GenericConstants.HDFS_URI));
        props.put(GenericConstants.HDFS_DESTINATION_DIRECTORY,prop.getProperty(GenericConstants.HDFS_DESTINATION_DIRECTORY));

        return props;
    }
    @Override
    public  StormTopology buildTopology(Map<String, String> props) throws IOException {
       if (props.get(GenericConstants.DB_TYPE).equalsIgnoreCase("vertica")) {
            LOGGER.warn("Database Type: {}", props.get(GenericConstants.DB_TYPE));


           Stream parserSpout = topology.newStream("listOfFilesSpout",
                    new SpoolDirectoryBatchSpout()).name("Spooling Spout").parallelismHint(1).shuffle();

            Stream sp1 = parserSpout
                    .shuffle()
                    .each(new Fields("filename", "auditsid"), new AsnToXmlParser(), new Fields("XMLfileName"))
                    .parallelismHint(8)
                    ;

            Stream sp2 = sp1
                    .shuffle()
                    .each(new Fields("XMLfileName"), new EricssonGsmXMLParser(), new Fields("xmlauditsid"))
                    .parallelismHint(8)
                    ;

           Stream sp3 = sp2
                   .shuffle()
                   .each(new Fields("XMLfileName", "xmlauditsid"), new HDFSUpload(), new Fields("csvFileName"))
                   .parallelismHint(8);

           Stream sp4 = sp3
                   .shuffle()
                   .each(new Fields("csvFileName", "xmlauditsid"), new VerticaCopyWriter(), new Fields())
                   .parallelismHint(8)
                   //.each(new Fields("filename", "auditsid","xmlfilename","csvField"),new PrintFunction(),new Fields());
                   ;

// DELETE The below
           /*
           Stream sp4 = sp2
                   .shuffle()
                   .each(new Fields("XMLfileName", "xmlauditsid"), new VerticaCopyWriter(), new Fields())
                   .parallelismHint(1)
                   //.each(new Fields("filename", "auditsid","xmlfilename","csvField"),new PrintFunction(),new Fields());
                   ;
            Stream sp3 = sp2
                    .shuffle()
                    .each(new Fields("XMLfileName", "xmlauditsid"), new HDFSUpload(), new Fields("csvFileName"))
                    .parallelismHint(8);

          Stream sp4 = sp3
                    .shuffle()
                    .each(new Fields("csvFileName", "xmlauditsid"), new VerticaCopyWriter(), new Fields())
                    .parallelismHint(8)
                    //.each(new Fields("filename", "auditsid","xmlfilename","csvField"),new PrintFunction(),new Fields());
                    ;
*/
        }

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
        List<String> partNames = factDBFields.getPartNames();
        // Fields for possible column data
        List<String> colNames = factDBFields.getColNames();

        // Record Writer configuration
        LOGGER.warn("Initialising Delimited Record Hive Mapper");
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper()
                .withColumnFields(new Fields(colNames))
                .withPartitionFields(new Fields(partNames));

        LOGGER.warn("Delimited Record Hive Mapper Initialised");

        hvOpt = new HiveOptions(metaStoreURI, dbName, tblName, mapper)
                .withTxnsPerBatch(txnsPerBatch)
                .withBatchSize(batchSize)
                .withIdleTimeout(idleTimeout);
        LOGGER.warn("Setting Hive Options");
        return hvOpt;
    }

    public static void main(final String[] args){

        final EricssonGsmTopology egsmTopology;

        try{
            if(args.length>=1)
            {
                LOGGER.warn("Topology Started reading from properties files {} passed as argument", args[0]);
                egsmTopology = new EricssonGsmTopology(args[0]);
            }
            else
            {
                LOGGER.warn("Topology Started reading default properties, no properties file passed");
                egsmTopology = new EricssonGsmTopology();
            }

            LOGGER.warn("Starting Directory Watching on: " + egsmTopology.props.get(GenericConstants.WATCHER_FILE_INCOMING_DIRECTORY) + " for files filtered with \""+egsmTopology.props.get(GenericConstants.WATCHER_FILE_EXTENSION) +"\"");
            /*Launch the topology as sm_user */
            UserGroupInformation ugi= UserGroupInformation.createRemoteUser("sm_user");
            ugi.doAs(new PrivilegedExceptionAction<Void>(){
                public Void run() throws Exception{
                    Config config = new Config();
                    config.putAll(egsmTopology.props); // allow all properties to be accessible across all topology components through the map
                    config.setDebug(Boolean.parseBoolean(props.get(GenericConstants.DEBUG_MODE)));
                    config.setMessageTimeoutSecs(Integer.parseInt(egsmTopology.props.get(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS)));
                    config.setNumWorkers(4);

                    //config.setMaxSpoutPending(100);
                    //config.setStatsSampleRate(1.0);//Decrease performance but shows exact figures in Storm UI
                    if(args.length>1){
                        //config.setNumWorkers(3);
                        StormSubmitter.submitTopology(args[1], config, egsmTopology.buildTopology(egsmTopology.props));
                    }
                    else{
                        LocalCluster cluster = new LocalCluster();
                        cluster.submitTopology("EricssonGsmParserTopology", config, egsmTopology.buildTopology(egsmTopology.props));
                    }

                    return null;
                }
            });

        }catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
