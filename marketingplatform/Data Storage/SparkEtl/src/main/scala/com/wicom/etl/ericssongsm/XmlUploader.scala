package com.wicom.etl.ericssongsm

import java.io._
import java.net.URI


import com.wicom.utils.Logger
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.DFSClient


/**
 * Created by santiago on 22/05/15.
 */
class XmlUploader(val config: Config, val logger: Logger, val auditor: Auditor) {

  val hdfsUrl = config.getString(GenericConstants.HDFS_URI)
  val hdfsDestinationDirectory =  config.getString(GenericConstants.HDFS_DESTINATION_DIRECTORY)
  val xmlFileSourceDirectory = config.getString(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY)


  def upload(fileName: String) = {
    val xmlFileName = fileName.replace(".csv",".xml")
    val zipFullFilename = xmlFileSourceDirectory.concat("/" + xmlFileName.replace(".xml", ".zip"))
    val xmlFullFilename = xmlFileSourceDirectory.concat("/" + xmlFileName)

    val fos = new FileOutputStream(zipFullFilename)

    val zos: ZipOutputStream = new ZipOutputStream(fos)
    val ze: ZipEntry = new ZipEntry(xmlFileName)
    zos.putNextEntry(ze)

    val fis: FileInputStream = new FileInputStream(xmlFullFilename)
    var leng: Int = 0

    val zipbuffer: Array[Byte] = new Array[Byte](1024)
    while ( {
      leng = fis.read(zipbuffer);
      leng
    } > 0) {
      zos.write(zipbuffer, 0, leng)
    }

    fis.close
    zos.closeEntry
    zos.close

    val zipFile: File = new File(zipFullFilename)

    if (zipFile.exists) {
      val conf: Configuration = new Configuration
      conf.set("fs.defaultFS", this.hdfsUrl)
      val client: DFSClient = new DFSClient(new URI(this.hdfsUrl), conf)
      val date_sid = xmlFileName.substring(1, 9)
      logger.info(s"Connection to $hdfsUrl established")
      val finalDestinationFileName = hdfsDestinationDirectory.concat(date_sid + "/").concat(xmlFileName.replace(".xml", ".zip"))
      var out: OutputStream = null
      var in: InputStream = null
      //logger.info("File already exists in hdfs: " + finalDestinationFileName)
      try {
        if (!client.exists(finalDestinationFileName)) {
          out = new BufferedOutputStream(client.create(finalDestinationFileName, false))
          in = new BufferedInputStream(new FileInputStream(zipFullFilename))
          val buffer: Array[Byte] = new Array[Byte](1024)
          var len: Int = 0
          while ( {
            len = in.read(buffer);
            len
          } > 0) {
            out.write(buffer, 0, len)
          }
        }
      } finally {
        client.close()
      }
    }
  }




}
