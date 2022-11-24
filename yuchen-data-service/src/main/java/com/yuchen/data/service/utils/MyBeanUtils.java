package com.yuchen.data.service.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zhengyanjuan
 * @date: 2021/1/25 13:09
 */
public class MyBeanUtils {


    /**
     * bean转换
     * @param sourceBean
     * @param targetBeanClass
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> E copyBean(T sourceBean, Class<E> targetBeanClass) {
        if (null == sourceBean) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(sourceBean), targetBeanClass);
    }

    /**
     * list转换
     * @param sourceList
     * @param targetListClass
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> List copyList(List<T> sourceList, Class<E> targetListClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList();
        }
        return JSON.parseArray(JSON.toJSONString(sourceList), targetListClass);
    }
}
