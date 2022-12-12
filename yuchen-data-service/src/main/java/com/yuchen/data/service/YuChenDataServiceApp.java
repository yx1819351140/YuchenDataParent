package com.yuchen.data.service;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 10:52
 * @Package: com.yuchen.data.service
 * @ClassName: YuChenDataServiceApp
 * @Description: 数据服务应用类
 **/
@SpringBootApplication
@SpringBootConfiguration
@EnableAsync
@EnableDubbo
@EnableSwagger2
public class YuChenDataServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(YuChenDataServiceApp.class, args);
    }
}
