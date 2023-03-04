package com.yuchen.etl.runtime.java.news.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.core.java.config.TaskConfig;
import org.elasticsearch.client.indexlifecycle.IndexLifecycleNamedXContentProvider;

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
    public void process(JSONObject value) throws Exception {
        //生成ID
        JSONObject news = value.getJSONObject("data");
        if(news != null) {
            value.putAll(news);
        }
        // 生成ID:title_id
        handleNewsTitle(value);

        // 过滤脏数据
        filterFields(value);

        // 获得原始domain, 或者从url中提取domain
        handleWebSite(value);

        // gdelt中的catalog处理为category
        handleCatalog(value);

        //添加媒体
        handleMediaInfo(value);

        //处理数据时间
        handleDataTime(value);

        System.out.println("正在处理Gdelt数据: " + value.toJSONString());
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
