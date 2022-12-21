package com.yuchen.test.etl.core.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/21 16:52
 * @Package: com.yuchen.test.etl.core.support
 * @ClassName: TestConfigLoad
 * @Description:
 **/
public class TestConfigLoad {
    public static void main(String[] args) throws JsonProcessingException {
        FlinkJobConfig flinkJobConfig = ConfigFactory.loadFromApi(args[0], FlinkJobConfig.class);
        System.out.println(flinkJobConfig);
        flinkJobConfig.printInfo();
    }
}
