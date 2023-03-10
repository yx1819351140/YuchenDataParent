package com.yuchen.test.etl.runtime

import com.alibaba.fastjson.JSONObject
import com.yuchen.common.enums.LangType
import com.yuchen.common.pub.HbaseHelper
import com.yuchen.data.api.pojo.ServiceRequest
import com.yuchen.data.api.service.IHbaseService
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.dubbo.DubboServiceHolder
import com.yuchen.etl.core.java.resolve._
import com.yuchen.etl.core.java.spark.{BroadcastInitializer, SparkBroadcastWrapper, SparkSupport}
import org.apache.spark.rdd.RDD

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/13 16:21
 * @Package: com.yuchen.etl.runtime.scala
 * @ClassName: TestHbaseApp
 * @Description: $END
 * */
object TestHbaseApp {
  def main(args: Array[String]): Unit = {
    //SparkSupport加载配置,创建Session对象
    val args: Array[String] = Array("D:\\project\\YuchenDataParent\\yuchen-etl-framework\\yuchen-etl-runtime\\src\\test\\resources\\job.json")
    val config: SparkJobConfig = ConfigFactory.load(args(0), classOf[SparkJobConfig])
    val session = SparkSupport.createSparkSession(config, LangType.SCALA)
    val context = session.sparkContext
    val taksConfig = config.getTaskConfig

    //创建日志收集器的包装对象, 用来进行Spark广播
    val collectorConfig: ErrorInfoCollectorConfig = new ErrorInfoCollectorConfig(taksConfig)
    val collectorWrapper: SparkBroadcastWrapper[ErrorInfoCollector] = SparkBroadcastWrapper.wrapper(new BroadcastInitializer[ErrorInfoCollector] {
      override def init(): ErrorInfoCollector = {
        val collector = ErrorInfoCollectorFactory.createCollector(collectorConfig)
        sys.addShutdownHook {
          collector.close()
        }
        collector
      }
    })
    val collectorBroadcast = context.broadcast(collectorWrapper)


    //创建HbaseHelper的包装对象,用来进行Spark广播
    val hbaseHelperWarpper: SparkBroadcastWrapper[HbaseHelper] = SparkBroadcastWrapper.wrapper(new BroadcastInitializer[HbaseHelper] {
      override def init(): HbaseHelper = {
        HbaseHelper.config(taksConfig)
        val helper = HbaseHelper.getInstance()
        helper
      }
    })
    val hbaseHelperBroadcast = context.broadcast(hbaseHelperWarpper)

    val rdd: RDD[String] = context.textFile("file:\\D:\\project\\YuchenDataParent\\yuchen-etl-framework\\yuchen-etl-runtime\\src\\test\\resources\\rowkeys.txt")
    val value = rdd.mapPartitions(p => {
      val hbaseClient = hbaseHelperBroadcast.value.getObj
      p.map(rowKey => {
        val json = hbaseClient.selectRow("url_hbase_v1", rowKey)
        json.put("rowKey", rowKey)
        json
      })
    })
    DubboServiceHolder.config(taksConfig)
    val dr = value.map(p => {
      val collector = collectorBroadcast.value.getObj
      collector.collect(LogType.STATUS, LogLevel.FATAL, LogSource.BIGDATA, "test_12_28_05_", "测试收集器_12_28_05_", null)
      val dubboServiceHolder = DubboServiceHolder.getInstance()
      val service = dubboServiceHolder.getService(classOf[IHbaseService], "1.0.0");
      val request = new ServiceRequest()
      request.setTable("url_hbase_v1")
      val req = new JSONObject()
      req.put("rowKey", p.getString("rowKey"))
      request.setRequest(req)
      val response = service.test(request)
      collector.collect(LogType.STATUS, LogLevel.FATAL, LogSource.BIGDATA, "test_12_28_05_", response.getData.toString, null)
      response.getData
    })
    dr.foreach(println(_))
  }
}
