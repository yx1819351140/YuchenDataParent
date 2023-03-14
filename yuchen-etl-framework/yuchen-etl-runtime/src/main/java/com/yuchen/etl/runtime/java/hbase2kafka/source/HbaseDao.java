package com.yuchen.etl.runtime.java.hbase2kafka.source;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.HbaseHelper;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import com.yuchen.common.utils.DateUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C), 2023-02-20
 * FileName: HbaseDao
 * Author:   HQP
 * Date:     2023/2/21 19:35
 * Description: 按一定查询条件查询hbase(这一层重点封装查询条件的组装功能，hbaseHelper封装原始查询功能)
 */
public class HbaseDao {

    private static final Logger logger = LoggerFactory.getLogger(HbaseDao.class);

    HbaseHelper hbaseHelper;

    Map<String, Object> hbaseConfigs;

    Map<String, Object> transformationConfigs;

    /**
     * get hbase data
     * <p>
     * */
    public List<JSONObject> getJSONResultLowerThreshold(Integer threshold) throws IOException{
        // read configuration
        String tableName = hbaseConfigs.get("source.table").toString();
        String[] hbaseRequiredFields = transformationConfigs.get("news.required.fields").toString().split(",");
        String[] hbaseOptionalFields = transformationConfigs.get("news.optional.fields").toString().split(",");
        String[] splits = transformationConfigs.get("news.required.filter.lang").toString().split("=");
        String compareFamily = splits[0].split(":")[0];
        String compareQualifier = splits[0].split(":")[1];
        String compareValue = splits[0].split(":")[1];
        boolean useFieldFilter = Boolean.parseBoolean(transformationConfigs.get("news.use.filter").toString());
        long timeRange = Long.parseLong(transformationConfigs.get("time.range.ms").toString());
        long latencyTime = Long.parseLong(transformationConfigs.get("time.latency.ms").toString());

        List<Get> gets = new ArrayList<>();
        List<JSONObject> resultList = new ArrayList<>();
        int count = 0;
        Scan customScan = new Scan();
        customScan.setLimit(threshold);
        // set time range
        customScan.setTimeRange(DateUtils.getCurrenTimestamp() - timeRange - latencyTime,DateUtils.getCurrenTimestamp());

        // get scanner and get json result through scanner limited by threshold
        // get needed column
        // 分为2步：1.过滤满足一定条件的列的rowkey list 2.加上可选字段的行记录
        // 1.过滤条件+必选字段
        List<String> filteredRowKey = hbaseHelper
                .getFilteredRowKey(customScan,tableName, Arrays.asList(hbaseRequiredFields), compareFamily, compareQualifier, compareValue, true, useFieldFilter);

        // 2.必选字段+可选字段+过滤字段的记录，组装批量get
        filteredRowKey.forEach(rowKey ->{
            System.out.println("第二阶段正在处理：" + rowKey);
            Get get = new Get(Bytes.toBytes(rowKey));
            // 必选字段
            Arrays.stream(hbaseRequiredFields).forEach(hbaseFieldString ->{
                String family01 = hbaseFieldString.split(":")[0];
                String qualifier01 = hbaseFieldString.split(":")[1];
                get.addColumn(Bytes.toBytes(family01),Bytes.toBytes(qualifier01));
            });
            // 可选字段
            Arrays.stream(hbaseOptionalFields).forEach(hbaseOptionalField ->{
                String family01 = hbaseOptionalField.split(":")[0];
                String qualifier01 = hbaseOptionalField.split(":")[1];
                get.addColumn(Bytes.toBytes(family01),Bytes.toBytes(qualifier01));
            });
            // 过滤字段
            if(useFieldFilter){
                get.addColumn(Bytes.toBytes(compareFamily),Bytes.toBytes(compareQualifier));
            }
            gets.add(get);
        });
        // 批量get
        return hbaseHelper.getOptionBatchData(tableName, gets);
    }

    public HbaseDao(Map<String, Object> hbaseConfig,Map<String, Object> hbaseConfigs, Map<String, Object> transformationConfigs) throws IOException {
        HbaseHelper.config(hbaseConfig);
        this.hbaseHelper = HbaseHelper.getInstance();
        this.hbaseConfigs = hbaseConfigs;
        this.transformationConfigs = transformationConfigs;
    }



}
