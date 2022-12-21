package com.yuchen.etl.core.java.flink.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.core.java.utils.BucketUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connectors.hive.HiveOptions;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 18:20
 * @Package: com.yuchen.etl.core.java.flink.sink
 * @ClassName: FileBucketAssigner
 * @Description:
 **/
public class FileBucketAssigner<T> implements BucketAssigner<T, String> {
    private String bucketFormat; //date:day=$create_time(yyyy-MM-dd)/hour=$

    /*
        day=($create_time|yyyy-MM-dd)

        day=($create_time|yyyy-MM-dd)

     */
    public FileBucketAssigner(String bucketFormat) {
        this.bucketFormat = bucketFormat;
    }


    @Override
    public String getBucketId(T element, Context context) {
        if (element instanceof JSONObject) {
            return BucketUtil.process((JSONObject) element, bucketFormat);
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(element));
        return BucketUtil.process(jsonObject, bucketFormat);
    }

    @Override
    public SimpleVersionedSerializer<String> getSerializer() {
        return null;
    }
}
