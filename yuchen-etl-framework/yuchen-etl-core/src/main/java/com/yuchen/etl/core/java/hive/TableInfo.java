package com.yuchen.etl.core.java.hive;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.serde2.SerDe;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 15:31
 * @Package: com.yuchen.etl.core.java.hive
 * @ClassName: TableInfo
 * @Description:
 **/
@Data
public class TableInfo {
    private String schema;
    private String tableName;
    private String tableLocation;
    private TableType tableType;
    private boolean isPartition;
    private String partitionKeys;
    private String inputFormat;
    private String outputFormat;
    private SerdeInfo serdeInfo;
    private String serializationFormat;
    private long createTime;
    private String fieldDelim;
    private boolean isCompressed;
    private String owner;
    private Map<String, JSONObject> fields;


}
