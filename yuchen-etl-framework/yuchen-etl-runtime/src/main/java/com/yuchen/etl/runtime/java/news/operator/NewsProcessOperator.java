package com.yuchen.etl.runtime.java.news.operator;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.HttpClientResult;
import com.yuchen.common.pub.HttpClientUtil;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.runtime.java.news.process.NewsProcessor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

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

    private final TaskConfig taskConfig;
    private final NewsProcessor processor;
    private final String operatorName;

    private final Map<String, MediaInfo> mediaInfos = new HashMap<>();


    @Override
    public void open(Configuration parameters) throws Exception {
        if(processor != null) {
            processor.init();
        }
    }

    public NewsProcessOperator(TaskConfig taskConfig, NewsProcessor processor, String operatorName) {
        this.taskConfig = taskConfig;
        this.processor = processor;
        this.operatorName = operatorName;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void processElement(JSONObject value, ProcessFunction<JSONObject, JSONObject>.Context ctx, Collector<JSONObject> out) throws Exception {
        //定制化处理数据
        try {
            processor.process(value);
            out.collect(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
