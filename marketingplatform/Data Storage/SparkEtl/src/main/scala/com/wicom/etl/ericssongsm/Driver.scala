package com.wicom.etl.ericssongsm

import java.io.File

import com.typesafe.config._
import com.wicom.utils.Logger
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.spark.{SparkContext, SparkConf}
import scala.collection.JavaConversions._

/**
 * Created by santiago on 14/05/15.
 */
object Driver {

  def main(args: Array[String]) {
    val properties = ConfigFactory.load()
    // println("The answer is " + conf.getString("watcher.file.incoming.directory"))

    //Creating the Spark Context
    val conf = new SparkConf().setAppName("Spark CDR Aggregation")
    val sc = new SparkContext(conf)

    /*1)FILES will live in a share folder accessible by the whole cluster
    2)asn2txt tool will need to be copied in every cluster node; if that's not possible then this step won't be able to be
     parallelized
    3)
  */

    val fileNames = FileUtils.listFiles(new File(properties.getString("watcher.file.queued.directory")),new IOFileFilter {override def accept(file: File): Boolean = true

      override def accept(file: File, s: String): Boolean = true
    }, new IOFileFilter {override def accept(file: File): Boolean = true

      override def accept(file: File, s: String): Boolean = true
    }).toList.map(_.getName)



    val auditor = new AuditorImpl(properties)
    sc.parallelize(fileNames).foreach(fileName => {

      val asnToXmlParser = new AsnToXmlParser(properties, Logger, auditor)
      val ericssoXmlParser = new EricssonGSMXmlParser(properties, Logger, auditor)
      val verticaCopyWriter = new VerticaCopyWriter(properties, Logger, auditor)
      val xmlUploader = new XmlUploader(properties, Logger, auditor)

      asnToXmlParser.parse("12345678", fileName)
        .flatMap(f => ericssoXmlParser.parse(f))
        .map(f => {
                   // xmlUploader.upload(f)
                    verticaCopyWriter.copy(f)
                  }
        )
    })



    //update network dimension
    (new UpdateNetworkDimension(properties, Logger, auditor)).update()


    
  }

}
