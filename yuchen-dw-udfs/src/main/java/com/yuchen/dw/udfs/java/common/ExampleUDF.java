package com.yuchen.dw.udfs.java.common;
import org.apache.hadoop.hive.ql.exec.UDF;


/**
 * 这种是过时写法,也可以用
 */
public class ExampleUDF   extends UDF{
    public  String evaluate(String str) {
        try {
            return "HelloWorld " + str;
        } catch (Exception e) {
            return null;
        }
    }
}