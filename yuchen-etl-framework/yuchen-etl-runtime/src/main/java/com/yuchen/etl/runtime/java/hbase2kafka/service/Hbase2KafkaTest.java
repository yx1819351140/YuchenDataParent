package com.yuchen.etl.runtime.java.hbase2kafka.service;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.HbaseHelper;
import com.yuchen.common.utils.DateUtils;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.runtime.java.hbase2kafka.source.HbaseDAO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: hqp
 * @Date: 2023/2/20 13:09
 * @Package: com.yuchen.etl.runtime.java.hbase2kafka
 * @ClassName: Hbase2KafkaTest
 * @Description: 规则引擎测试程序
 **/
public class Hbase2KafkaTest {
    public static void main(String[] args) throws Exception {
        String configPath = args[0];
        TaskConfig config = ConfigFactory.load(configPath, TaskConfig.class);
        Map<String, Object> hbaseConfig = config.getMap("hbase");
        Map<String, Object> transformationConfigs = config.getMap("transformationConfig");
        Map<String, Object> kafkaConfigs = config.getMap("kafka");
        AtomicInteger countAll = new AtomicInteger();
        HbaseHelper.config(hbaseConfig);
        HbaseHelper hbaseHelper = HbaseHelper.getInstance(); // 双重验证的单例模式

        long currenTimestamp = DateUtils.getCurrenTimestamp();

        HbaseDAO hbaseDAO = new HbaseDAO(hbaseHelper, hbaseConfig, transformationConfigs);
        Integer threshold = Integer.valueOf(transformationConfigs.get("threshold").toString());
        List<JSONObject> jsonResults = hbaseDAO.getJSONResultLowerThreshold(threshold);

        // 测试hbase批量数据读取,这里的数据过滤则需要加到service层处理了，以保证每一层的职责单一性
        // 初始的过滤需求,这一块的功能需要抽象出去：
        // 1.必须存在的字段检查
        // 2.时间范围筛选
        jsonResults.forEach(jsonResult -> {
            countAll.getAndIncrement();
            System.out.println("read batch data success：" + jsonResult.toString());
        });

        hbaseHelper.close();

//        // 线程池批量写入kafka
//        KafkaThreadProducer kafkaThreadProducer = new KafkaThreadProducer(configPath,1);
//        kafkaThreadProducer.setJsonResults(jsonResults);
//        ExecutorService executorService= Executors.newFixedThreadPool(kafkaThreadProducer.getMaxSize());
//        // 提交任务批量执行
//        executorService.submit(kafkaThreadProducer);
//        // 关闭线程池
//        executorService.shutdown();
        System.out.println(countAll);
        System.out.println("processing time is:" + String.valueOf(DateUtils.getCurrenTimestamp() - currenTimestamp));
        System.out.println("program is completed,it will be closed after 60s!");

        // main主线程休眠60秒,退出
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
