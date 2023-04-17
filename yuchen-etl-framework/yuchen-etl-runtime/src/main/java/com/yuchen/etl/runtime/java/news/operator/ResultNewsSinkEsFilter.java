package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.utils.JsonUtil;
import org.apache.flink.api.common.functions.RichFilterFunction;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/20 13:48
 * @Package: com.yuchen.etl.runtime.java.news.operator
 * @ClassName: FinalNewsSinkKafkaFilter
 * @Description:
 **/
public class ResultNewsSinkEsFilter extends RichFilterFunction<JSONObject> {
    @Override
    public boolean filter(JSONObject value) throws Exception {
        //如果是更新数据,过滤掉
//        Boolean isUpdate = value.getBoolean("isUpdate");
//        if (isUpdate != null && isUpdate) {
//            return false;
//        }

        // 非json格式的数据不要
        if (!JsonUtil.isJSONValid(value.toJSONString())) {
            return false;
        }

        // 空数据不要
        if (value == null || value.size() == 0) {
            return false;
        }

        return true;
    }
}
