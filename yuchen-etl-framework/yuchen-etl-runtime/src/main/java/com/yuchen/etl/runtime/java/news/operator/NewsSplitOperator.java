package com.yuchen.etl.runtime.java.news.operator;

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
 * @Description: 新闻流拆分算子
 **/
public class NewsSplitOperator extends ProcessFunction<JSONObject, JSONObject> {

    private Map<String, OutputTag<JSONObject>> tagMap;

    public NewsSplitOperator(Map<String, OutputTag<JSONObject>> tagMap) {
        this.tagMap = tagMap;
    }

    @Override
    public void processElement(JSONObject value, ProcessFunction<JSONObject, JSONObject>.Context ctx, Collector<JSONObject> out) throws Exception {
        //从数据中获取数据来源topic
        final String topic = value.getString("topic");
        if (topic != null) {
            //根据数据中的topic来选择对应的流进行输出
            final OutputTag<JSONObject> tag = tagMap.get(topic);
            if (tag != null) {
                //把数据发送到对应的tag 也就是对应的流中  tag1-yuchen-news_gdelt ---> stream-yuchen-news-gdelt
                ctx.output(tag, value);
            }
        }
        //如果没有对应的流处理,发送到原始流中.
        out.collect(value);
    }
}
