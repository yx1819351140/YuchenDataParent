package com.yuchen.etl.core.java.hive;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 16:28
 * @Package: com.yuchen.etl.core.java.hive
 * @ClassName: SerdeInfo
 * @Description: SerdeInfo
 **/
public enum SerdeInfo {
    PARQUET("org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe"),
    ORC("org.apache.hadoop.hive.ql.io.orc.OrcSerde"),
    TEXT("org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe"),
    JSON("org.apache.hadoop.hive.serde2.JsonSerDe"),
    CSV("org.apache.hadoop.hive.serde2.OpenCSVSerde"),
    AVRO("org.apache.hadoop.hive.serde2.avro.AvroSerDe");

    private String serde;

    SerdeInfo(String serde) {
        this.serde = serde;
    }

    public static SerdeInfo getByClass(String className) {
        for (SerdeInfo value : values()) {
            if (value.serde.equalsIgnoreCase(className)) {
                return value;
            }
        }
        return TEXT;
    }
}
