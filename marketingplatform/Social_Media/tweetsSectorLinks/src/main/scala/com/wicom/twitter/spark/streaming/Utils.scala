package com.wicom.twitter.spark.streaming

import java.io.{FileInputStream, InputStreamReader, BufferedReader, File}
import java.nio.charset.StandardCharsets
import java.nio.file.{Paths, Path, Files}
import org.apache.spark.SparkContext

import scala.collection.JavaConversions._
import scala.io.Source

import org.apache.commons.cli.{Options, ParseException, PosixParser}
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
object Utils {

  val numFeatures = 1000


  val CONSUMER_KEY = "consumerKey"
  val CONSUMER_SECRET = "consumerSecret"
  val ACCESS_TOKEN = "accessToken"
  val ACCESS_TOKEN_SECRET = "accessTokenSecret"

  val THE_OPTIONS = {
    val options = new Options()
    options.addOption(CONSUMER_KEY, true, "Twitter OAuth Consumer Key")
    options.addOption(CONSUMER_SECRET, true, "Twitter OAuth Consumer Secret")
    options.addOption(ACCESS_TOKEN, true, "Twitter OAuth Access Token")
    options.addOption(ACCESS_TOKEN_SECRET, true, "Twitter OAuth Access Token Secret")
    options
  }

  def parseCommandLineWithTwitterCredentials(args: Array[String]) = {
    val parser = new PosixParser
    try {
      val cl = parser.parse(THE_OPTIONS, args)
      System.setProperty("twitter4j.oauth.consumerKey", cl.getOptionValue(CONSUMER_KEY))
      System.setProperty("twitter4j.oauth.consumerSecret", cl.getOptionValue(CONSUMER_SECRET))
      System.setProperty("twitter4j.oauth.accessToken", cl.getOptionValue(ACCESS_TOKEN))
      System.setProperty("twitter4j.oauth.accessTokenSecret", cl.getOptionValue(ACCESS_TOKEN_SECRET))
      cl.getArgList.toArray
    } catch {
      case e: ParseException =>
        System.err.println("Parsing failed.  Reason: " + e.getMessage)
        System.exit(1)
    }
  }

  def getAuth = {
    Some(new OAuthAuthorization(new ConfigurationBuilder().build()))
  }



  object IntParam {
    def unapply(str: String): Option[Int] = {
      try {
        Some(str.toInt)
      } catch {
        case e: NumberFormatException => None
      }
    }
  }



  def readCSV(absPath:String, delimiter:String, sc: SparkContext): List[Array[String]] = {
    val lines = if (absPath.toUpperCase().startsWith("HDFS")) {
      sc.textFile(absPath).collect().toList
    } else {
      Files.readAllLines(Paths.get(absPath), StandardCharsets.ISO_8859_1).toList
    }
    lines map {
        // String#split() takes a regex, thus escaping.
        _.split( """\""" + delimiter).toList.toArray
    }
  }

}
