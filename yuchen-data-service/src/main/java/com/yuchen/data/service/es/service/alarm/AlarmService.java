package com.yuchen.data.service.es.service.alarm;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class AlarmService {
    public static void main(String[] args) throws IOException {
        sendStatusAlarm("告警测试");
    }

    public static void sendAlarm(String content) throws IOException {

        //  创建 client 实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建 post 实例
        HttpPost httpPost = new HttpPost("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=8ec2080a-246f-4b86-97f7-610a0ebb0113");

        // 设置 header
        httpPost.setHeader("Content-Type", "application/json");

        //创建参数集合
        JSONObject paramsJSONObject = new JSONObject();
        JSONObject markdown = new JSONObject();
        markdown.put("content",content);
        paramsJSONObject.put("msgtype","markdown");
        paramsJSONObject.put("markdown",markdown);

        httpPost.setEntity(new StringEntity(paramsJSONObject.toJSONString(), "UTF-8"));

        // 获取相应结果
        CloseableHttpResponse response = httpClient.execute(httpPost);
    }

    public static void sendStatusAlarm(String content) throws IOException {

        //  创建 client 实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建 post 实例
        HttpPost httpPost = new HttpPost("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=cdecf27b-f22f-4167-8ca3-bce1280e50d3");

        // 设置 header
        httpPost.setHeader("Content-Type", "application/json");

        //创建参数集合
        JSONObject paramsJSONObject = new JSONObject();
        JSONObject markdown = new JSONObject();
        markdown.put("content",content);
        paramsJSONObject.put("msgtype","markdown");
        paramsJSONObject.put("markdown",markdown);

        httpPost.setEntity(new StringEntity(paramsJSONObject.toJSONString(), "UTF-8"));

        // 获取相应结果
        CloseableHttpResponse response = httpClient.execute(httpPost);
    }
}
