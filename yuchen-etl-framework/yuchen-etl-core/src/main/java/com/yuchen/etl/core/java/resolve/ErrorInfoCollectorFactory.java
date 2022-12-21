package com.yuchen.etl.core.java.resolve;


import com.yuchen.common.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xiaozhennan
 * @date: 2021/6/21 18:38
 * @description:
 */
public class ErrorInfoCollectorFactory {

    private static final Logger _LOGGER = LoggerFactory.getLogger(ErrorInfoCollectorFactory.class);

    private static volatile ErrorInfoCollectorFactory instance;
    private static final Map<String, ErrorInfoCollector> collectors = new ConcurrentHashMap<>();

    public static ErrorInfoCollectorFactory getInstance() {
        if (instance == null) {
            synchronized (ErrorInfoCollectorFactory.class) {
                if (instance == null) {
                    instance = new ErrorInfoCollectorFactory();
                }
            }
        }
        return instance;
    }

    private ErrorInfoCollectorFactory() {
    }

    public static ErrorInfoCollector createCollector(final ErrorInfoCollectorConfig config) {
        return getInstance().create(config);
    }

    private ErrorInfoCollector create(final ErrorInfoCollectorConfig config) {
        //反射创建handler
        String collectorHandler = config.getCollectorHandler();
        if (collectors.get(collectorHandler) != null) {
            return collectors.get(collectorHandler);
        }
        long samplingInterval = config.getSamplingInterval();
        long maxSamplingRecord = config.getMaxSamplingRecord();
        AbstractErrorInfoCollector collector = null;
        Class<?> aClass = null;
        try {
            aClass = Class.forName(collectorHandler);
            collector = (AbstractErrorInfoCollector) aClass.newInstance();
            ReflectUtil.setFieldValue(AbstractErrorInfoCollector.class, collector, "maxSamplingRecord", maxSamplingRecord);
            ReflectUtil.setFieldValue(AbstractErrorInfoCollector.class, collector, "samplingInterval", samplingInterval);
        } catch (ClassNotFoundException e) {
            _LOGGER.error("Unable to find corresponding unresolved processor class", e);
        } catch (IllegalAccessException e) {
            _LOGGER.error("The corresponding unresolved processor class constructor cannot be found", e);
        } catch (InstantiationException e) {
            _LOGGER.error("Exception creating unresolved processor", e);
        }

        if (collector != null) {
            collector.open(config);
        }
        collectors.put(collectorHandler, collector);
        return collector;
    }


}
