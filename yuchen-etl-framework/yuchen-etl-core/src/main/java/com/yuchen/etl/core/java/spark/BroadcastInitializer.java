package com.yuchen.etl.core.java.spark;


/**
 * @Author: xiaozhennan
 * @Date: 2023/1/3 13:29
 * @Package: com.yuchen.etl.core.java.spark
 * @ClassName: BroadcastInitializer
 * @Description:
 **/

@FunctionalInterface
public interface BroadcastInitializer<T> {

    T init();

}
