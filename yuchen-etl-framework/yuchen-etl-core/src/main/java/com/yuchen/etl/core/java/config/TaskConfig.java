package com.yuchen.etl.core.java.config;

import com.yuchen.common.pub.AbstractConfig;
import com.yuchen.common.pub.BaseConfig;
import lombok.Getter;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 16:50
 * @Package: com.yuchen.etl.core.java.config
 * @ClassName: TaskConfig
 * @Description: 任务配置
 **/
public class TaskConfig extends BaseConfig {
    public TaskConfig() {
    }

    public TaskConfig(Map m) {
        super(m);
    }
}
