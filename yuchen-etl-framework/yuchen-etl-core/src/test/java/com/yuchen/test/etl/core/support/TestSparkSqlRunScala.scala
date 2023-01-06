package com.yuchen.test.etl.core.support

import com.yuchen.common.enums.LangType
import com.yuchen.etl.core.java.config.{ConfigFactory, SparkJobConfig}
import com.yuchen.etl.core.java.spark.SparkSupport

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/23 11:17
 * @Package: com.yuchen.test.etl.runtime.support
 * @ClassName: TestSparkSqlRunScala
 * @Description: $END
 * */
object TestSparkSqlRunScala {
  def main(args: Array[String]): Unit = {
    val sparkJobConfig = ConfigFactory.load("yuchen-etl-framework/yuchen-etl-runtime/src/test/resources/job.json", classOf[SparkJobConfig])

    val session = SparkSupport
      .createSparkSession(sparkJobConfig, LangType.SCALA)

    val frame = session
      .read.json("yuchen-etl-framework/yuchen-etl-core/src/test/resources/job.json")

    frame.show()
    //
    //    frame.createTempView("test_t")
    //
    //    session.sql("select * from test_t")


  }
}
