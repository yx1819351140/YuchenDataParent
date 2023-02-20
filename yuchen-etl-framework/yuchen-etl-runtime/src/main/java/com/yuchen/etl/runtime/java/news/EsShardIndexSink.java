package com.yuchen.etl.runtime.java.news;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 13:53
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: EsShardIndexSink
 * @Description: ES分片索引Sink
 **/
public class EsShardIndexSink extends RichSinkFunction<JSONObject> {


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(JSONObject value, Context context) throws Exception {
    }

    @Override
    public void finish() throws Exception {
        super.finish();
    }
}
