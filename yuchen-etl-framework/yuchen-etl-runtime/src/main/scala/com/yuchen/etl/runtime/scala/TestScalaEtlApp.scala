package com.yuchen.etl.runtime.scala

import com.yuchen.common.enums.LangType
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.spark.SparkSupport

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/21 11:40
 * @Package:
 * @ClassName: TestETLApp
 * @Description: Test
 * */
object TestScalaEtlApp {
  def main(args: Array[String]): Unit = {
    System.out.print("123")
    val config = ConfigFactory
      .load(args(0), classOf[SparkJobConfig], true)
    val session = SparkSupport
      .createSparkSession(config, LangType.SCALA)

  }
}
