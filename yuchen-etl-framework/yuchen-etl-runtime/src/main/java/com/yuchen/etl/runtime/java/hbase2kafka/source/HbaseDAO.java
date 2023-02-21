package com.yuchen.etl.runtime.java.hbase2kafka.source;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.HbaseHelper;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.TaskConfig;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
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
 * FileName: HbaseDAO
 * Author:   HQP
 * Date:     2023/2/21 19:35
 * Description: 按一定查询条件查询hbase(这一层重点封装查询条件的组装功能，hbaseHelper封装原始查询功能)
 */
public class HbaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(HbaseDAO.class);

    HbaseHelper hbaseHelper;

    Map<String, Object> hbaseConfigs;

    Map<String, Object> transformationConfigs;

    public HbaseDAO(HbaseHelper hbaseHelper,Map<String, Object> hbaseConfigs,Map<String, Object> transformationConfigs) throws IOException {
        this.hbaseHelper = hbaseHelper;
        this.hbaseConfigs = hbaseConfigs;
        this.transformationConfigs = transformationConfigs;
    }

    /**
     * get hbase data
     * <p>
     * */
    public List<JSONObject> getJSONResultLowerThreshold(Integer threshold) throws IOException{
        List<Get> gets = new ArrayList<>();
        List<JSONObject> resultList = new ArrayList<>();
        int count = 0;
        Scan customScan = new Scan();
        // set time range
        long timeRange = Long.parseLong(transformationConfigs.get("time.range.ms").toString());
        long latencyTime = Long.parseLong(transformationConfigs.get("time.latency.ms").toString());
        customScan.setTimeRange(DateUtils.getCurrenTimestamp() - timeRange - latencyTime,DateUtils.getCurrenTimestamp());

        // set none column, get rowKey
        String hbaseFields = transformationConfigs.get("must.fields").toString();
        String[] hbaseFieldsString = hbaseFields.split(",");
        int mustLength = hbaseFieldsString.length;

        // get scanner and get json result through scanner limited by threshold
        // get needed column
        String tableName = hbaseConfigs.get("source.table").toString();
        try (ResultScanner scanner = hbaseHelper.getResultByScan(tableName, customScan)) {
            for (Result result : scanner) {
                String rowKey = Bytes.toString(result.getRow());
                logger.info("processing rowKey:{}", rowKey);
                System.out.println("processing rowKey:" + rowKey);
                count++;
                if(count <= threshold){
                    // assemble get
                    Get get = new Get(Bytes.toBytes(rowKey));
                    Arrays.stream(hbaseFieldsString).forEach(hbaseFieldString ->{
                        String qualifier = hbaseFieldString.split(":")[0];
                        String column = hbaseFieldString.split(":")[1];
                        get.addColumn(Bytes.toBytes(qualifier),Bytes.toBytes(column));
                    });
                    gets.add(get);
                }else{
                    break;
                }
            }
        }

        return hbaseHelper.getMustBatchData(tableName, gets,mustLength);
    }



}
