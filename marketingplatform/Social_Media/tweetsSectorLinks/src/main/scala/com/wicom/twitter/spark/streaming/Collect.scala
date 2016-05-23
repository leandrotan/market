package com.wicom.twitter.spark.streaming


import java.text.SimpleDateFormat


import com.google.gson.Gson
import com.vividsolutions.jts.geom.{Coordinate, Geometry, GeometryFactory}
import com.vividsolutions.jts.io.WKTReader
import org.apache.hive.hcatalog.streaming.{StrictJsonWriter, HiveEndPoint}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import com.wicom.utils._
/**
 * Collect at least the specified number of tweets into json text files.
 */
object Collect extends Logs{
  private var numTweetsCollected = 0L
  private var partNum = 0
  private var gson = new Gson()

  def main(args: Array[String]) {

    // Process program arguments and set properties
    if (args.length < 5) {
      System.err.println("Usage: " + this.getClass.getSimpleName +
        "<geolocFile> <intervalInSeconds> <partitionsEachInterval> <thrift> <db>")
      System.exit(1)
    }
    val Array(geolocFile, Utils.IntParam(intervalSecs), Utils.IntParam(partitionsEachInterval), pattern, thrift, db) =
      Utils.parseCommandLineWithTwitterCredentials(args)

    info("Initializing Streaming Spark Context...")
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(intervalSecs))

    //Get coverage areas
    info("Broadcasting coverage areas...")
    val coverageAreasLocal = Utils.readCSV(geolocFile.toString, ";", sc).map(array => CoverageArea(array(0), array(3)))
    val coverageAreas = sc.broadcast(coverageAreasLocal)

    val searchStr = pattern.toString
    info(s"Search string: $searchStr")

    info("Starting collecting tweets...")

    val tweetStream = TwitterUtils.createStream(ssc, Utils.getAuth, Array(searchStr))

      .map(status => {
      val geolocation = status.getGeoLocation
      val tweet = if (geolocation != null) {
        val createdAt = status.getCreatedAt
        val text = status.getText
        info(s"Got Tweet with geolocation created at: $createdAt - Text: $text")
        val (lat, long) = (geolocation.getLatitude, geolocation.getLongitude)
        val geometryFactory = new GeometryFactory()
        val geoPoint = geometryFactory.createPoint(new Coordinate(lat, long))
        coverageAreas.value.find(ca => ca.contains(geoPoint)) match {
          case Some(ca) =>
            val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            info("Tweet matched sector")
            Some(new Tweet(df.format(status.getCreatedAt), lat, long, status.getText, ca.sectorId))
          case None => None
        }
      }
      else {
        None
      }
      tweet
    })
      .filter {
      case Some(_) => true
      case None => false
    }
      .map(tweet => {
      gson.toJson(tweet.get)
    })


    tweetStream.foreachRDD((rdd, time) => {
      rdd.foreach(tweetJsonStr => {
        val dbName = db.toString
        val tableName = "unlinked_tweets_orc"
        println(s"Going to insert tweet: $tweetJsonStr")
        val hiveEndpoint = new HiveEndPoint(thrift.toString, dbName, tableName, null)
        val connection = hiveEndpoint.newConnection(true)
        val jsonWriter = new StrictJsonWriter(hiveEndpoint)
        val txBatch = connection.fetchTransactionBatch(10, jsonWriter)
        txBatch.beginNextTransaction()
        txBatch.write(tweetJsonStr.getBytes)
        txBatch.commit()
        txBatch.close() //TODO - WHAT ABOUT EXCEPTION HANDLING - WHAT IF HIVE SERVER GOES DOWN
        connection.close()
        println("Tweet inserted")

      })
    })

    ssc.start()
    ssc.awaitTermination()
  }



}

case class CoverageArea(wtk: String, sectorId: String) {
  val geometry: Option[Geometry] =
    try {
      val geometryFactory = new GeometryFactory
      val reader = new WKTReader(geometryFactory)
      val geo = reader.read(wtk.replace("\"",""))
      Some(geo)
    }
    catch {
      case _ : Throwable => None
    }

  def contains(geometry2: Geometry): Boolean = {
    geometry match {
      case None => false
      case Some(g) => g.contains(geometry2)
    }
  }
}


case class Tweet(createdat: String, lat: Double, long: Double, text: String, sectorid: String)

