package com.yuchen.etl.core.java.flink.sink;


import com.yuchen.etl.core.java.hive.HiveMetaClient;
import com.yuchen.etl.core.java.hive.SerdeInfo;
import com.yuchen.etl.core.java.hive.TableInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.table.data.RowData;
import org.apache.flink.types.Row;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 14:39
 * @Package: com.yuchen.etl.core.java.flink.sink
 * @ClassName: HiveTableSink
 * @Description: 写Hive表Sink
 **/

public class HiveTableSink<T> extends FileSystemSink<T> {
    private String schema;
    private String table;

    private HiveMetaClient metaClient = HiveMetaClient.getClient();

    private FileFormat getTableFormat(TableInfo tableInfo) {
        SerdeInfo serdeInfo = tableInfo.getSerdeInfo();
        switch (serdeInfo) {
            case TEXT:
                return FileFormat.TEXT;
            case ORC:
                return FileFormat.ORC;
            case PARQUET:
                return FileFormat.PARQUET;
            case AVRO:
                return FileFormat.AVRO;
            default:
                throw new IllegalArgumentException(String.format("unsupported table type: %s", serdeInfo));
        }
    }

    @Override
    public FlinkSink<T> init() {
        // 校验表
        System.out.println("hive table sink init");
        return this;
    }

    public static final class HiveTableSinkBuilder<T> {
        private HiveTableSink<T> hiveTableSink;

        private HiveTableSinkBuilder() {
            hiveTableSink = new HiveTableSink();
        }

        public static <T> HiveTableSinkBuilder<T> builder() {
            return new HiveTableSinkBuilder();
        }

        public HiveTableSinkBuilder<T> schema(String schema) {
            hiveTableSink.schema = schema;
            return this;
        }

        public HiveTableSinkBuilder<T> table(String table) {
            hiveTableSink.table = table;
            return this;
        }

        public FlinkSink<T> init() {
            FlinkSink<T> init = hiveTableSink.init();
            return init;
        }
    }
}
