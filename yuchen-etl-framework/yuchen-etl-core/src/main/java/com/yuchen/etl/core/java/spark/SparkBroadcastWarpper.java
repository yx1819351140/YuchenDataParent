package com.yuchen.etl.core.java.spark;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/3 13:19
 * @Package: com.yuchen.etl.core.java.spark
 * @ClassName: SparkBroadcastWarpper
 * @Description: 广播变量包装类
 **/
public class SparkBroadcastWarpper<T> implements Serializable {

    private BroadcastInitializer<T> broadcastInitializer;
    private T value;

    public SparkBroadcastWarpper(BroadcastInitializer<T> broadcastInitializer) {
        this.broadcastInitializer = broadcastInitializer;
    }

    public synchronized T getObj() {
        if (value == null) {
            value = broadcastInitializer.init();
        }
        return value;
    }

    public static <T> SparkBroadcastWarpper<T> wrapper(BroadcastInitializer<T> initializer) {
        return new SparkBroadcastWarpper<T>(initializer);
    }

}
