package com.wicom.spark.scala

import org.apache.spark._
import org.apache.spark.sql
import org.apache.spark.sql.Row
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

object ConversationBuilder {
  def main(args: Array[String]) {
    
    val parser = new scopt.OptionParser[Config]("scopt") {
      head("scopt", "3.x")
      opt[String]("country") required() action { (x, c) =>
        c.copy(country = x) } text("Country name to process")
     
      help("help") text("prints this usage text")
    }
  
    parser.parse(args, Config()) match {
      case Some(config) =>
        // do stuff
        print("\n")
        print(config.country)
      case None =>
        parser.showUsage
        throw new IllegalStateException("No parameter's passed. Check usage section")
    }
    
    val conf = new SparkConf().setAppName("ScalaConversationBuilder_")
    //conf.setMaster("local")
    val sc = new SparkContext(conf)
    
    val test = Array(1,3,4,5)
    
    val driverName = "org.apache.hive.jdbc.HiveDriver"
    Class.forName(driverName)
    
    sc.parallelize(test,2).foreachPartition {
      x => {
        val driverName = "org.apache.hive.jdbc.HiveDriver"
        Class.forName(driverName)
        val con = DriverManager.getConnection("jdbc:hive2://nex-hdp-14:10000/wrd10_socialmedia", "sm_user", "hdp-08/home");
        val stmt = con.createStatement();
        x.foreach {y => stmt.execute(s"insert into table test_jdbc_insert values ('$y')")}
      }
    }
  }
}