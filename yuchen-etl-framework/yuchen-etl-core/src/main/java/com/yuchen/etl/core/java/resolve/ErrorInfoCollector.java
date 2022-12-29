package com.yuchen.etl.core.java.resolve;

/**
 * @Author: xiaozhennan
 * @Date: 2021/6/1 23:18
 * @Package: com.weiwan.dsp.api.context
 * @ClassName: UnresolvedDataCollector
 * @Description: 未解析日志收集器接口
 **/
public interface ErrorInfoCollector {

    /**
     * 输出未解析日志,这里是异步处理的
     *
     * @param type       未解析类型
     * @param dataRecord 数据
     */
//    void collect(ErrorInfoType type, Object info);
    void collect(LogType type,LogLevel level, LogSource source, String model, String content, Throwable error);
    void open(ErrorInfoCollectorConfig config);
    void close();


}
