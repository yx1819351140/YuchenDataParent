//package com.yuchen.etl.runtime.scala
//
//import com.alibaba.fastjson.JSONObject
//import com.yuchen.common.enums.LangType
//import com.yuchen.common.pub.HbaseHelper
//import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
//import com.yuchen.etl.core.java.spark.{BroadcastInitializer, SparkBroadcastWrapper, SparkSupport}
//import org.slf4j.{Logger, LoggerFactory}
//
//import java.io.File
//import java.util
//import scala.collection.mutable
//
///**
// * @Author: xiaozhennan
// * @Date: 2023/1/3 16:15
// * @Package: com.yuchen.etl.runtime.scala
// * @ClassName: ExampleConfigLoadApp
// * @Description: $END
// * */
//object ExampleConfigLoadApp {
//
//  val logger: Logger = LoggerFactory.getLogger(this.getClass)
//
//  def main(args: Array[String]): Unit = {
//    val loadParameter = args(1)
//    val loadType = args(0)
//    val sparkJobConfig: SparkJobConfig = loadType match {
//      case "file" => ConfigFactory.load(new File(loadParameter), classOf[SparkJobConfig])
//      case "http" => ConfigFactory.loadFromApi(loadParameter, classOf[SparkJobConfig])
//      case "str" => ConfigFactory.load(loadParameter, classOf[SparkJobConfig], true)
//      case _ => throw new IllegalArgumentException("非法的配置类型, 需要指定配置类型, 可选[file|http|str]")
//    }
//
//    val session = SparkSupport.createSparkSession(sparkJobConfig, LangType.SCALA)
//    val context = session.sparkContext
//    //广播hbaseHelper
//    val hbaseHelperWarpper: SparkBroadcastWrapper[HbaseHelper] = SparkBroadcastWrapper.wrapper(new BroadcastInitializer[HbaseHelper] {
//      override def init(): HbaseHelper = {
//        HbaseHelper.config(sparkJobConfig.getTaskConfig)
//        val helper = HbaseHelper.getInstance()
//        sys.addShutdownHook {
//          helper.close();
//        }
//        helper
//      }
//    })
//    val hbaseHelperBroadcast = context.broadcast(hbaseHelperWarpper)
//
//    val taskConfig = sparkJobConfig.getTaskConfig
//    val sql = taskConfig.getStringVal("execute_sql")
//    val frame = session.sql(sql)
//    logger.info("sql执行结果: {}", frame.count())
//
//    frame.foreachPartition(p => {
//      logger.info("开始执行foreachPartition, 当前partition: {}", p)
//      p.map(r => {
//        logger.info("执行map方法: {}", r)
//        val hbaseHelper = hbaseHelperBroadcast.value.getObj
//        val testKey: String = "0000013e7b2cc1d889b3fc2cf7ab2a11"
//        hbaseHelper.selectRow("url_hbase_v1", testKey);
//      })
//    })
//
//    frame.show()
//    //准备规则引擎包装类,用来进行广播
//    val ruleEngineWrapper: SparkBroadcastWrapper[RuleEngine] =
//      SparkBroadcastWrapper.wrapper(new BroadcastInitializer[RuleEngine] {
//        //初始化规则引擎的函数
//      override def init(): RuleEngine = {
//        val ruleEngine = RuleEngineFactory.builder.config(null)
//          .`type`(EngineType.DROOLS)
//          .init(true)
//          .build
//        ruleEngine.start()
//        ruleEngine
//      }
//    })
//    //使用spark广播
//    val ruleEngineBroadcast = context.broadcast(ruleEngineWrapper)
//    //执行
//    ruleEngineBroadcast.value.getObj.execute(new JSONObject())
//
//  }
//}
//
