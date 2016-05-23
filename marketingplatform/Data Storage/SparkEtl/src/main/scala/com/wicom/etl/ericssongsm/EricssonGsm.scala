package com.wicom.etl.ericssongsm


import java.util
import java.util.ArrayList
import java.util.List

/**
 * Created by Charbel Hobeika on 4/9/2015.
 */
object EricssonGsm {
  def split[T <: Object](list: List[T], targetSize: Int): List[List[T]] = {
    val lists: List[List[T]] = new util.ArrayList[List[T]]
    var i = 0
    while (i < list.size) {
      {
        lists.add(list.subList(i, Math.min(i + targetSize, list.size)))
      }
      i += targetSize
    }
    lists
  }
}

class EricssonGsm {
  private val measObjInstIds = new util.ArrayList[String]
  private val measTypes = new util.ArrayList[String]
  private val iValues = new util.ArrayList[String]

  def setmeasObjInstId(measObjInstId: String) {
    this.measObjInstIds.add(measObjInstId)
  }

  def getmeasObjInstId: List[String] = {
    measObjInstIds
  }

  def getMeasType: List[String] = {
    measTypes
  }

  def setMeasType(MeasType: String) {
    measTypes.add(MeasType)
  }

  def getiValue: List[String] = {
    iValues
  }

  def setiValue(iValue: String) {
    this.iValues.add(iValue)
  }

  def PrintFunction: String = {
    val newLine = System.lineSeparator
    val measObjInstIdCount = measObjInstIds.size
    val measTypeCount = measTypes.size
    var iValue_cnt = 0
    val combined = new util.ArrayList[String]
    for (i <- 0 to (measObjInstIdCount - 1)) {
      combined.add(measObjInstIds.get(i))
      for (j <- 0 to (measTypeCount-1)) {
        combined.add(iValues.get(iValue_cnt))
        iValue_cnt += 1
      }
    }
    if (measObjInstIdCount > 1) {
      val targetSize: Int = measTypeCount + 1
      val lists: List[List[String]] = EricssonGsm.split(combined, targetSize)
      String.valueOf(lists.toString + newLine)
    }
    else {
      String.valueOf(combined + newLine)
    }
  }

  def PrintMeasuresCounters: String = {
    val newLine: String = System.lineSeparator
    val bla = new util.ArrayList[String]
    val msgArray = measObjInstIds.get(0).substring(0, this.measObjInstIds.get(0).indexOf("|")).split(",")
    for (j <- 0 to (measTypes.size-1)) {
      bla.add("SELECT '" + msgArray(msgArray.length - 1).substring(0, msgArray(msgArray.length - 1).indexOf("=")) + "','" + measTypes.get(j) + "'," + j + 1 + " UNION" + newLine)
    }
    val query: String = "insert into spark_stg_measure_type (dimension_type, measure_name, measure_column_index) " + String.valueOf(bla).replace("[", "").replace("]", "").replace(", ", "").substring(0, String.valueOf(bla).replace("[", "").replace("]", "").replace(", ", "").lastIndexOf("UNION"))
    query
  }
}
