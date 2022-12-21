package com.yuchen.etl.core.java.flink.sink;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.table.data.RowData;

import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 14:57
 * @Package: com.yuchen.etl.core.java.flink.sink
 * @ClassName: FlinkSink
 * @Description: Sink操作接口
 **/
public interface FlinkSink<T> {

    FlinkSink<T> init();

    Sink<T> getSink();
}
