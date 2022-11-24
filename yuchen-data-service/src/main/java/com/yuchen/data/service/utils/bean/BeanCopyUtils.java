package com.yuchen.data.service.utils.bean;

import com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 封装集合拷贝
 *
 * @author admin
 */
public class BeanCopyUtils extends BeanUtils {
    /**
     * 集合拷贝
     *
     * @param sources 数据源
     * @param target  目标
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, null);
    }

    /**
     * 带回调函数的拷贝，可定义字段拷贝规则
     *
     * @param sources  源
     * @param target   目标
     * @param callBack 回调函数
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target, BeanCopyUtilCallBack<S, T> callBack) {
        List<T> list = new ArrayList<>();
        for (S s : sources) {
            T t = target.get();
            copyProperties(s, t);
            list.add(t);
            if (callBack != null) {
                callBack.callBack(s, t);
            }
        }
        return list;
    }


    /**
     * 获取null 值  的字段
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = Sets.newHashSet();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] res = new String[emptyNames.size()];
        return emptyNames.toArray(res);
    }


    /**
     * a->b的值   根据想要的值  给 目标值赋值
     *
     * @param list
     * @param tagTag
     * @return
     */
    public static Object getProperty(List<String> list, Object source, Object tagTag) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        final BeanWrapper tag = new BeanWrapperImpl(tagTag);
        for (PropertyDescriptor pd : pds) {
            boolean contains = list.contains(pd.getName());
            if (contains) {
                Object srcValue = src.getPropertyValue(pd.getName());
                tag.setPropertyValue(pd.getName(), srcValue);
            }
        }
        return tagTag;

    }


    /**
     * a->b的值   根据想要的值  给 目标值赋值
     *
     * @param property
     * @param tagTag
     * @return
     */
    public static Object setOneProperty(String property, Object tagTag, String rep) {
        final BeanWrapper src = new BeanWrapperImpl(tagTag);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        for (PropertyDescriptor pd : pds) {
            if (property.equals(pd.getName())) {
                src.setPropertyValue(pd.getName(), rep);
            }
        }
        return tagTag;

    }


}
