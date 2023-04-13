package com.yuchen.etl.runtime.scala.news

import com.yuchen.common.enums.LangType
import com.yuchen.common.pub.HbaseHelper
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.hbase.HbaseDao
import com.yuchen.etl.core.java.spark.{BroadcastInitializer, SparkBroadcastWrapper, SparkSupport}

/**
 * @Author: xiaozhennan
 * @Date: 2023/4/13 9:54
 * @Package: com.yuchen.etl.runtime.scala.news
 * @ClassName: Gdelt2KafkaSpark
 * @Description: $END
 * */
object Gdelt2KafkaSpark {

  def main(args: Array[String]): Unit = {
    //json文件对象
    val sparkJobConfig = ConfigFactory.load(args(0), classOf[SparkJobConfig])
    //用json文件创建session
    val session = SparkSupport.createSparkSession(sparkJobConfig, LangType.SCALA)
    //从sparkJobConfig中取taskConfig, json中taskConfig的部分
    val taskConfig = sparkJobConfig.getTaskConfig

    val hbaseDaoWrapper: SparkBroadcastWrapper[HbaseDao] = SparkBroadcastWrapper.wrapper(new BroadcastInitializer[HbaseDao] {
      override def init(): HbaseDao = {
        HbaseHelper.config(taskConfig)
        val helper = HbaseHelper.getInstance()
        sys.addShutdownHook {
          helper.close();
        }
        val dao = new HbaseDao(helper.getConnection, helper.getAdmin)
        dao
      }
    })
    val context = session.sparkContext
    val broadcastWrapper = context.broadcast(hbaseDaoWrapper)



    // 读orc表
    //groyo by 去重
    //生成get对象
    //通过HbaseDao查询Hbase
    //根据过滤筛选条件决定发送到kafka的数据
    //发送kafka
    val str = taskConfig.getStringVal("aaa")




  }

}
