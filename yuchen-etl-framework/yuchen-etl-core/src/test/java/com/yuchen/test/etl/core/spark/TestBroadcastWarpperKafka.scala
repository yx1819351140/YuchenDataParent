package com.yuchen.test.etl.core.spark

import com.yuchen.common.enums.LangType
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.spark.{BroadcastInitializer, SparkBroadcastWarpper, SparkSupport}
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.spark.rdd.RDD

import java.util.Properties

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/3 15:44
 * @Package: com.yuchen.test.etl.core.spark
 * @ClassName: BroadcastWarpperKafkaTest
 * @Description: $END
 * */
object TestBroadcastWarpperKafka {

  def main(args: Array[String]): Unit = {
    val args: Array[String] = Array("yuchen-etl-framework/yuchen-etl-runtime/src/test/resources/job.json")
    val config: SparkJobConfig = ConfigFactory.load(args(0), classOf[SparkJobConfig])
    val session = SparkSupport.createSparkSession(config, LangType.SCALA)
    val context = session.sparkContext
    val taskConfig = config.getTaskConfig

    val kafkaWrapper = SparkBroadcastWarpper.wrapper(new BroadcastInitializer[KafkaProducer[String, String]] {
      override def init(): KafkaProducer[String, String] = {
        val properties = new Properties()
        properties.putAll(taskConfig)
        val producer = new KafkaProducer[String, String](properties)
        sys.addShutdownHook {
          println("关闭kafka")
          producer.close()
        }
        producer
      }
    })

    // 这里将kafakproducer广播出去
    val kafkaProducer = context.broadcast(kafkaWrapper)
    val rdd: RDD[String] = context.textFile("yuchen-etl-framework/yuchen-etl-runtime/src/test/resources/rowkeys.txt")


    val l = rdd.count()
    println("count: " + l)

  }

}
