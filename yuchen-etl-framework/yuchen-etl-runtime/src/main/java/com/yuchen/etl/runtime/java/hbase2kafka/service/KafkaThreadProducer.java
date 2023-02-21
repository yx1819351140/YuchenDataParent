package com.yuchen.etl.runtime.java.hbase2kafka.service;


import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.runtime.java.hbase2kafka.sink.KafkaHelper;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class KafkaThreadProducer extends  Thread
{
   private  static  Logger logger=LoggerFactory.getLogger(KafkaThreadProducer.class);

   public int getMaxSize() {
      return maxSize;
   }

   public void setMaxSize(int maxSize) {
      this.maxSize = maxSize;
   }

   //创建一个最大的线程数
   private  int maxSize;

   private List<JSONObject> jsonResults; // send messages to Kafka

   private final org.apache.kafka.clients.producer.KafkaProducer<String, String> kafkaProducer;

   public KafkaThreadProducer(String configPath, int maxSize) throws IOException, URISyntaxException {
      // 根据配置文件路径创建hbase相关的配置
      this.kafkaProducer = new KafkaHelper(configPath).createKafkaProducer();
      this.maxSize = maxSize;
   }

   public  void run()
   {
      Producer<String, String> objProducer=this.kafkaProducer;
      //模拟发送批量的数据
      for(JSONObject newsJSONObject : jsonResults){
         //异步发送，调用回调函数,给主题login写入数据
         objProducer.send(new ProducerRecord<String, String>("hbase2kafka_test", newsJSONObject.toJSONString()), new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e)
            {
               if(e!=null)
               {
                  logger.error("发送错误，信息具体为:"+e.getMessage());
               }
               else
               {
                  logger.info("写入的数据为:"+recordMetadata.offset());
               }
            }
         });
      }

      try{
         Thread.sleep(3000);
      }catch(Exception e){
         e.printStackTrace();
      }
      //关闭生产者的对象
      objProducer.close();
   }

   public List<JSONObject> getJsonResults() {
      return jsonResults;
   }

   public void setJsonResults(List<JSONObject> jsonResults) {
      this.jsonResults = jsonResults;
   }

   public static void main(String[] args) throws IOException, URISyntaxException {
      String configPath = args[0];

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("context","11111111");
      jsonObject.put("title","11111111");

      KafkaThreadProducer kafkaThreadProducer = new KafkaThreadProducer(configPath,1);

      ExecutorService executorService= Executors.newFixedThreadPool(kafkaThreadProducer.getMaxSize());
      //提交任务批量执行
      executorService.submit(new KafkaThreadProducer(args[0],1));
      //关闭线程池
      executorService.shutdown();
   }
}