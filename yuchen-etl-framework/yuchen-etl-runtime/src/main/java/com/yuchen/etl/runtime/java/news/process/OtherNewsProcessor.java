package com.yuchen.etl.runtime.java.news.process;

import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.runtime.java.news.process.GenericNewsProcessor;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/23 13:09
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: OtherNewsProcessor
 * @Description: 其它类型新闻处理器
 **/
public class OtherNewsProcessor extends GenericNewsProcessor {

    public OtherNewsProcessor(TaskConfig taskConfig) {
        super(taskConfig);
    }
}
