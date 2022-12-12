package com.yuchen.data.api.pojo;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;


/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 9:54
 * @Package: com.yuchen.data.api.pojo
 * @ClassName: ServiceRequest
 * @Description: 服务通用查询对象
 **/
public class ServiceRequest implements Serializable {

    private static final long serialVersionUID = -3514720322688578728L;
    private String table;
    private String schema;
    private JSONObject request;

    public ServiceRequest() {
    }

    public ServiceRequest(String table, String schema, JSONObject request) {
        this.table = table;
        this.schema = schema;
        this.request = request;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public JSONObject getRequest() {
        return request;
    }

    public void setRequest(JSONObject request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "table='" + table + '\'' +
                ", schema='" + schema + '\'' +
                ", request=" + request +
                '}';
    }
}
