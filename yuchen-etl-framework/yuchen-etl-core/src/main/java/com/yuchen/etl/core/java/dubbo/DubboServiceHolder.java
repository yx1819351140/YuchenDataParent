package com.yuchen.etl.core.java.dubbo;

import com.yuchen.common.utils.JsonExtractTool;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 13:53
 * @Package: com.yuchen.etl.core.java.dubbo
 * @ClassName: DubboServiceHolder
 * @Description: Dubbo服务调用
 **/
public class DubboServiceHolder {
    private static DubboServiceHolder instance;
    private static Properties configs;

    private Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private ApplicationConfig application = new ApplicationConfig();
    private List<RegistryConfig> registries = new ArrayList<>();

    public static DubboServiceHolder getInstance() {
        if (instance == null) {
            synchronized (JsonExtractTool.class) {
                if (instance == null) {
                    instance = new DubboServiceHolder();
                    instance.init();
                }
            }
        }
        return instance;
    }

    public synchronized static void config(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Parameter cannot be empty please check");
        }
        if (configs == null) {
            configs = properties;
        }
        if (properties != null && configs != null) {
            for (Object key : properties.keySet()) {
                //覆盖
                configs.put(key, properties.getProperty(key.toString()));
            }
        }
        getInstance();
    }


    private void init() {
        if (configs == null) {
            try {
                configs = new Properties();
                configs.load(DubboServiceHolder.class.getResourceAsStream("/etl-core.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        application.setName(configs.getProperty("dubbo.application.name", "etl-core-app"));
        application.setLogger("slf4j");

        // 连接注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(configs.getProperty("dubbo.registry.address", "zookeeper://127.0.0.1:2181"));
        registries.add(registry);
    }

    private DubboServiceHolder() {
    }

    public <T> T getService(Class<T> clazz, String version, String... tag) {
        if (clazz == null || StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("DubboServiceHolder#getService(): 参数不能为空.");
        }
        //服务的key: com.yuchen.data.api.service.IEsService:1.0.0
        String key = StringUtils.join(clazz.getName(), ":", version);
        Object serviceRef = serviceMap.get(key);
        if (serviceRef != null && !StringUtils.equals(serviceRef.getClass().getName(), clazz.getName())) {
            throw new IllegalArgumentException("DubboServiceHolder#getService(): 返回值类型不符.期望:" + clazz.getName() + ",实际:" + serviceRef.getClass().getName());
        }
        if (serviceMap.containsKey(key)) {
            return (T) serviceRef;
        }

        ReferenceConfig reference = createReference();
        // 设置tag：用于灰度发布、线上程序调试
        otherSetting(reference, tag);
        // 设置接口版本
        reference.setVersion(version);
        // 设置接口class
        reference.setInterface(clazz);
        // 获取代理对象
        serviceRef = reference.get();
        //使用key将代理对象缓存
        serviceMap.put(key, serviceRef);
        return (T) serviceRef;
    }

    private void otherSetting(ReferenceConfig reference, String[] tag) {
        String tagEnv = getTag(tag);
        if (StringUtils.isNotBlank(tagEnv)) {
            ConsumerConfig consumerConfig = new ConsumerConfig();
            consumerConfig.setTag(tagEnv);
            reference.setConsumer(consumerConfig);
        }
    }

    private ReferenceConfig createReference() {
        // 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
        ReferenceConfig reference = new ReferenceConfig();
        reference.setApplication(application);
        reference.setRegistries(registries);
        reference.setCheck(false);
        return reference;
    }

    private String getTag(String... tag) {
        String tagEnv = System.getProperty("dubbo.provider.tag");
        if (tag != null && tag.length > 0 && StringUtils.isNotBlank(tag[0])) {
            tagEnv = tag[0];
        }
        return tagEnv;
    }
}
