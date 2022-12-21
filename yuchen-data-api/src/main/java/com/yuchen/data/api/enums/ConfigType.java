package com.yuchen.data.api.enums;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/21 15:10
 * @Package: com.yuchen.data.api.enums
 * @ClassName: ConfigType
 * @Description:
 **/
public enum ConfigType {
    FLINK_JOB_CONFIG(1),
    SPARK_JOB_CONFIG(2),
    MYSQL_CONFIG(3),
    HBASE_CONFIG(4),
    HDFS_CONFIG(5),
    MONGO_CONFIG(6),
    ES_CONFIG(7),
    KAFKA_CONFIG(8);


    private final int code;

    ConfigType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
