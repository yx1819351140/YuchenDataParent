package com.yuchen.data.service.config;

import com.yuchen.common.constants.HbaseConstants;
import com.yuchen.common.pub.HbaseHelper;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/13 14:33
 * @Package: com.yuchen.data.service.config
 * @ClassName: HbaseConfig
 * @Description: Hbase配置类
 **/
@Configuration
@ConfigurationProperties
@Data
public class HbaseConfig {
    private Properties hbase; //这里会自动将配置文件中hbase部分注入

    @Bean
    public HbaseHelper hbaseHelper() {
        //这里需要拼接下hbase.这个前缀
        final Properties properties = new Properties();
        hbase.keySet().stream()
                .forEach(k -> properties.setProperty("hbase." + k, hbase.getProperty(k.toString())));
        //配置HbaseHelper
        HbaseHelper.config(properties);
        return HbaseHelper.getInstance();
    }

}
