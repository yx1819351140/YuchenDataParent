package com.yuchen.dw.udfs.scala.common

import org.apache.hadoop.hive.ql.udf.generic.GenericUDF
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 11:26
 * @Package: com.yuchen.dw.udfs.scala.common
 * @ClassName: CommonUDF
 * @Description: $END
 * */
class CommonUDF extends GenericUDF {
  override def initialize(objectInspectors: Array[ObjectInspector]): ObjectInspector = {
    objectInspectors(0)
  }

  override def evaluate(deferredObjects: Array[GenericUDF.DeferredObject]): AnyRef = {
    "test udf"
  }

  override def getDisplayString(strings: Array[String]): String = {
    "test msg"
  }
}
