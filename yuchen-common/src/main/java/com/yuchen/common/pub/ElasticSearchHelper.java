package com.yuchen.common.pub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/13 14:00
 * @Package: com.yuchen.common.pub
 * @ClassName: ElasticSearchHelper
 * @Description: Es单例工具类, 使用该类需要在工程中引入三个es依赖
 * 1.elasticsearch-rest-high-level-client
 * 2.elasticsearch-rest-client
 * 3.elasticsearch
 **/
public class ElasticSearchHelper implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchHelper.class);
    private static volatile ElasticSearchHelper instance;
    private static volatile Properties configs;

    private ElasticSearchHelper() {
    }

    public static ElasticSearchHelper getInstance() {
        if (instance == null) {
            synchronized (ElasticSearchHelper.class) {
                if (instance == null) {
                    instance = new ElasticSearchHelper();
                    instance.init();
                }
            }
        }
        return instance;
    }

    private void init() {
        if (configs == null) {
            configs = new Properties();
        }
    }

    public synchronized static void config(Properties properties) {
        configs = properties;
        getInstance();
    }

    public synchronized static void config(Map confMap) {
        Properties properties = new Properties();
        properties.putAll(confMap);
        config(properties);
    }


}
