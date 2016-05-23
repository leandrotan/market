package com.wicom.etl.ericssongsm

import java.io.{FileWriter, BufferedWriter, File, FileInputStream}
import java.text.SimpleDateFormat
import java.util
import javax.xml.stream.{XMLStreamConstants, XMLInputFactory}

import com.typesafe.config.Config
import com.wicom.utils.Logger
import com.vertica.jdbc._
import scala.collection.JavaConversions._


import scala.collection.JavaConversions._

trait EricssonGSMXmlParsing {

  val config: Config
  val logger: Logger
  val auditor: Auditor

  def parse(fileName: String) : Option[String]
}
/**
 * Created by santiago on 13/05/15.
 */
class EricssonGSMXmlParser(val config: Config, val logger: Logger, val auditor: Auditor) extends EricssonGSMXmlParsing {
  val parserfilePath = config.getString(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY)
  val processId = config.getString(GenericConstants.TOPOLOGY_PROCESS_ID)
  val parserOutputfilePath = config.getString(GenericConstants.STAX_PARSER_OUTPUT_DIRECTORY)


  override  def parse(xmlFileName: String): Option[String] = {

    var result: Option[String] = None

    val fullFilePath = if (parserfilePath.endsWith("/")) parserfilePath + xmlFileName
    else parserfilePath + "/" + xmlFileName
    val fullOutputFilePath = (if (parserOutputfilePath.endsWith("/")) parserOutputfilePath + xmlFileName
    else parserOutputfilePath + "/" + xmlFileName).replace(".xml",".csv")
    val ericssonGsms = new util.ArrayList[EricssonGsm]
    var tagContent = ""
    var currentMeasInfo: EricssonGsm = null
    val dateSid = xmlFileName.substring(1, 9)
    val hourSid = xmlFileName.substring(10, 12)
    val secondSid = "0"
    val sdf1: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    val sdf2: SimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy")
    val dateId = sdf2.format(sdf1.parse(dateSid))
    val genericHeader = dateSid + "|" + dateId + "|" + hourSid + "|" + secondSid
    val bscId = xmlFileName.substring(xmlFileName.indexOf("_") + 1, xmlFileName.lastIndexOf("_"))
    val inputFile = new File(fullFilePath)
    val auditSid = auditor.logAudit("XML Parsing", fullFilePath)
    try {
      if (inputFile.canRead) {
        val reader = XMLInputFactory.newInstance.createXMLStreamReader(new FileInputStream(fullFilePath))
        while (reader.hasNext) {
          val event: Int = reader.next
          event match {
            case XMLStreamConstants.START_ELEMENT =>
              if ("MeasInfo" == reader.getLocalName) {
                currentMeasInfo = new EricssonGsm
                ericssonGsms.add(currentMeasInfo)
              }
            case XMLStreamConstants.CHARACTERS =>
              tagContent = reader.getText.trim
            case XMLStreamConstants.END_ELEMENT =>
              reader.getLocalName match {
                case "MeasType" =>
                  currentMeasInfo.setMeasType(tagContent)

                case "measObjInstId" =>
                  if (tagContent == "BSC.-") {
                    currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC=" + bscId + "|" + genericHeader)
                  }
                  else if ((tagContent == "BSCGPRS.-") || (tagContent == "BSCGPRS2.-") || (tagContent == "TRH.-") || (tagContent == "GPHLOADREG.-") || (tagContent == "LOADREG.-") || (tagContent == "LOAS.-") || (tagContent == "LOASMISC.-") || (tagContent == "TRALOST.-") || (tagContent == "TRAPCOM.-")) {
                    currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC=" + bscId + "," + tagContent.replace(".-", "") + "=" + bscId + "|" + genericHeader)
                  }
                  else if (tagContent.contains("BSCQOS") || tagContent.contains("DIP") || tagContent.contains("EMGPRS") || tagContent.contains("LAPD") || tagContent.contains("NONRES64K") || tagContent.contains("RES64K") || tagContent.contains("TRAPEVENT") || tagContent.contains("MOTS")) {
                    val t = tagContent.split("\\.")
                    currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC=" + bscId + "," + t(0) + "=" + t(1) + "|" + genericHeader)
                  }
                  else if (tagContent.contains("NUCELLREL") || tagContent.contains("NCELLREL") || tagContent.contains("NECELASS") || tagContent.contains("NECELHO") || tagContent.contains("NECELHOEX") || tagContent.contains("NECELLREL") || tagContent.contains("NICELASS") || tagContent.contains("NICELHO") || tagContent.contains("NICELHOEX")) {
                    val t = tagContent.split("\\.")
                    val tt = t(1).split("-")
                    currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC=" + bscId + ",CELL=" + tt(0) + "," + t(0) + "=" + t(1) + "|" + genericHeader)
                  }
                  else if (tagContent.contains(".")) {
                    val t = tagContent.split("\\.")
                    currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC=" + bscId + ",CELL=" + t(1) + "," + t(0) + "=" + t(1) + "|" + genericHeader)
                  }
                  else currentMeasInfo.setmeasObjInstId(tagContent + "|" + genericHeader)
                case "iValue" =>
                  currentMeasInfo.setiValue(tagContent)
                case "noValue" =>
                  currentMeasInfo.setiValue("")
                case _ => ()
              }
            case _ => ()

          }
        }
        val outputFile = new File(fullOutputFilePath)
        val outputFileDir = new File(parserOutputfilePath)
        if (!outputFileDir.exists()) {
          outputFileDir.mkdir()
        }
        val outputBufferWriter = new BufferedWriter(new FileWriter(outputFile))

        //mvn install:install-file -DgroupId=vertica -DartifactId=vertica-jdbc -Dversion=7.0.1-0 -Dpackaging=jar -Dfile=vertica-jdbc-7.0.1-0.jar -DgeneratePom=true
        val verticaDs = new DataSource
        verticaDs.setHost(config.getString(GenericConstants.VERTICA_SERVER))
        verticaDs.setPort(config.getString(GenericConstants.VERTICA_PORT).toShort)
        verticaDs.setDatabase(config.getString(GenericConstants.VERTICA_DB))
        verticaDs.setUserID(config.getString(GenericConstants.VERTICA_USER))
        verticaDs.setPassword(config.getString(GenericConstants.VERTICA_PASS))
        verticaDs.setAutoCommitOnByDefault(true)
        val conn = verticaDs.getConnection
        val stmt = conn.createStatement()

        for (emp <- ericssonGsms) {
          outputBufferWriter.write(emp.PrintFunction.replace("], [", System.lineSeparator())
                                                    .replace("]]", "")
                                                    .replace("[[", "")
                                                    .replace("[", "")
                                                    .replace("]", "")
                                                    .replace(", ", "|"))
          stmt.addBatch(emp.PrintMeasuresCounters)
        }

        val query: String = "\n" + "INSERT INTO spark_generic_dimension_types (dimension_type_sid, dimension_table_name, dimension_type)\n" +
                                    "SELECT NEXTVAL('spark_dim_net_seq'), 'spark_dim_network', dimension_type\n" +
                                    "FROM (\n" + "SELECT DISTINCT dimension_type \n" + "FROM spark_stg_measure_type\n" + ") z \n" + 
                                    "WHERE dimension_type \n" + "NOT IN (SELECT DISTINCT dimension_type FROM spark_generic_dimension_types);\n" + "\n" +
                                    "INSERT INTO spark_measure_dictionary (measure_sid, measure_name, measure_type, measure_column_index, fact_table)\n" + 
                                    "SELECT NEXTVAL('spark_dim_net_seq'), measure_name, measure_type, measure_column_index, fact_table\n" + 
                                    "FROM (SELECT DISTINCT measure_name, 'numeric' as measure_type, measure_column_index, 'spark_stg_fact' as fact_table FROM spark_stg_measure_type\n" + 
                                    ") z WHERE ISNULL(measure_name,'') <> ''\n" + "AND measure_name NOT IN (SELECT DISTINCT measure_name FROM spark_measure_dictionary);\n" + "\n" + 
                                    "INSERT INTO spark_types_x_measures (dimension_type_sid, measure_sid)\n" + 
                                    "SELECT dimension_type_sid,measure_sid \n" + 
                                    "FROM (\n" + "SELECT DISTINCT measure_name,dimension_type FROM spark_stg_measure_type ) z \n" + 
                                    "INNER JOIN spark_generic_dimension_types b\n" + "ON z.dimension_type = b.dimension_type\n" + 
                                    "INNER JOIN spark_measure_dictionary c\n" + "ON z.measure_name = c.measure_name\n" + 
                                    "WHERE ISNULL(z.measure_name,'') <> ''\n" + "AND CONCAT(dimension_type_sid,measure_sid)\n" + 
                                    "NOT IN (SELECT CONCAT(dimension_type_sid,measure_sid) FROM spark_types_x_measures);\n" + "\n" + 
                                    "DELETE FROM spark_stg_measure_type;"

        stmt.addBatch(query)

        stmt.executeBatch

        logger.info("Table spark_stg_measure_type is populated")
        stmt.close
        conn.close
      }
    } catch {
      case e: Exception => {
        logger.error(e.getMessage)

      }
    }

    result = Some(xmlFileName.replace(".xml",".csv" ))
    result

  }
}
