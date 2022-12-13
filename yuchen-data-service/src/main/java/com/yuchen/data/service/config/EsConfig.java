package com.yuchen.data.service.config;

import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.common.pub.HbaseHelper;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/13 15:06
 * @Package: com.yuchen.data.service.config
 * @ClassName: EsConfig
 * @Description: Es配置类
 **/
@Configuration
@ConfigurationProperties
@Data
public class EsConfig {
    private Properties es; //这里会自动将配置文件中hbase部分注入

    @Bean
    public ElasticSearchHelper elasticSearchHelper() {
        //这里需要拼接下hbase.这个前缀
        final Properties properties = new Properties();
        //配置HbaseHelper
        ElasticSearchHelper.config(properties);
        return ElasticSearchHelper.getInstance();
    }

}
