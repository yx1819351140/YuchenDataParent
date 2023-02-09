package com.yuchen.etl.core.java.spark;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/3 13:19
 * @Package: com.yuchen.etl.core.java.spark
 * @ClassName: SparkBroadcastWrapper
 * @Description: 广播变量包装类
 **/
public class SparkBroadcastWrapper<T> implements Serializable {

    private BroadcastInitializer<T> broadcastInitializer;
    private T value;

    public SparkBroadcastWrapper(BroadcastInitializer<T> broadcastInitializer) {
        this.broadcastInitializer = broadcastInitializer;
    }

    public synchronized T getObj() {
        if (value == null) {
            value = broadcastInitializer.init();
        }
        return value;
    }

    public static <T> SparkBroadcastWrapper<T> wrapper(BroadcastInitializer<T> initializer) {
        return new SparkBroadcastWrapper<T>(initializer);
    }

}
