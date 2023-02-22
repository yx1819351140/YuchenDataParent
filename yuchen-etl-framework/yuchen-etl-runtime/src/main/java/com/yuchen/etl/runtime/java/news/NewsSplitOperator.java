package com.yuchen.etl.runtime.java.news;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 13:32
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: NewsSplitOperator
 * @Description: 新闻流拆分
 **/
public class NewsSplitOperator extends ProcessFunction<JSONObject, JSONObject> {

    private Map<String, OutputTag<JSONObject>> tagMap;

    public NewsSplitOperator(Map<String, OutputTag<JSONObject>> tagMap) {
        this.tagMap = tagMap;
    }

    @Override
    public void processElement(JSONObject value, ProcessFunction<JSONObject, JSONObject>.Context ctx, Collector<JSONObject> out) throws Exception {
        System.out.println("value: " + value);
        String topic = value.getString("topic");
        ctx.output(tagMap.get(topic), value);

    }
}
