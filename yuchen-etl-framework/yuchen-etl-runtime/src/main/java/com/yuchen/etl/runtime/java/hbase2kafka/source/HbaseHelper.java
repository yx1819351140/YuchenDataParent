//package com.yuchen.etl.runtime.java.hbase2kafka.source;
//
//import com.yuchen.etl.core.java.config.ConfigFactory;
//import com.yuchen.etl.core.java.config.TaskConfig;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.client.Connection;
//import org.apache.hadoop.hbase.client.ConnectionFactory;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.util.Map;
//
///**
// * Copyright (C), 2023-02-20
// * FileName: HbaseReader
// * Author:   HQP
// * Date:     2023/2/20 13:23
// * Description: 单机模式读取hbase的数据
// */
//public class HbaseHelper {
//
//    Map<String, Object> hbaseConfigs;
//
//    public HbaseHelper(String configPath) throws IOException {
//        // 根据配置文件路径创建hbase相关的配置
//        TaskConfig taskConfig = ConfigFactory.load(configPath, TaskConfig.class);
//        hbaseConfigs = taskConfig.getMap("hbase");
//    }
//
//    public Connection createHbaseConnection() throws IOException, URISyntaxException {
//        // 创建hbase的配置
//        Configuration config = HBaseConfiguration.create();
//        // 循环添加配置文件信息
//        hbaseConfigs.forEach((String key, Object value) ->{
//            config.set(key,String.valueOf(value));
//        });
//        // 返回hbaseConnection
//        return ConnectionFactory.createConnection(config);
//    }
//
//}
