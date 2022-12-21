package com.yuchen.etl.core.java.hive;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 15:13
 * @Package: com.yuchen.etl.core.java.hive
 * @ClassName: HiveMetaClient
 * @Description: Hive元数据客户端
 **/
public class HiveMetaClient {

    private static final Logger logger = LoggerFactory.getLogger(HiveMetaClient.class);
    private static volatile HiveMetaClient metaStoreClient;
    public static final String HIVE_META_URL = "thrift://datanode01:9083";
    private HiveMetaStoreClient client;

    private HiveMetaClient() {
    }

    public static HiveMetaClient getClient() {
        if (metaStoreClient == null) {
            synchronized (HiveMetaClient.class) {
                if (metaStoreClient == null) {
                    metaStoreClient = new HiveMetaClient();
                    metaStoreClient.init();
                }
            }
        }
        return metaStoreClient;
    }

    private void init() {
        HiveConf hiveConf = new HiveConf();
        String property = System.getProperty("hive.metastore.uris");
        hiveConf.set("hive.metastore.uris", StringUtils.isNotBlank(property) ? property : HIVE_META_URL);
        try {
            this.client = new HiveMetaStoreClient(hiveConf);
        } catch (Exception e) {
            logger.error("Error initializing HiveMetaStoreClient, please check!");
            throw new RuntimeException(e);
        }
    }

    public TableInfo getTableInfo(String schema, String tableName) {
        TableInfo tableInfo = new TableInfo();
        try {
            Table table = this.client.getTable(schema, tableName);
            StorageDescriptor sd = table.getSd();
            List<FieldSchema> cols = sd.getCols();
            tableInfo.setTableName(tableName);
            tableInfo.setSchema(schema);
            tableInfo.setOwner(table.getOwner());
            tableInfo.setCreateTime(table.getCreateTime());
            tableInfo.setPartition(table.getPartitionKeys().size() > 0);
            List<String> keys = new ArrayList<>();
            table.getPartitionKeys().forEach(p -> {
                keys.add(p.getName());
            });
            tableInfo.setPartitionKeys(JSONObject.toJSONString(keys));
            tableInfo.setTableLocation(sd.getLocation());
            tableInfo.setTableType(TableType.valueOf(table.getTableType()));
            tableInfo.setFieldDelim(sd.getSerdeInfo().getParameters().get("field.delim"));
            tableInfo.setInputFormat(sd.getInputFormat());
            tableInfo.setOutputFormat(sd.getOutputFormat());
            tableInfo.setSerializationFormat(sd.getSerdeInfo().getParameters().get("serialization.format"));
            tableInfo.setCompressed(sd.isCompressed());
            tableInfo.setSerdeInfo(SerdeInfo.getByClass(sd.getSerdeInfo().getSerializationLib()));
            Map<String, JSONObject> fields = new HashMap<String, JSONObject>();
            for (FieldSchema col : cols) {
                String name = col.getName();
                String comment = col.getComment();
                String type = col.getType();
                JSONObject field = new JSONObject();
                field.put("name", name);
                field.put("comment", comment);
                field.put("type", type);
                fields.put(name, field);
            }
            tableInfo.setFields(fields);
        } catch (TException e) {
            logger.error("Failed to obtain Hive Table information", e);
        }
        return tableInfo;
    }
}
