package com.wicom.twitter.spark.batch

import com.wicom.utils.Logs
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.hive.HiveContext


/**
 * Created by santiago on 4/1/15.
 */
object Link extends App with Logs {

  val conf = new SparkConf().setAppName("Link tweet with subscriber")
  val sc = new SparkContext(conf)
  if (args.length != 2)
    System.exit(1)
  val dbName = args(0)
  val domainName = args(1).toUpperCase
  val hiveContext = new HiveContext(sc)

  hiveContext.udf.register("extractSectorId", (cgi: String) => extractSectorId(cgi))
  hiveContext.udf.register("getSentiment", (text: String) => getSentiment(text))
  hiveContext.udf.register("getCategory", (text: String) => getCategory(text))



  val rows = hiveContext.sql(s"""
                                         INSERT INTO TABLE $dbName.linked_tweets_orc
                                         SELECT createdAt,
                                                lat,
                                                long,
                                                text,
                                                getCategory(text),
                                                getSentiment(text),
                                                sectorId,
                                                msisdn
                                          FROM $dbName.unlinked_tweets_orc join $dbName.test_fact_orc_raw_prob
                                         on createdAt >= start_time and createdAt <= end_time and
                                         extractSectorId(serving_cell_cgi) = sectorId and upper(domain_name) = '$domainName'

                             """)


  def extractSectorId(cgi: String): String = {
    val parts = cgi.split("-")
    if (parts.length == 4) {
      parts(3)
    } else {
      ""
    }
  }

  def getSentiment(text: String): String = {
    //TODO - call the algorithm here
    "Neutral"
  }

  def getCategory(text: String): String = {
    //TODO - call the algorithm here
    "Network"
  }

}