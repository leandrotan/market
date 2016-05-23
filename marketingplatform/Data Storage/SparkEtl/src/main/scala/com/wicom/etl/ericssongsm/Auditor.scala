package com.wicom.etl.ericssongsm

import com.typesafe.config._

/**
 * Created by santiago on 15/05/15.
 */
trait Auditor {

  val config: Config

  def logAudit(processId: String, fileName: String): String = {
    "12345678"
  }

  def updateAudit(rowKey: String, rowCount: String) = {

  }

  def updateAudit(rowKey: String, rowCount: String, status: String) = {

  }

  def updateAudit(rowKey: String, rowCount: String, rejectedCount: String, status: String) = {

  }

  def updateAudit(rowKey: String) = {

  }

}

case class AuditorImpl(val config: Config) extends Auditor



