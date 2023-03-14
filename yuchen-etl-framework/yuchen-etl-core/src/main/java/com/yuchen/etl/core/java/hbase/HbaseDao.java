package com.yuchen.etl.core.java.hbase;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/14 15:09
 * @Package: com.yuchen.etl.core.java.hbase
 * @ClassName: HbaseDao
 * @Description: Hbase操作类
 **/
public class HbaseDao {
    private Connection connection;
    private Admin admin;

    public HbaseDao(Connection connection, Admin admin) {
        this.connection = connection;
        this.admin = admin;
    }

    public HbaseDao(Connection connection) {
        this.connection = connection;
        try {
            this.admin = connection.getAdmin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultScanner getResultByScan(String tableName, Scan scan) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            return table.getScanner(scan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Result[] selectRows(String tableName, List<Get> gets) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            return table.get(gets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject resultToJson(Result result) {
        //每一行数据
        JSONObject jsonObject = new JSONObject();
        String rowKey = null;
        for (Cell cell : result.rawCells()) {
            if (rowKey == null) {
                rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
            }
            jsonObject.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()), Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
        }
        return jsonObject;
    }
}
