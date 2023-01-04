package com.yuchen.test.etl.runtime

import com.yuchen.common.enums.LangType
import com.yuchen.common.pub.HbaseHelper
import com.yuchen.data.api.pojo.{ServiceRequest, ServiceResponse}
import com.yuchen.data.api.service.IEsService
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.dubbo.DubboServiceHolder
import com.yuchen.etl.core.java.spark.SparkSupport
import org.apache.spark.rdd.RDD

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/13 9:37
 * @Package: com.yuchen.etl.runtime.scala
 * @ClassName: TestDubboApp
 * @Description: $END
 * */
object TestDubboApp {

  def main(args: Array[String]): Unit = {
    //SparkSupport加载配置,创建Session对象
    val args: Array[String] = Array("yuchen-etl-framework/yuchen-etl-runtime/src/test/resources/job.json")
    val config: SparkJobConfig = ConfigFactory.load(args(0), classOf[SparkJobConfig])
    val session = SparkSupport.createSparkSession(config, LangType.SCALA)
    //读取数据到RDD
    val context = session.sparkContext
    val rdd: RDD[String] = context.textFile("yuchen-etl-framework/yuchen-etl-runtime/src/test/resources/example.txt")
    val flatRdd: RDD[String] = rdd.flatMap(_.split(","))
    val mapRDD: RDD[(String,Int)] = flatRdd.map(s => {
      (s,1)
    })
    HbaseHelper.getInstance()
    val reduceRDD: RDD[(String,Int)] = mapRDD.reduceByKey((i,y) => {
      i+y
    })
    val job = config.getTaskConfig
    DubboServiceHolder.config(job)
    val response = reduceRDD.mapPartitions(e => {
      val iEs = DubboServiceHolder.getInstance().getService(classOf[IEsService], "1.0.0")
      e.map(s => {
        val request = new ServiceRequest()
        request.setTable(s._1)
        val value: ServiceResponse[_ <: Long] = iEs.query(request).asInstanceOf[ServiceResponse[_ <: Long]]
        value.getData
      })
    })
    response.foreach(println(_))
  }
}
