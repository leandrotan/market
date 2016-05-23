package com.wicom.etl.ericssongsm

import java.io.{FileReader, BufferedReader, Reader}

import com.ibatis.common.jdbc.ScriptRunner
import com.vertica.jdbc.DataSource
import com.wicom.utils.Logger
import com.typesafe.config.Config


/**
 * Created by santiago on 22/05/15.
 */
class UpdateNetworkDimension(val config: Config, val logger: Logger, val auditor: Auditor) {
  val populateNetworkAndDictionary = config.getString(GenericConstants.TRANSFORMER_FILE_NETWORKANDDICTIONARY_POPULATE)

  def update(): Unit = {


    val verticaDs = new DataSource
    verticaDs.setHost(config.getString(GenericConstants.VERTICA_SERVER))
    verticaDs.setPort(config.getString(GenericConstants.VERTICA_PORT).toShort)
    verticaDs.setDatabase(config.getString(GenericConstants.VERTICA_DB))
    verticaDs.setUserID(config.getString(GenericConstants.VERTICA_USER))
    verticaDs.setPassword(config.getString(GenericConstants.VERTICA_PASS))
    verticaDs.setAutoCommitOnByDefault(false)

    val conn = verticaDs.getConnection

    val sr: ScriptRunner = new ScriptRunner(conn, false, false)

    // Give the input file to Reader
    logger.info("Adding the input file to Reader")
    val reader: Reader = new BufferedReader(new FileReader(populateNetworkAndDictionary))

    // Exctute script
    logger.info("Executing Script to populate Storm_Dim_Network and Dictionary Tables")
    sr.runScript(reader)

    conn.commit
  }
}
