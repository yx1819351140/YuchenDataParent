package com.yuchen.etl.core.java.config;

import com.yuchen.common.pub.AbstractConfig;
import com.yuchen.common.utils.CommonUtil;
import com.yuchen.etl.core.java.constants.SparkConstant;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 14:16
 * @Package: com.yuchen.etl.core.java.config
 * @ClassName: SparkJobConfig
 * @Description: Spark程序运行的配置类
 **/
public class SparkJobConfig extends AbstractConfig {

    protected static final String JOB_NAME = "jobName";
    protected static final String STREAM_DURATION = "streamDuration";
    protected static final String ENABLE_HIVE_SUPPORT = "enableHiveSupport";
    protected static final String ENABLE_DEBUG = "enableDebug";
    protected static final String IS_LOCAL = "isLocal";

    protected static final String EXECUTE_SQL = "execute_sql";

    public SparkJobConfig() {

    }

    public SparkJobConfig(Map m) {
        super(m);
    }

    public SparkConfig getSparkConfig() {
        return new SparkConfig((Map) this.getVal("sparkConfig"));
    }

    public JobConfig getJobConfig() {
        return new JobConfig((Map) this.getVal("jobConfig"));
    }


    public boolean isEnableHiveSupport() {
        return this.getBooleanVal(ENABLE_HIVE_SUPPORT, false);
    }

    public boolean isLocal() {
        return this.getBooleanVal(IS_LOCAL, false);
    }

    public boolean isEnableDebugMode() {
        return this.getBooleanVal(ENABLE_DEBUG, false);
    }

    public String getJobName(String defaultName) {
        return this.getStringVal(JOB_NAME, defaultName);
    }

    public Long getStreamDuration(long defaultDuration) {
        return this.getLongVal(STREAM_DURATION, defaultDuration);
    }

    public String getExecuteSql(boolean... isEncode) {
        boolean encode = (isEncode != null && isEncode.length > 0) ? isEncode[0] : false;
        String sql = this.getStringVal(EXECUTE_SQL);
        if (encode) {
            return CommonUtil.bese64Decode(sql);
        }
        return sql;
    }
}
