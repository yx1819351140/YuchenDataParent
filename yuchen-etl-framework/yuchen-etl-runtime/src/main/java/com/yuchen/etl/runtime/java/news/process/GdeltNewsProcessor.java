package com.yuchen.etl.runtime.java.news.process;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.core.java.config.TaskConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 13:06
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: GdeltNewsProcessor
 * @Description:
 **/
public class GdeltNewsProcessor extends GenericNewsProcessor {

    public GdeltNewsProcessor(TaskConfig taskConfig) {
        super(taskConfig);
    }


    // TODO
    // 1.title_id处理
    // 2.content,url,lang
    // 3.domain
    // 4.category
    // 5.time
    // 6.建立字段间的映射关系：

    @Override
    public void  process(JSONObject value) throws Exception {
        //生成ID
        JSONObject data = value.getJSONObject("data");
        // 生成ID:title_id
        handleNewsTitle(data);

        // 过滤脏数据
        filterFields(data);

        // 获得原始domain, 或者从url中提取domain
        handleWebSite(data);

        // gdelt中的catalog处理为category
        handleCatalog(data);

        //添加媒体
        handleMediaInfo(data);

        //处理数据时间,从value中获取时间戳,写入到data中
        handleDataTime(value, data);

        //data数据放回value
        value.put("data", data);
    }

    private static void handleDataTime(JSONObject value, JSONObject data) {
        Long timestamp = value.getLong("timestamp");
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        data.put("create_time", time);
        data.put("pub_time", time);
        data.put("pub_timestamp", timestamp);
    }
}
