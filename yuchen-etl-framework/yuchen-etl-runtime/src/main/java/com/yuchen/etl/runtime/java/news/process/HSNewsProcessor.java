package com.yuchen.etl.runtime.java.news.process;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.core.java.config.TaskConfig;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/23 13:08
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: HSNewsProcessor
 * @Description:
 **/
public class HSNewsProcessor extends GenericNewsProcessor {

    public HSNewsProcessor(TaskConfig taskConfig) {
        super(taskConfig);
    }

    @Override
    public void process(JSONObject value) throws Exception {
        super.process(value);
    }
}
