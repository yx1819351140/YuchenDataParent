package com.yuchen.etl.core.scala

import com.yuchen.common.enums.LangType
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.spark.SparkSupport

import java.util.Base64

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 14:55
 * @Package: com.yuchen.etl.core.scala
 * @ClassName: ExecuteSparkSql
 * @Description: $END
 * */
object ExecuteSparkSql {


  def main(args: Array[String]): Unit = {
    val base64Json = args(0)
    val sparkJobConfig = ConfigFactory.load(base64Json, classOf[SparkJobConfig], true)
    val session = SparkSupport.createSparkSession(sparkJobConfig, LangType.SCALA)
    val sql = sparkJobConfig.getExecuteSql(true)
    println("=====================sql start==================")
    println()
    println(sql)
    println()
    println("=====================sql end====================")
    println()
    val mediumNewsCount = session.sql(sql)
    mediumNewsCount.show()
    println("================================================")
    println()


  }
}
