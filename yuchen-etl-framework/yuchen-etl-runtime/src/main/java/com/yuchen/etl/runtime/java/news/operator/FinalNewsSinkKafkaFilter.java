package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFilterFunction;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/20 13:48
 * @Package: com.yuchen.etl.runtime.java.news.operator
 * @ClassName: FinalNewsSinkKafkaFilter
 * @Description:
 **/
public class FinalNewsSinkKafkaFilter extends RichFilterFunction<JSONObject> {
    @Override
    public boolean filter(JSONObject value) throws Exception {
        //如果是更新数据,过滤掉
//        Boolean isUpdate = value.getBoolean("isUpdate");
//        if (isUpdate != null && isUpdate) {
//            return false;
//        }

        // 没有关联到信源的数据过滤掉
        JSONObject data = value.getJSONObject("data");
        Object media = data.get("media");
        return media != null;
    }
}
