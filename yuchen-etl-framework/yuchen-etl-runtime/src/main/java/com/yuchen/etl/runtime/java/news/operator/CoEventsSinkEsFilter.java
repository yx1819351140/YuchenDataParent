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
public class CoEventsSinkEsFilter extends RichFilterFunction<JSONObject> {
    @Override
    public boolean filter(JSONObject value) throws Exception {
        //如果是更新数据,过滤掉
//        Boolean isUpdate = value.getBoolean("isUpdate");
//        if (isUpdate != null && isUpdate) {
//            return false;
//        }

        // 如果data中的数据不是json格式则过滤掉,非json格式的数据不要
        if(value.containsKey("data")){
            Object data = value.get("data");
            if (data == null) {
                return false;
            }else {
                if (!JsonUtil.isJSONValid(data.toString())) {
                    return false;
                }
            }
        }else {
            return false;
        }

        // 空数据不要
        if (value == null || value.size() == 0) {
            return false;
        }

        return true;
    }
}
