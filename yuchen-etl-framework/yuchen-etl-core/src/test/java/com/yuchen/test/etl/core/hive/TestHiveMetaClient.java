package com.yuchen.test.etl.core.hive;

import com.yuchen.etl.core.java.hive.HiveMetaClient;
import com.yuchen.etl.core.java.hive.TableInfo;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 15:45
 * @Package: com.yuchen.test.etl.core.hive
 * @ClassName: TestHiveMetaClient
 * @Description:
 **/
public class TestHiveMetaClient {
    public static void main(String[] args) {
        HiveMetaClient client = HiveMetaClient.getClient();
//        org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe
//        org.apache.hadoop.hive.ql.io.orc.OrcSerde
//        org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe
        TableInfo tableInfo = client.getTableInfo("yuchen_tmp", "parquet_test");
        TableInfo tableInfo1 = client.getTableInfo("yuchen_tmp", "test_table");
        TableInfo tableInfo2 = client.getTableInfo("yuchen_tmp", "my_test_20221215");
        System.out.println(tableInfo);
        System.out.println(tableInfo1);
        System.out.println(tableInfo2);
    }
}
