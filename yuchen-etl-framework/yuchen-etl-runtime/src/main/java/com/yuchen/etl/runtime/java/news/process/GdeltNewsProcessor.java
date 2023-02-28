package com.yuchen.etl.runtime.java.news.process;

import com.alibaba.fastjson.JSONArray;
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

    @Override
    public void process(JSONObject value) throws Exception {

        //生成ID
        String title = value.getString("title");
        if (value.get("id") == null && value.getString("title") != null) {
            String id = generateID(title);
            value.put("id", id);
        }

        //从url中提取domain
        handleWebSite(value);

        //添加媒体
        handleMediaInfo(value);

        //gdelt中的catalog
        JSONArray yuchenNewsCatalogue = value.getJSONArray("yuchen_news_catalogue");
        value.put("category", yuchenNewsCatalogue);

        //处理数据时间
        handleDataTime(value);

        System.out.println("正在处理gdelt数据: " + value.toJSONString());
    }

    private static void handleDataTime(JSONObject value) {
        Long timestamp = value.getLong("timestamp");
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        value.put("create_time", time);
        value.put("pub_time", time);
        value.put("pub_timestamp", timestamp);
    }
}
