package com.yuchen.test.etl.core.spark

import com.alibaba.fastjson.JSONObject
import com.yuchen.common.enums.LangType
import com.yuchen.common.pub.HbaseHelper
import com.yuchen.data.api.pojo.ServiceRequest
import com.yuchen.data.api.service.IHbaseService
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.dubbo.DubboServiceHolder
import com.yuchen.etl.core.java.resolve.{ErrorInfoCollector, ErrorInfoCollectorConfig, ErrorInfoCollectorFactory, LogLevel, LogSource, LogType}
import com.yuchen.etl.core.java.spark.{BroadcastInitializer, SparkBroadcastWrapper, SparkSupport}
import org.apache.spark.rdd.RDD

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/3 13:40
 * @Package: com.yuchen.test.etl.core.spark
 * @ClassName: SparkBroadcastWarpperTest
 * @Description: $END
 * */
object TestSparkBroadcastWarpper {

  def main(args: Array[String]): Unit = {
    //SparkSupport加载配置,创建Session对象
    val args: Array[String] = Array("yuchen-etl-framework/yuchen-etl-runtime/src/test/resources/job.json")
    val config: SparkJobConfig = ConfigFactory.load(args(0), classOf[SparkJobConfig])
    val session = SparkSupport.createSparkSession(config, LangType.SCALA)
    val context = session.sparkContext
    val taskConfig = config.getTaskConfig

    //日志错误信息collector包装
    val collectorConfig: ErrorInfoCollectorConfig = new ErrorInfoCollectorConfig(taskConfig)
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

    //dubbo服务holder广播包装
    val dubboBroadcastWarpper : SparkBroadcastWrapper[DubboServiceHolder] = SparkBroadcastWrapper.wrapper(new BroadcastInitializer[DubboServiceHolder] {
      override def init(): DubboServiceHolder = {
        DubboServiceHolder.config(taskConfig)
        DubboServiceHolder.getInstance();
      }
    })
    val dubboBroadcast = context.broadcast(dubboBroadcastWarpper)


    val rdd: RDD[String] = context.textFile("yuchen-etl-framework/yuchen-etl-runtime/src/test/resources/rowkeys.txt")
    HbaseHelper.config(taskConfig)
    val value = rdd.mapPartitions(p => {
      val hbaseClient = HbaseHelper.getInstance()
      p.map(rowKey => {
        val json = hbaseClient.selectRow("url_hbase_v1", rowKey)
        json.put("rowKey", rowKey)
        json
      })
    })
    val dr = value.map(p => {
      val collector = collectorBroadcast.value.getObj
      collector.collect(LogType.STATUS, LogLevel.FATAL, LogSource.BIGDATA, "test_12_28_05_", "测试收集器_12_28_05_", null)
      val service = dubboBroadcast.value.getObj.getService(classOf[IHbaseService], "1.0.0");
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
