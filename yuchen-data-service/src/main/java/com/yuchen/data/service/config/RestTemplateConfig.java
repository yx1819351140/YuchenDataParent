package com.yuchen.data.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @description RestTemplateConfig
 * @author: mazhenwei
 * @date: 2022年6月21日
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(SimpleClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }

    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(1800000);
        factory.setConnectTimeout(5000);
        return factory;
    }

}