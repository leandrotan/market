/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wicom.storm.streaming.twitter;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.FailedException;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.storm.guava.collect.ImmutableList;
import org.apache.storm.hive.bolt.HiveBolt;
import org.apache.storm.hive.bolt.mapper.JsonRecordHiveMapper;
import org.apache.storm.hive.common.HiveOptions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import scala.Int;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

import java.io.UnsupportedEncodingException;
import java.security.PrivilegedExceptionAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TwitterHiveTopology {

    public static final String KAFKA_SPOUT_ID = "kafka-spout";
    public static final String TWEET_BOLT_ID = "tweet-bolt";
    public static final String HIVE_BOLT_ID = "hive-twitter-bolt";


        public static void main(String... args) {


        String kafkaTopic = "Twitter.live";

        SpoutConfig spoutConfig = new SpoutConfig(new ZkHosts("nex-hdp-13.nexius.com:2181,nex-hdp-14.nexius.com:2181"),kafkaTopic, "/kafka_storm", "StormSpout");
        spoutConfig.useStartOffsetTimeIfOffsetOutOfRange = true;
        spoutConfig.startOffsetTime = System.currentTimeMillis();
        spoutConfig.zkServers= ImmutableList.of("nex-hdp-13.nexius.com,nex-hdp-14.nexius.com");
        spoutConfig.zkPort=2181;
        spoutConfig.forceFromStart=false;


        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

        // Hive connection configuration
        String metaStoreURI = "thrift://nex-hdp-14.nexius.com:9083";
        String dbName = "wrd10_socialmedia";
        String tblName = "twitter_publicstream_storm";
        // Fields for possible partition
        String[] partNames = {"day_key"};
        // Fields for possible column data
        String[] colNames = {"coordinates","retweeted","source","matched_keywords","entities","favorite_count","in_reply_to_status_id_str","geo","id_str","in_reply_to_user_id","timestamp_ms","truncated","text","retweet_count","retweeted_status","id","in_reply_to_status_id","possibly_sensitive","filter_level","created_at","place","favorited","lang","contributors","in_reply_to_screen_name","in_reply_to_user_id_str","user","sentiment_score","entity","entity_subject"};
        // Record Writer configuration
        JsonRecordHiveMapper mapper = new JsonRecordHiveMapper()
                .withColumnFields(new Fields(colNames))
                .withPartitionFields(new Fields(partNames));

        HiveOptions hiveOptions;
        hiveOptions = new HiveOptions(metaStoreURI, dbName, tblName, mapper)
                .withTxnsPerBatch(2)
                .withBatchSize(100)
                .withIdleTimeout(10);

        final TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(KAFKA_SPOUT_ID, kafkaSpout);
        builder.setBolt(TWEET_BOLT_ID, new TweetBolt()).shuffleGrouping(KAFKA_SPOUT_ID);
        builder.setBolt(HIVE_BOLT_ID, new HiveBolt(hiveOptions)).shuffleGrouping(TWEET_BOLT_ID);
       // builder.setBolt(HIVE_BOLT_ID, new SimpleBolt()).shuffleGrouping(TWEET_BOLT_ID);


        final String topologyName = "StormHiveStreamingTopo";
        final Config config = new Config();
        config.setNumWorkers(1);
        config.setMessageTimeoutSecs(60);
        config.setDebug(false);//Set to true when debugging


        try{
            UserGroupInformation ugi= UserGroupInformation.createRemoteUser("sm_user");
            //ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.SIMPLE);
            ugi.doAs(new PrivilegedExceptionAction<Void>(){
                public Void run() throws Exception{
                    LocalCluster lc = new LocalCluster();
                    lc.submitTopology(topologyName,config,builder.createTopology());

                    return null;
                }
            });

        }catch(Exception e){
            System.out.println(e.getMessage().toString());
        }


    }

    public static class TweetBolt extends BaseBasicBolt {

        @Override
        public void declareOutputFields(OutputFieldsDeclarer ofDeclarer) {
            ofDeclarer.declare(new Fields("coordinates","retweeted","source","matched_keywords","entities","favorite_count","in_reply_to_status_id_str","geo","id_str","in_reply_to_user_id","timestamp_ms","truncated","text","retweet_count","retweeted_status","id","in_reply_to_status_id","possibly_sensitive","filter_level","created_at","place","favorited","lang","contributors","in_reply_to_screen_name","in_reply_to_user_id_str","user","sentiment_score","entity","entity_subject","day_key"));
        }

        @Override
        public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
            Fields fields = tuple.getFields();
            JSONParser jsonParser = new JSONParser();
            SentimentClassifier sentClassifier = new SentimentClassifier();
            String sent;

            try {
                String tweetJSON = new String((byte[]) tuple.getValueByField(fields.get(0)), "UTF-8");
                JSONObject tweet = (JSONObject) jsonParser.parse(tweetJSON);

                //Sentiment Analysis
                //getting sentiment_score, entity, entity_subject
                int sentiment_score = getPolarity(String.valueOf(tweet.get("text").toString()));
                String entity =  getEntity(String.valueOf(tweet.get("text").toString()));
                String entity_subject = getEntitySubject(String.valueOf(tweet.get("text").toString()));

                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                Long timestamp=Long.valueOf(tweet.get("timestamp_ms").toString().isEmpty()?"0":tweet.get("timestamp_ms").toString());
                String day_key=df.format(new Date(timestamp));

                Values values = new Values(tweet.get("coordinates"),tweet.get("retweeted"),tweet.get("source"),tweet.get("matched_keywords"),tweet.get("entities"),tweet.get("favorite_count"),tweet.get("in_reply_to_status_id_str"),tweet.get("geo"),tweet.get("id_str"),tweet.get("in_reply_to_user_id"),tweet.get("timestamp_ms"),tweet.get("truncated"),tweet.get("text"),tweet.get("retweet_count"),tweet.get("retweeted_status"),tweet.get("id"),tweet.get("in_reply_to_status_id"),tweet.get("possibly_sensitive"),tweet.get("filter_level"),tweet.get("created_at"),tweet.get("place"),tweet.get("favorited"),tweet.get("lang"),tweet.get("contributors"),tweet.get("in_reply_to_screen_name"),tweet.get("in_reply_to_user_id_str"),tweet.get("user"),sentiment_score,entity,entity_subject,day_key);
                outputCollector.emit(values);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TwitterHiveTopology.class.getName()).log(Level.SEVERE, null, ex);
                throw new FailedException(ex.toString());
            } catch (org.json.simple.parser.ParseException e) {
                throw new FailedException(e.toString());
            }
        }
    }
    public static class SimpleBolt extends BaseBasicBolt {

        @Override
        public void declareOutputFields(OutputFieldsDeclarer ofDeclarer) {
        }

        @Override
        public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
            Fields fields = tuple.getFields();
            List<String> listOfFields =fields.toList();
            for(String field:listOfFields) {
                System.out.println(field + "=" + tuple.getValueByField(field));
            }

        }
    }

    public static int getPolarity(String text)
    {
        SentimentClassifier sentClassifier = new SentimentClassifier();
        String sent;
        sent = sentClassifier.classify(text);
        if (sent.equalsIgnoreCase("Negative")) return 0;
        else if (sent.equalsIgnoreCase("Neutral")) return 1;
        else if (sent.equalsIgnoreCase("Positive")) return 2;
        return 1;
    }

    public static String getEntity(String text)
    {   DictionaryChunker dc = new DictionaryChunker();

        return dc.getEntity(DictionaryChunker.dictionaryChunkerTF,text);

    }
    public static String getEntitySubject(String text)
    {   DictionaryChunker dc = new DictionaryChunker();

        return dc.getEntityCategory(DictionaryChunker.dictionaryChunkerTF,text);

    }

}
