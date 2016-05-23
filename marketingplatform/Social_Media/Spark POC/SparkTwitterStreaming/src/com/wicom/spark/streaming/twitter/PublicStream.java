package com.wicom.spark.streaming.twitter;

import java.util.ArrayList;

import org.apache.hive.hcatalog.streaming.ConnectionError;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.apache.hive.hcatalog.streaming.StreamingConnection;
import org.apache.hive.hcatalog.streaming.StreamingException;
import org.apache.hive.hcatalog.streaming.StrictJsonWriter;
import org.apache.hive.hcatalog.streaming.TransactionBatch;
import org.apache.spark.*;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.twitter.TwitterUtils;

import twitter4j.Status;
import twitter4j.json.DataObjectFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicStream {
	private static final Logger logger = LoggerFactory.getLogger(PublicStream.class);
	public static void main(String[] args) throws ConnectionError, StreamingException, InterruptedException, ClassNotFoundException { 
		
		class ProcessRDD implements Function<JavaRDD<Status>,Void> {
			private static final long serialVersionUID = 1906777388572495520L;
			String dbName = "wrd10_socialmedia";
		    String tblName = "twitter_publicstream_spark_orc";

			public Void call(JavaRDD<Status> s) throws Exception {
				s.foreach(new VoidFunction<Status>() {
					private static final long serialVersionUID = 765653272923202521L;
					public void call(Status x) throws StreamingException, InterruptedException, ClassNotFoundException {
						ArrayList<String> partitionVals = new ArrayList<String>(2);
						partitionVals.add("2015-02-23");
						logger.info("Full JSON: "+DataObjectFactory.getRawJSON(x));
						HiveEndPoint hiveEP = new HiveEndPoint("thrift://nex-hdp-14.nexius.com:9083", dbName, tblName, partitionVals);
						StreamingConnection connection = hiveEP.newConnection(true);
						StrictJsonWriter writer2 = new StrictJsonWriter(hiveEP);
						TransactionBatch txnBatch = connection.fetchTransactionBatch(10, writer2);
						txnBatch.beginNextTransaction();
						txnBatch.write(DataObjectFactory.getRawJSON(x).getBytes());
						txnBatch.commit();
						txnBatch.close();
					}
				});
				return null;
			}
	    }
		
		logger.info("Start process");
		SparkConf conf = new SparkConf().setAppName("SparkTwitterStream").setMaster("local[2]");
		JavaStreamingContext ssc = new JavaStreamingContext(conf, Durations.seconds(5));
		
		System.setProperty("twitter4j.oauth.consumerKey", "eBd5qpBfUc7zblIgcUnNDVuEZ");
	    System.setProperty("twitter4j.oauth.consumerSecret", "4zdRMGjykt79Ervelcwybcj3CqMRMXf022bJT3Ciq3tTCGaDgA");
	    System.setProperty("twitter4j.oauth.accessToken", "2903950749-wMdueUqxFnV3vFSTppyHUeoIEQTlKkd0xBKOmEq");
	    System.setProperty("twitter4j.oauth.accessTokenSecret", "6KiR2NE6EOloYdqkMAjebpT2mdyHaESl8AZGg0E4le6wv");
	    
	    JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(ssc);
	    stream.foreachRDD(new ProcessRDD());
	    
		ssc.start();
	    ssc.awaitTermination();
	}
}
