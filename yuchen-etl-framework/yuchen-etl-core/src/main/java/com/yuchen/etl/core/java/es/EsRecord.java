package com.yuchen.etl.core.java.es;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.index.seqno.SequenceNumbers;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/21 9:14
 * @Package: com.yuchen.etl.core.java.es
 * @ClassName: EsRecord
 * @Description:
 **/
public class EsRecord {
    private String indexName;
    private String indexType;
    private JSONObject data;
    private String id;

    //乐观锁并发控制用字段
    private long sedNo = SequenceNumbers.UNASSIGNED_SEQ_NO;
    //乐观锁并发控制用字段
    private long primaryTerm = SequenceNumbers.UNASSIGNED_PRIMARY_TERM;

    public long getSedNo() {
        return sedNo;
    }

    public void setSedNo(long sedNo) {
        this.sedNo = sedNo;
    }

    public long getPrimaryTerm() {
        return primaryTerm;
    }

    public void setPrimaryTerm(long primaryTerm) {
        this.primaryTerm = primaryTerm;
    }

    @Override
    public String toString() {
        return "EsRecord{" +
                "indexName='" + indexName + '\'' +
                ", indexType='" + indexType + '\'' +
                ", data=" + data +
                ", id='" + id + '\'' +
                '}';
    }

    public EsRecord() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }


    public static final class Builder {
        private EsRecord esRecord;

        private Builder() {
            esRecord = new EsRecord();
        }

        public static Builder anEsRecord() {
            return new Builder();
        }

        public Builder indexName(String indexName) {
            esRecord.setIndexName(indexName);
            return this;
        }

        public Builder indexType(String indexType) {
            esRecord.setIndexType(indexType);
            return this;
        }

        public Builder data(JSONObject data) {
            esRecord.setData(data);
            return this;
        }

        public Builder id(String id) {
            esRecord.setId(id);
            return this;
        }

        public Builder sedNo(long sedNo) {
            esRecord.setSedNo(sedNo);
            return this;
        }

        public Builder primaryTerm(long primaryTerm) {
            esRecord.setPrimaryTerm(primaryTerm);
            return this;
        }

        public EsRecord build() {
            return esRecord;
        }
    }
}
