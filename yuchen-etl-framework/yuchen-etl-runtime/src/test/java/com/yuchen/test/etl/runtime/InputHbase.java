package com.yuchen.test.etl.runtime;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.yuchen.common.pub.HbaseHelper;
import org.apache.hadoop.hbase.client.Connection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/2 15:36
 * @Package: com.yuchen.test.etl.runtime
 * @ClassName: InputHbase
 * @Description:
 **/
public class InputHbase {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.put("hbase.zookeeper.quorum", "192.168.12.194");
        properties.put("hbase.zookeeper.property.clientPort", "21811");
        HbaseHelper.config(properties);
        HbaseHelper instance = HbaseHelper.getInstance();

        Connection connection = instance.getConnection();

        // 连接到 mongodb 服务
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://admin:123456@192.168.12.100:27018/?authSource=usinpac&ssl=false"));
        // 连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("usinpac");

        MongoCollection<Document> data_collect_news =
                mongoDatabase.getCollection("data_collect_news");
        System.out.println("data_collect_news.countDocuments(): " + data_collect_news.countDocuments());
        FindIterable<Document> documents = data_collect_news.find();
        MongoCursor<Document> iterator = documents.iterator();
        while (iterator.hasNext()){
            Document next = iterator.next();
            Object id = next.get("_id");
//            Object title = next.get("title");
//            Object publish_time = next.get("publish_time");
//            Object author = next.get("author");
//            Object content = next.get("content");
//            Object content_html = next.get("content_html");
//            Object source = next.get("source");
//            Object keywords = next.get("keywords");
//            Object categories = next.get("categories");
//            Object img_data = next.get("img_data");
//            Object video_data = next.get("video_data");
//            Object url = next.get("url");
//            Object site_name = next.get("site_name");
//            Object insert_time = next.get("insert_time");
//            JSONObject obj = new JSONObject();
            String s = next.toJson();
//            JSONObject jsonObject = JSONObject.parseObject(s);
            instance.insertRecord("data_news_temp_01", id.toString(), "json", "value", s);
            instance.insertRecord("data_news_temp_02", id.toString(), "json", "value", s);
            instance.insertRecord("data_news_temp_03", id.toString(), "json", "value", s);
        }
        System.out.println(data_collect_news);
    }
}
