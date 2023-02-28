package com.yuchen.etl.runtime.java.news.process;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.core.java.config.TaskConfig;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 13:23
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: CollectNewsProcessor
 * @Description: 采集类新闻处理
 **/
public class CollectNewsProcessor extends GenericNewsProcessor {
    public CollectNewsProcessor(TaskConfig taskConfig) {
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
    }
}
