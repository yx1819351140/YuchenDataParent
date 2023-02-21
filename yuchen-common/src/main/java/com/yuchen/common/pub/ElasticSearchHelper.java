package com.yuchen.common.pub;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

    private RestHighLevelClient restHighLevelClient;

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

    private HttpHost[] getHosts(String esHosts, String esPort) {
        String[] split = esHosts.split(",");
        List<HttpHost> hosts = new ArrayList<HttpHost>();
        for (String host : split) {
            hosts.add(new HttpHost(host, Integer.parseInt(esPort), "http"));
        }
        return hosts.toArray(new HttpHost[]{});
    }

    private void init() {
        if (configs == null) {
            configs = new Properties();
        }
        String esHosts = configs.getProperty("elasticsearch.hosts");
        String esPort = configs.getProperty("elasticsearch.port");
        //获取hosts
        HttpHost[] hosts = getHosts(esHosts, esPort);
        restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(hosts).setHttpClientConfigCallback(
                        new RestClientBuilder.HttpClientConfigCallback() {
                            @Override
                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                                httpAsyncClientBuilder.disableAuthCaching();
                                return httpAsyncClientBuilder;
                            }
                        }
                ));
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


    public RestHighLevelClient getEsClient() {
        return restHighLevelClient;
    }
}
