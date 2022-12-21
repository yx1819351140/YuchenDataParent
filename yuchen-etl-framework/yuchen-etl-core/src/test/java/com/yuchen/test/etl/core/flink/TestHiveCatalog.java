package com.yuchen.test.etl.core.flink;

import com.yuchen.etl.core.java.hive.HiveMetaClient;
import com.yuchen.test.etl.core.hive.TestHiveMetaClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connectors.hive.write.HiveBulkWriterFactory;
import org.apache.flink.table.api.TableColumn;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.catalog.CatalogBaseTable;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.flink.table.catalog.hive.HiveCatalogConfig;
import org.apache.flink.table.catalog.hive.client.HiveShim;
import org.apache.flink.table.catalog.hive.client.HiveShimLoader;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.utils.TableSchemaUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Test;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/19 11:20
 * @Package: com.yuchen.test.etl.core.flink
 * @ClassName: TestHiveCatalog
 * @Description:
 **/
public class TestHiveCatalog {


    @Test
    public void testCatalog() throws TableNotExistException {
        HiveConf hiveConf = new HiveConf();
        String property = System.getProperty("hive.metastore.uris");

    }
}
