package com.wicom.spark.scala

import org.apache.spark._
import org.apache.spark.sql
import org.apache.spark.sql.Row
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._
import org.apache.spark.storage.StorageLevel
import org.scala_tools.time.Imports._
import java.text.SimpleDateFormat

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

/**
 * @author Mykhail Martsynyuk
 */

case class Config(country: String = "", kwargs: Map[String,String] = Map())

object TwitterConversationBuilder {
  def concat(ss: Any*) = ss filter (_.toString().nonEmpty) mkString "\t"
  
  def main(args: Array[String]) {
    
    val parser = new scopt.OptionParser[Config]("scopt") {
      head("scopt", "3.x")
      opt[String]("country") required() action { (x, c) =>
        c.copy(country = x) } text("Country name to process")
      help("help") text("prints this usage text")
    }
  
    parser.parse(args, Config()) match {
      case Some(config) =>
        buildTwitterConversations(config.country)
      case None =>
        parser.showUsage
        throw new IllegalStateException("No parameters passed. Check usage section")
    }    
  }
  
  def buildTwitterConversations(country: String) {
    val today = DateTime.now
    val dateFormat = new SimpleDateFormat("yyyyMMdd")
    val today_str = dateFormat.format(today.toDate())
    
    val conf = new SparkConf().setAppName("ScalaTwitterConversationBuilder_"+today_str)
    //conf.setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)
    
    val toplevel = sqlContext.sql(s"""
  select distinct tt.id,tt.in_reply_to_status_id from 
    (select t.id,t.in_reply_to_status_id
      from wrd10_socialmedia.fact_orc_raw_tweets t,
      wrd10_socialmedia.fact_orc_raw_tweets t1
     where t.in_reply_to_status_id is null
       and t.id = t1.in_reply_to_status_id
       and t.country = '$country'
       and t1.country = '$country'
     union all
     select t2.id,t2.in_reply_to_status_id
      from wrd10_socialmedia.fact_orc_raw_twitter_userstream t2,
      wrd10_socialmedia.fact_orc_raw_twitter_userstream t3
     where t2.in_reply_to_status_id is null and 
           (t2.id = t3.in_reply_to_status_id and t2.country = t3.country)
       and t2.country = '$country'
       and t3.country = '$country'
     union all
     select t4.id,t4.in_reply_to_status_id
      from wrd10_socialmedia.fact_orc_raw_twitter_userstream t4,
      wrd10_socialmedia.fact_orc_raw_tweets t5
     where t4.in_reply_to_status_id is null and  
            (t4.id=t5.in_reply_to_status_id and t5.country = t4.country)
       and t5.country = '$country'
       and t4.country = '$country'
    ) tt
""").persist(StorageLevel.MEMORY_AND_DISK)
    val replies = sqlContext.sql(s"""
  select distinct tt.id,tt.in_reply_to_status_id,tt.created_at,tt.user_name,tt.text,tt.country from
    (select t.id,t.in_reply_to_status_id,t.text,t.user_name,t.created_at,t.country
      from wrd10_socialmedia.fact_orc_raw_tweets t 
     where t.in_reply_to_status_id is not null
       and t.country = '$country'
     union all
     select t1.id,t1.in_reply_to_status_id,t1.created_at,t1.user_name,t1.text,t1.country
      from wrd10_socialmedia.fact_orc_raw_twitter_userstream t1
     where t1.in_reply_to_status_id is not null
       and t1.country = '$country'
  ) tt
""").persist(StorageLevel.MEMORY_AND_DISK)

    val repliesReverted = replies.map { row => (row(1), row) }
    repliesReverted.persist(StorageLevel.MEMORY_AND_DISK)
    val Level1 = repliesReverted.join(toplevel.map(row => (row(0), row)))
    var nLevel = Level1.mapValues { case (child, top) => (child, (top, top)) }.coalesce(10).persist(StorageLevel.MEMORY_AND_DISK)
    var Level = 1
    
    val driverName = "org.apache.hive.jdbc.HiveDriver"
    
    nLevel.coalesce(1).foreachPartition {
      x => {
        Class.forName(driverName);
        val con = DriverManager.getConnection("jdbc:hive2://nex-hdp-14:10000/wrd10_socialmedia", "sm_user", "hdp-08/home");
        val stmt = con.createStatement();
        x.foreach {
          case(key,(child, (parent, top))) => {
            val stext = s"""
              insert into table fact_orc_twitter_conversations partition (country='$country') 
              values (${child(0)},'${child(3)}','${child(4)}',
                     '${child(2)}',${child(1)},${top(0)},$Level)
            """
            stmt.execute(stext) }
        }
        con.close()
      }
    }

    var level_count = nLevel.count()

    while (level_count > 0) {
      //set new child.id as a RDD key to join with replies and get new level rdd
      var topReverted = nLevel.map { case (id, (child, (parent, top))) => (child(0), (child, top)) }
      nLevel = repliesReverted.join(topReverted).persist(StorageLevel.MEMORY_AND_DISK)
      level_count = nLevel.count()
      if (level_count != 0) {
        Level = Level + 1
        nLevel.coalesce(1).foreachPartition {
          x => {
            Class.forName(driverName);
            val con = DriverManager.getConnection("jdbc:hive2://nex-hdp-14:10000/wrd10_socialmedia", "sm_user", "hdp-08/home");
            val stmt = con.createStatement();
            x.foreach {
              case(key,(child, (parent, top))) => 
                stmt.execute(s""""
                  insert into table fact_orc_twitter_conversations partition (country='$country') 
                  values (${child(0)},'${child(3)}','${child(4)}',
                         '${child(2)}',${child(1)},${top(0)},$Level)
                """)
            }
            con.close()
          }
        }
      }
    }
    sc.stop()
  }
}