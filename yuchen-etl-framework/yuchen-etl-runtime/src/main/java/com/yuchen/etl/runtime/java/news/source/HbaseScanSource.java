package com.yuchen.etl.runtime.java.news.source;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.HbaseHelper;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.hbase.HbaseDao;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/13 11:21
 * @Package: com.yuchen.etl.runtime.java.news.source
 * @ClassName: HbaseScanSource
 * @Description: Hbase按时间扫描读取数据
 **/
public class HbaseScanSource extends RichSourceFunction<JSONObject> {
    private static final Logger logger = LoggerFactory.getLogger(HbaseScanSource.class);
    private TaskConfig taskConfig;
    private long startRow;
    private long endRow;

    private long scanInterval;

    private String scanTable;

    private Map hbaseConfig;

    private transient HbaseDao hbaseDao;

    private List<HField> hFields = new ArrayList<>();

    private volatile long lastProcessTime = -1;
    private volatile boolean isRunning;

    public HbaseScanSource(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
        this.hbaseConfig = this.taskConfig.getMap("hbaseConfig");
        //开始时间,默认当前时间
        this.startRow = taskConfig.getLongVal("hbase.scan.start", -1);
        //结束时间,默认持续扫描
        this.endRow = taskConfig.getLongVal("hbase.scan.end", -1);
        //扫描间隔,默认五分钟300s
        this.scanInterval = taskConfig.getLongVal("hbase.scan.interval", 1000 * 60 * 5);
        //扫描的表
        this.scanTable = taskConfig.getStringVal("hbase.scan.table");
        //字段映射表
        List<String> fields = taskConfig.getListForSplit("hbase.scan.fields", ",");
        for (String field : fields) {
            String[] meta = field.split("\\|");
            String column = meta[0]; //字段列簇
            String fieldName = meta[1]; //字段名称
            String targetField = meta[2]; //目标字段
            Boolean required = Boolean.valueOf(meta[3] == null ? "false" : meta[3]); //是否必要
            HField hField = new HField(column, fieldName, targetField, required);
            hFields.add(hField);
        }

    }

    class HField implements Serializable {
        //列簇
        public String column;
        //字段名称
        public String field;
        //是否必要
        public boolean required;
        //发送到下游的字段名称
        public String targetField;

        public HField(String column, String field, String targetField, boolean required) {
            this.column = column;
            this.field = field;
            this.targetField = targetField;
            this.required = required;
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        HbaseHelper.config(hbaseConfig);
        Connection connection = HbaseHelper.getInstance().getConnection();
        Admin admin = HbaseHelper.getInstance().getAdmin();
        this.hbaseDao = new HbaseDao(connection, admin);
        this.isRunning = true;
    }

    /**
     * 1. 指定开始时间 指定结束时间 批处理程序,扫描完时间区间后结束
     * 2. 指定开始时间 不指定结束时间 流程序,持续运行以指定开始时间戳顺序扫描Hbase表
     * 3. 不指定开始时间 不指定结束时间 流程序,定期扫描Hbase表
     */
    @Override
    public void run(SourceContext<JSONObject> sourceContext) throws Exception {

        //第一种情况 直接指定区间扫描就完了
        if (startRow != -1 && endRow != -1 && isRunning) {
            try {
                if (startRow != -1 && endRow != -1) {
                    scanRange(sourceContext, startRow, endRow);
                }
            } catch (Exception e) {
                logger.error("批量扫描Hbase数据时发生错误,请检查!", e);
                throw new RuntimeException(e);
            }
        } else {
            while (isRunning) {
                long currentTime = System.currentTimeMillis();
                if (!(lastProcessTime < (currentTime - scanInterval))) {
                    //没有到执行时间,休息1秒释放线程
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }
                try {
                    //第二种情况
                    if (startRow != -1 && endRow == -1) {
                        if (lastProcessTime == -1) {
                            lastProcessTime = startRow;
                        }
                        scanRange(sourceContext, lastProcessTime, currentTime);
                        lastProcessTime = currentTime + 1;
                        continue;
                    }
                    //第三种情况
                    if (startRow == -1 && endRow == -1) {
                        if (lastProcessTime == -1) {
                            lastProcessTime = currentTime - scanInterval;
                        }
                        scanRange(sourceContext, lastProcessTime, currentTime);
                        lastProcessTime = currentTime + 1;
                        continue;
                    }
                } catch (Exception e) {
                    logger.error("扫描Hbase数据时发生错误,请检查!", e);
                }
            }
        }


    }

    private void scanRange(SourceContext<JSONObject> sourceContext, long start, long end) {
        long interval = end - start;
        if (interval > (1000 * 60 * 60)) {
            //指定开始时间,结束时间,返回分片数组
            List<long[]> arr = getRange(start, end, 1000 * 60 * 60);
            for (long[] range : arr) {
                long startTime = range[0];
                long endTime = range[1];
                handleRange(sourceContext, startTime, endTime);
            }
        } else {
            handleRange(sourceContext, start, end);
        }
    }

    private void handleRange(SourceContext<JSONObject> sourceContext, long startTime, long endTime) {
        Scan scan = new Scan();
        // set time range
        try {
            scan.setTimeRange(startTime, endTime);
            ResultScanner resultByScan = hbaseDao.getResultByScan(scanTable, scan);
            //获取rowKeyList
            List<Get> gets = new ArrayList<>();
            resultByScan.forEach(result -> {
                String rowKey = new String(result.getRow());
                Get get = new Get(Bytes.toBytes(rowKey));
                for (HField hField : hFields) {
                    get.addColumn(Bytes.toBytes(hField.column), Bytes.toBytes(hField.field));
                }
                gets.add(get);
                System.out.println("rowKey: " + rowKey);
            });
            resultByScan.close();
            Result[] results = hbaseDao.selectRows(scanTable, gets);
            a: for (Result result : results) {
                JSONObject json = HbaseHelper.resultToJson(result);
                //校验字段过滤/重命名
                JSONObject data = new JSONObject();
                b: for (HField hField : hFields) {
                    Object o = json.get(hField.field);
                    if (o == null && hField.required) {
                        //如果字段是必选,但数据不存在,直接过滤掉,否则继续
                        continue a;
                    }
                    //字段改名
                    data.put(hField.targetField, o);
                }
                //从Hbase中取回后直接发送到下游
                sourceContext.collect(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<long[]> getRange(long startRow, long endRow, long interval) {
        List<long[]> objects = new ArrayList<>();
        long count = Math.abs((endRow - startRow) / interval);
        long index = -1;
        while (++index <= count) {
            long[] arr = new long[2];
            arr[0] = startRow + (index * interval);
            arr[1] = arr[0] + interval;
            objects.add(arr);
        }
        return objects;
    }


    public static void main(String[] args) {
        List<long[]> range = getRange(1678776284459L, 1678776287459L, 1000);
        for (long[] longs : range) {
            System.out.println(String.format("start: %s , end: %s", longs[0], longs[1]));
        }
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }


}
