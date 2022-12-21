package com.yuchen.etl.core.java.flink.sink;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 15:02
 * @Package: com.yuchen.etl.core.java.flink.sink
 * @ClassName: FileFormat
 * @Description:
 **/
public enum FileFormat {
    PARQUET,
    AVRO,
    ORC,
    TEXT;
}
