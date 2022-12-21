package com.yuchen.test.etl.core.flink;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.core.java.flink.sink.FileFormat;
import com.yuchen.etl.core.java.flink.sink.FileSystemSink;
import com.yuchen.etl.core.java.flink.sink.HiveTableSink;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.file.table.FileSystemTableFactory;
import org.apache.flink.connector.file.table.FileSystemTableSink;
import org.apache.flink.connectors.hive.HiveDynamicTableFactory;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSinkHelper;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.flink.table.data.RowData;
import org.apache.flink.types.Row;
import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Test;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 17:10
 * @Package: com.yuchen.test.etl.core.flink
 * @ClassName: TestFlinkHiveTableSink
 * @Description:
 **/
public class TestFlinkHiveTableSink {

    @Test
    public void testHiveTableSink() {
        Sink<RowData> tableSink = HiveTableSink.HiveTableSinkBuilder.<RowData>builder()
                .table("")
                .schema("")
                .init().getSink();

        Sink<RowData> fileSink = FileSystemSink.FileSystemSinkBuilder.<RowData>builder()
                .path("")
                .format(FileFormat.TEXT)
                .init().getSink();



    }
}
