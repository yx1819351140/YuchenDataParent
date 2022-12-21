package com.yuchen.etl.runtime.scala

import com.alibaba.fastjson.JSONObject
import com.yuchen.common.enums.LangType
import com.yuchen.common.pub.HbaseHelper
import com.yuchen.data.api.pojo.ServiceRequest
import com.yuchen.data.api.service.IHbaseService
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.dubbo.DubboServiceHolder
import com.yuchen.etl.core.java.spark.SparkSupport
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
    val job = config.getTaskConfig
    val rdd: RDD[String] = context.textFile("file:\\D:\\project\\YuchenDataParent\\yuchen-etl-framework\\yuchen-etl-runtime\\src\\test\\resources\\rowkeys.txt")
    HbaseHelper.config(job)
    val value = rdd.mapPartitions(p => {
      val hbaseClient = HbaseHelper.getInstance()
      p.map(rowKey => {
        val json = hbaseClient.selectRow("url_hbase_v1", rowKey)
        json.put("rowKey", rowKey)
        json
      })
    })
    DubboServiceHolder.config(job)
    val dr = value.map(p => {
      val dubboServiceHolder = DubboServiceHolder.getInstance()
      val service = dubboServiceHolder.getService(classOf[IHbaseService], "1.0.0");
      val request = new ServiceRequest()
      request.setTable("url_hbase_v1")
      val req = new JSONObject()
      req.put("rowKey", p.getString("rowKey"))
      request.setRequest(req)
      val response = service.test(request)
      response.getData
    })

    dr.foreach(println(_))
  }
}
