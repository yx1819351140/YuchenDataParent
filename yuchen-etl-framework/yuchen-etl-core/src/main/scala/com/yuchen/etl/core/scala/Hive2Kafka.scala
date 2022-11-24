package com.yuchen.etl.core.scala

import com.yuchen.common.enums.LangType
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.spark.SparkSupport

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/21 16:31
 * @Package: com.yuchen.etl.core.scala
 * @ClassName: Hive2Kafka
 * @Description: $END
 * */
object Hive2Kafka {

  def main(args: Array[String]): Unit = {
    val configJson = args(0)
    val sparkJobConfig = ConfigFactory.loadFromJson(configJson, classOf[SparkJobConfig])
    val session = SparkSupport.createSparkSession(sparkJobConfig, LangType.SCALA)
    val tableName = sparkJobConfig.getJobConfig.getStringVal("tableName")
    val frame = session
      .sql("select * from default." + tableName)
    frame.show()
  }

}
