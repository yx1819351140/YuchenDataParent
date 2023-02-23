package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.runtime.java.news.process.NewsProcessor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 11:17
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: NewsProcessOperator
 * @Description: 新闻处理节点
 * 1. 数据规整
 * 1.1 ID生成
 * 1.2 WebSite处理
 * 1.3 字段重命名
 * 1.4 语种判断
 * 1.5 时间字段处理
 * <p>
 * 2. 媒体关联
 * 2.1 启动时加载媒体表
 * 2.2 在算子中关联媒体表(domain=url)
 * 3. 国家关联
 * 1. 加载国家表
 * 2. 媒体国家关联
 **/
public class NewsProcessOperator extends ProcessFunction<JSONObject, JSONObject> {

    private NewsProcessor processor;
    private String operatorName;


    public NewsProcessOperator(NewsProcessor processor, String operatorName) {
        this.processor = processor;
        this.operatorName = operatorName;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        //加载媒体表
        //加载国家表
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void processElement(JSONObject value, ProcessFunction<JSONObject, JSONObject>.Context ctx, Collector<JSONObject> out) throws Exception {
        //定制化处理数据
        try {
            processor.process(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
