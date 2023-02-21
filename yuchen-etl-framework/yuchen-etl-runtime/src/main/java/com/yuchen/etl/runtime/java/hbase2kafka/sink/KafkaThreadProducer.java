package com.yuchen.etl.runtime.java.hbase2kafka.sink;


import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class KafkaThreadProducer extends  Thread
{
   private  static  Logger logger=LoggerFactory.getLogger(KafkaThreadProducer.class);
   //创建一个最大的线程数
   private  final  static  int max_sise=10;

   /*连接Kafka*/
   public Properties configure()
   {
      Properties properties=new Properties();
      //指定kafka的集群地址
      properties.put("bootstrap.servers","localhost:9092");
      //设置应答机制
      properties.put("acks","1");
      //批量提交大小
      properties.put("batch.size",16384);
      //延时提交
      properties.put("linger.ms",1);
      //缓充大小
      properties.put("buffer.memory",33554432);
      //序列化主键
      properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
      //序列化值
      properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
      return properties;
   }

   public  void run()
   {
      Producer<String, String> objProducer=new org.apache.kafka.clients.producer.KafkaProducer<String, String>(this.configure());
      //模拟发送批量的数据
      for(int i=0;i<10000;i++)
      {
         JSONObject jsonObject=new JSONObject();
         jsonObject.put("id",i);
         jsonObject.put("username","无涯");
         jsonObject.put("city","西安");
         jsonObject.put("age",18);
         jsonObject.put("date",new Date().toString());
         //异步发送，调用回调函数,给主题login写入数据
         objProducer.send(new ProducerRecord<String, String>("login", jsonObject.toJSONString()), new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e)
            {
               if(e!=null)
               {
                  logger.error("send error:"+e.getMessage());
               }
               else
               {
                  logger.info("writing kafka success:"+recordMetadata.offset());
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

   public static void main(String[] args)
   {
      ExecutorService executorService = Executors.newFixedThreadPool(max_sise);
      //提交任务批量执行
      executorService.submit(new KafkaThreadProducer());
      //关闭线程池
      executorService.shutdown();

   }

}