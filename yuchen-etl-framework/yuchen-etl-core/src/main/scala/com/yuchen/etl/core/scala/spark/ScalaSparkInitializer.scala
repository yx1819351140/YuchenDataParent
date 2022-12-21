package com.yuchen.etl.core.scala.spark

import com.yuchen.etl.core.java.config.SparkJobConfig
import com.yuchen.etl.core.java.constants.SparkConstant
import com.yuchen.etl.core.java.spark.SparkInitializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 17:54
 * @Package: com.yuchen.etl.core.scala.spark
 * @ClassName: SparkIniter
 * @Description: $END
 * */
class ScalaSparkInitializer extends SparkInitializer {

  private final lazy val sparkConf: SparkConf = new SparkConf()
  private var sparkSession: SparkSession = null
  private var sparkContext: SparkContext = null
  private var streamingContext: StreamingContext = null

  override def init(sparkJobConfig: SparkJobConfig): Unit = {
    val sparkConfig = sparkJobConfig.getSparkConfig
    import scala.collection.JavaConversions._
    for (k <- sparkConfig.keySet()) {
      val value = sparkConfig.get(k)
      if (value != null) {
        sparkConf.set(k, String.valueOf(value))
      }
    }

    val builder = SparkSession.builder
      .config(sparkConf)
      .appName(sparkJobConfig.getJobName())
    if (sparkConfig.isEnableHiveSupport) {
      builder.enableHiveSupport
    }
    if (sparkConfig.isLocal) {
      builder.master(SparkConstant.LOCAL_MODE)
    }

    this.sparkSession = builder.getOrCreate();
    this.sparkContext = sparkSession.sparkContext;
    this.streamingContext = new StreamingContext(sparkSession.sparkContext, Seconds(sparkConfig.getStreamDuration()))
  }

  override def scalaSparkSession(): SparkSession = this.sparkSession

  override def scalaSparkStreaming(): StreamingContext = this.streamingContext

  override def scalaSparkContext(): SparkContext = this.sparkContext
}
