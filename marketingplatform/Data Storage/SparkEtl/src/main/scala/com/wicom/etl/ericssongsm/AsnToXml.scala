package com.wicom.etl.ericssongsm

import java.io.{IOException, File}
import java.text.SimpleDateFormat
import java.util.Calendar

import com.typesafe.config._
import com.wicom.utils.Logger
import org.apache.commons.io.FileUtils


/**
 * Created by santiago on 18/05/15.
 */
trait AsnToXmlParsing {

  val config: Config
  val logger: Logger
  val auditor: Auditor

  def parse(auditSid: String, fileName: String) : Option[String]
}


class AsnToXmlParser(val config: Config, val logger: Logger, val auditor: Auditor) extends  AsnToXmlParsing {
  val asnParserPath = config.getString(GenericConstants.ASN_PARSER_PATH) + "asn2txt.sh"
  val asnParserSchemaPath = config.getString(GenericConstants.ASN_PARSER_SCHEMA_PATH)
  val asnParserOutputDirectory = config.getString(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY)
  val binaryFilePath = config.getString(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY)
  val processedFilePath = config.getString(GenericConstants.WATCHER_FILE_PROCESSED_DIRECTORY)
  val errorFilePath = config.getString(GenericConstants.WATCHER_FILE_ERROR_DIRECTORY)


  override def parse(auditSid: String, fileName: String) : Option[String] = {

    var result: Option[String] = None
    val temporaryXmlFilenameFormat = new SimpleDateFormat("ddHHmmssSSS")
    val c1 = Calendar.getInstance()
    val temporaryXmlFileName = temporaryXmlFilenameFormat.format(c1.getTime())
    val fullBinaryFilePath = if (binaryFilePath.endsWith("/") || binaryFilePath.endsWith("\\"))  binaryFilePath + fileName
                             else binaryFilePath + "/" + fileName

    val outputFileName = temporaryXmlFileName + ".xml"
    val outputFilePath = if (asnParserOutputDirectory.endsWith("/") || asnParserOutputDirectory.endsWith("\\"))
                           asnParserOutputDirectory + outputFileName
                         else
                           asnParserOutputDirectory + "/" + outputFileName

    val command: String = asnParserPath + " " + fullBinaryFilePath + " -xer -schema " + asnParserSchemaPath + " -xml -o " + outputFilePath
    logger.info("Parsing file " + fileName + " with asn2txt parser")
    logger.info("Running command:" + command)
    try {
      val timeStamp: String = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance.getTime)
      logger.info(timeStamp + " ... Decoding file " + fileName)
      val p: Process = Runtime.getRuntime.exec(command)
      p.waitFor
      logger.info(fileName + " parsed. Output file  " + outputFilePath + " was generated")
      org.apache.commons.io.FileUtils.moveFileToDirectory(new File(fullBinaryFilePath), new File(processedFilePath), true)
      logger.info(fileName + " is moved to " + processedFilePath )
      val outputFullFilePath = (if (asnParserOutputDirectory.endsWith("/") || asnParserOutputDirectory.endsWith("\\"))
                                  asnParserOutputDirectory + fileName
                                else
                                  asnParserOutputDirectory + "/" + fileName) + ".xml"

      org.apache.commons.io.FileUtils.moveFile(new File(outputFilePath), new File(outputFullFilePath))
      logger.info(outputFilePath + " is renamed to " +  asnParserOutputDirectory + fileName + ".xml")
      auditor.updateAudit(auditSid, "0", "Decoded")
      result = Some(fileName + ".xml")

    }
    catch {
      case e: IOException => {
        logger.error("Error Decoding file." + e.getMessage)
        try {
          org.apache.commons.io.FileUtils.moveFileToDirectory(new File(fullBinaryFilePath), new File(errorFilePath), true)
          auditor.updateAudit(auditSid, "0", "Error")
        }
        catch {
          case e1: IOException => {
            e1.printStackTrace
          }
        }
      }
      case e: InterruptedException => {
        logger.error("Unexpected Error Decoding file." + e.getMessage)
        try {
          org.apache.commons.io.FileUtils.moveFileToDirectory(new File(fullBinaryFilePath), new File(errorFilePath), true)
          auditor.updateAudit(auditSid, "0", "Error")
        }
        catch {
          case e1: IOException => {
            e1.printStackTrace
          }
        }
        e.printStackTrace
      }
    }
    result

  }
}