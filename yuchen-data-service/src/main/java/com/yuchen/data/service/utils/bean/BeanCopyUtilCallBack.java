package com.yuchen.data.service.utils.bean;

/**
 *
 * @author admin
 * @param <S>
 * @param <T>
 */
@FunctionalInterface
public interface BeanCopyUtilCallBack<S,T> {
    /**
     *定义默认回调方法
     * @param s
     * @param t
     */
    void callBack(S s, T t);
}
