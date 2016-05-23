package com.wicom.storm.socialmedia.twitter.utils;

/**
 * Created by Charbel Hobeika on 3/20/2015.
 */
public class GenericConstants {

    public static final String TOPOLOGY_PROCESS_ID = "topology.process.id";
    public static final String TOPOLOGY_MESSAGE_TIMEOUT_SECS = "topology.message.timeout.secs";
    public static final String TOPOLOGY_PARALLELISM_READER_HINT = "topology.parallelism.readerHint";
    public static final String TOPOLOGY_PARALLELISM_LOADER_HINT = "topology.parallelism.dbLoaderHint";
    public static final String TOPOLOGY_PARALLELISM_PROCESSING_HINT = "topology.parallelism.processingHint";
    public static final String HIVE_DB_METASTOREURI = "hive.db.metaStoreURI";
    public static final String HIVE_DB_DBNAME = "hive.db.dbName";
    public static final String HIVE_DB_TBLNAME = "hive.db.tblName";
    public static final String HIVE_DB_BATCH_SIZE = "hive.db.batch.size";
    public static final String HIVE_DB_TX_PER_BATCH = "hive.db.tx.per.batch";
    public static final String HIVE_DB_IDLE_TIMEOUT = "hive.db.idle.timeout";
    public static final String HIVE_DB_TBL_COLUMNS = "hive.db.tbl.columns";
    public static final String DEBUG_MODE = "debug.mode";

    public static final String TWITTER4J_OAUTH_CONSUMERKEY = "Twitter4j.OAuth.consumerKey";
    public static final String TWITTER4J_OAUTH_CONSUMERSECRET = "Twitter4j.OAuth.consumerSecret";
    public static final String TWITTER4J_OAUTH_ACCESSTOKEN = "Twitter4j.OAuth.accessToken";
    public static final String TWITTER4J_OAUTH_ACCESSTOKENSECRET = "Twitter4j.OAuth.accessTokenSecret";
    public static final String TWITTER_FILTER_KEYWORDS = "Twitter.filter.keywords";
    public static final String TWITTER_FILTER_LANGUAGES = "Twitter.filter.languages";
    public static final String TWITTER_FILTER_LOCATIONS = "Twitter.filter.locations";
    public static final String TWITTER_COUNTRY = "Twitter.country";
    public static final String HDFS_TWEETS_DIRECTORY = "hdfs.tweets.directory";
    public static final String HDFS_TWEETS_FILENAME = "hdfs.tweets.filename";
    public static final String HDFS_TWEETS_BATCH = "hdfs.tweets.batch";
}
