package com.wicom.spark.aggregation


import au.com.bytecode.opencsv.CSVWriter
import org.apache.log4j.Logger
import org.apache.spark.SparkContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.hadoop.util.Shell

//import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import java.io._
import java.util.UUID


import scala.collection.JavaConversions._
import com.vertica.jdbc._
import scala.collection.mutable.ListBuffer
import java.security.MessageDigest

/**
 * Created by Santiago on 3/9/2015.
 */
object CDRAggregation {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Spark CDR Aggregation")
    val sc = new SparkContext(conf)
    val hiveContext = new HiveContext(sc)

    val rows = hiveContext.sql(""" SELECT CALLINGPARTYNUMBER as MSISDN,

                      SUM(CASE
                      WHEN TERMINATIONREASON <> 1 THEN 1
                      ELSE 0
                      END) AS DROP_CALLS,
                      SUM(CASE
                      WHEN CHARGEPARTYINDICATOR=1 THEN CHARGEDURATION
                      ELSE 0
                      END) / 60 AS MOU,
                      SUM(CASE
                      WHEN CHARGEPARTYINDICATOR=1 THEN 1
                      ELSE 0
                      END) AS TOTAL_CALLS,
                      SUM(CASE WHEN CHARGEPARTYINDICATOR=1 AND CALLTYPE = 0
                      AND ONNETINDICATOR=0 THEN CHARGEDURATION
                      ELSE 0
                      END) / 60 AS ON_NET_MOU,
                      SUM(CASE WHEN CHARGEPARTYINDICATOR=1 AND CALLTYPE = 0
                      AND ONNETINDICATOR=1 THEN CHARGEDURATION
                      ELSE 0
                      END) / 60 AS OFF_NET_MOU

                      from wrd118_cdrdata.cdr_post_rec_orc

                      GROUP BY CALLINGPARTYNUMBER""")



    val batchSize = 1000
    rows.mapPartitions(_.grouped(batchSize)).foreach(batch=> {
      val newBatch = batch.map(row =>
        Array[String](row(0).toString, row.getLong(1).toString(), row.getDouble(2).toInt.toString,
          row.getLong(3).toString(), row.getDouble(4).toInt.toString(), row.getDouble(5).toInt.toString())
      )


      //generating a unique file name
      val fileName = UUID.randomUUID().toString

      val tempFile = new File(fileName)
      val writer = new CSVWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(tempFile/*.getFile*/), 64 * 1024)), '\t', CSVWriter.NO_QUOTE_CHARACTER)
      writer.writeAll(newBatch)
      writer.close()

      val source = new com.vertica.jdbc.DataSource()
      source.setUserID(args(0));
      source.setPassword(args(1));
      source.setURL(args(2));
      source.setResultBufferSize(8 * 1024 * 1024);


      val connection = source.getConnection()

      connection.setAutoCommit(true);
      val statement = connection.prepareStatement(getInsertStatement(tempFile, args(3)));

      statement.execute()

      connection.close()

      tempFile.delete()
    })

  }

  private def getInsertStatement(fifoFile: File, tempFilePath: String): String = {
    val sqlStmt: StringBuilder = new StringBuilder(2048)
    sqlStmt.append("COPY ")
    sqlStmt.append("wrd.cdr_data_test")
    sqlStmt.append(" FROM LOCAL ")
    sqlStmt.append(" '").append(fifoFile.getAbsolutePath).append("'")
    sqlStmt.append(" DIRECT ")
    sqlStmt.append(" DELIMITER '" + "\t" + "'")
    sqlStmt.append(" REJECTED DATA '").append(tempFilePath).append(".rejected.log' ")
    sqlStmt.append(" EXCEPTIONS '").append(tempFilePath).append(".exceptions.log' ")
    sqlStmt.append(";")
    return sqlStmt.toString
  }

}

