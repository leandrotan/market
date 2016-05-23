package com.wicom.storm.socialmedia.twitter;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import com.wicom.storm.socialmedia.twitter.bolts.HDFSBolt;
import com.wicom.storm.socialmedia.twitter.bolts.PrinterBolt;
import com.wicom.storm.socialmedia.twitter.spout.TwitterSpout;
import org.apache.hadoop.security.UserGroupInformation;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;

import com.wicom.storm.socialmedia.twitter.utils.GenericConstants;

import java.io.FileReader;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Charbel Hobeika on 5/8/2015.
 */
public class StormTwitterTopology {
    public static Map<String,String> props = new HashMap<>();
    public static TopologyBuilder builder;

    private static String TWITTER_SPOUT = "twitterSpout";
    private static String TWITTER_BOLT1 = "twitterBolt1";
    public static  String HIVE_BOLT_ID = "hive-twitter-bolt";
    private static String PRINTER_BOLT = "printerBolt1";

    /*DEFAULT CONSTRUCTOR if no properties file provided*/
    public StormTwitterTopology() throws IOException{
        builder = new TopologyBuilder();
        props.put(GenericConstants.TWITTER4J_OAUTH_CONSUMERKEY, "sSPYmbG0VR79ilJtliIMjni24");
        props.put(GenericConstants.TWITTER4J_OAUTH_CONSUMERSECRET, "ZliOArMtl1cGCk89XBmqhQK1bY91GnFr2VEH38v7RQ20RHJsKN");
        props.put(GenericConstants.TWITTER4J_OAUTH_ACCESSTOKEN, "229521597-26rG0MZdPuZ7ovjo8FRmPKPpvhbFkG3MXyCc5G9h");
        props.put(GenericConstants.TWITTER4J_OAUTH_ACCESSTOKENSECRET, "Q9IgW85tEwqhZUqwgkKMiBwYJPNdbSEA2Nh7I3YPcBdFA");
        props.put(GenericConstants.TWITTER_FILTER_KEYWORDS, "iphone,android");
        props.put(GenericConstants.TWITTER_FILTER_LANGUAGES, "en");
        props.put(GenericConstants.TWITTER_COUNTRY, "Egypt");
        props.put(GenericConstants.TWITTER_FILTER_LOCATIONS, "-126.562500, 30.448674,-61.171875, 44.087585");
        props.put(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS,"60");
        props.put(GenericConstants.DEBUG_MODE,"true");
        props.put(GenericConstants.HDFS_TWEETS_DIRECTORY,"hdfs:/user/sm_user/Hobeika/SocialMedia/Storm/");
        props.put(GenericConstants.HDFS_TWEETS_FILENAME,"StormPublicSTatusStream.txt");
        props.put(GenericConstants.HDFS_TWEETS_BATCH,"100");
/*
        props.put(GenericConstants.HIVE_DB_METASTOREURI,"thrift://nex-hdp-14.nexius.com:9083");
        props.put(GenericConstants.HIVE_DB_DBNAME,"wrd10_socialmedia");
        props.put(GenericConstants.HIVE_DB_TBLNAME,"test_twitter_publicstream_stormFramework");
        props.put(GenericConstants.HIVE_DB_BATCH_SIZE,"500");
        props.put(GenericConstants.HIVE_DB_TX_PER_BATCH,"2");
        props.put(GenericConstants.HIVE_DB_IDLE_TIMEOUT,"10");
        */
    }

    /*CONSTRUCTOR with properties file*/
    public StormTwitterTopology(String propFile) throws IOException{
        builder = new TopologyBuilder();
        props = initializeProperties(propFile);
    }

    public static Map<String,String> initializeProperties(String filePath) throws IOException {
        Map<String,String> props = new HashMap<String, String>();
        Properties prop= new Properties();
        FileReader propReader=new FileReader(filePath);
        prop.load(propReader);

        props.put(GenericConstants.TWITTER4J_OAUTH_CONSUMERKEY, prop.getProperty(GenericConstants.TWITTER4J_OAUTH_CONSUMERKEY));
        props.put(GenericConstants.TWITTER4J_OAUTH_CONSUMERSECRET,prop.getProperty(GenericConstants.TWITTER4J_OAUTH_CONSUMERSECRET));
        props.put(GenericConstants.TWITTER4J_OAUTH_ACCESSTOKEN,prop.getProperty(GenericConstants.TWITTER4J_OAUTH_ACCESSTOKEN));
        props.put(GenericConstants.TWITTER4J_OAUTH_ACCESSTOKENSECRET,prop.getProperty(GenericConstants.TWITTER4J_OAUTH_ACCESSTOKENSECRET));
        props.put(GenericConstants.TWITTER_FILTER_KEYWORDS,prop.getProperty(GenericConstants.TWITTER_FILTER_KEYWORDS));
        props.put(GenericConstants.TWITTER_FILTER_LOCATIONS,prop.getProperty(GenericConstants.TWITTER_FILTER_LOCATIONS));
        props.put(GenericConstants.TWITTER_FILTER_LANGUAGES,prop.getProperty(GenericConstants.TWITTER_FILTER_LANGUAGES));
        props.put(GenericConstants.TWITTER_COUNTRY,prop.getProperty(GenericConstants.TWITTER_COUNTRY));

        props.put(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS,prop.getProperty(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS));
        props.put(GenericConstants.DEBUG_MODE,prop.getProperty(GenericConstants.DEBUG_MODE));

        props.put(GenericConstants.HDFS_TWEETS_DIRECTORY,prop.getProperty(GenericConstants.HDFS_TWEETS_DIRECTORY));
        props.put(GenericConstants.HDFS_TWEETS_FILENAME,prop.getProperty(GenericConstants.HDFS_TWEETS_FILENAME));
        props.put(GenericConstants.HDFS_TWEETS_BATCH,prop.getProperty(GenericConstants.HDFS_TWEETS_BATCH));
/*
        props.put(GenericConstants.HIVE_DB_METASTOREURI, prop.getProperty(GenericConstants.HIVE_DB_METASTOREURI));
        props.put(GenericConstants.HIVE_DB_DBNAME, prop.getProperty(GenericConstants.HIVE_DB_DBNAME));
        props.put(GenericConstants.HIVE_DB_TBLNAME, prop.getProperty(GenericConstants.HIVE_DB_TBLNAME));
        props.put(GenericConstants.HIVE_DB_BATCH_SIZE, prop.getProperty(GenericConstants.HIVE_DB_BATCH_SIZE));
        props.put(GenericConstants.HIVE_DB_TX_PER_BATCH, prop.getProperty(GenericConstants.HIVE_DB_TX_PER_BATCH));
        props.put(GenericConstants.HIVE_DB_IDLE_TIMEOUT, prop.getProperty(GenericConstants.HIVE_DB_IDLE_TIMEOUT));
*/
        return props;
    }

    public StormTopology buildTopology(Map<String, String> props) throws IOException {

        /*Adding Hive Configuration and state*/
        //HiveOptions hiveOptions;
       // hiveOptions = initializeHiveOptions(props);

        builder.setSpout(TWITTER_SPOUT, new TwitterSpout());
        //builder.setBolt(PRINTER_BOLT, new PrinterBolt()).shuffleGrouping(TWITTER_SPOUT);

        builder.setBolt(HIVE_BOLT_ID, new HDFSBolt()).shuffleGrouping(TWITTER_SPOUT);



        return builder.createTopology();
    }
/*
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
        String[] partNames = {"country","day_key"};
        // Fields for possible column data
        String[] colNames = {"coordinates","retweeted","source","entities","favorite_count","in_reply_to_status_id_str","geo","id_str","in_reply_to_user_id","timestamp_ms","truncated","text","retweet_count","id","in_reply_to_status_id","possibly_sensitive","filter_level","created_at","place","favorited","lang","contributors","in_reply_to_screen_name","in_reply_to_user_id_str","user"};
        //String[] colNames = {"lang"};
        // Record Writer configuration
        JsonRecordHiveMapper mapper = new JsonRecordHiveMapper()
                .withColumnFields(new Fields(colNames))
                .withPartitionFields(new Fields(partNames));

        hvOpt = new HiveOptions(metaStoreURI, dbName, tblName, mapper)
                .withTxnsPerBatch(txnsPerBatch)
                .withBatchSize(batchSize)
                .withIdleTimeout(idleTimeout);

        return hvOpt;
    }
*/

    public static void main(final String[] args) throws Exception {
        final StormTwitterTopology topology;
        try{
            if(args.length>=1)
            {
                System.out.println("Topology Started reading from properties files passed as argument");
                topology = new StormTwitterTopology(args[0]);
            }
            else
            {
                System.out.println("Topology Started reading default properties, no properties file passed");
                topology = new StormTwitterTopology();
            }

            UserGroupInformation ugi= UserGroupInformation.createRemoteUser("sm_user");
            ugi.doAs(new PrivilegedExceptionAction<Void>(){
                public Void run() throws Exception{
                    Config config = new Config();
                    config.putAll(props); // allow all properties to be accessible across all topology components through the map
                    config.setDebug(Boolean.parseBoolean(props.get(GenericConstants.DEBUG_MODE)));
                    config.setMessageTimeoutSecs(Integer.parseInt(props.get(GenericConstants.TOPOLOGY_MESSAGE_TIMEOUT_SECS)));
                    //config.setMaxSpoutPending(100);
                    //config.setStatsSampleRate(1.0);//Decrease performance but shows exact figures in Storm UI
                    if(args.length>1){
                        StormSubmitter.submitTopology(args[1], config, topology.buildTopology(topology.props));
                    }
                    else{
                        LocalCluster cluster = new LocalCluster();
                        cluster.submitTopology("twitter-fun", config, topology.buildTopology(topology.props));
                    }

                    return null;
                }
            });
    }catch(Exception e){
            System.out.println(e.getMessage());
             e.printStackTrace();
        }
    }
}

