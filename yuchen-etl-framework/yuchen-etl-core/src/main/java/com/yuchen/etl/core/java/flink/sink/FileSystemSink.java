package com.yuchen.etl.core.java.flink.sink;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.parquet.avro.AvroParquetWriters;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.RollingPolicy;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.CheckpointRollingPolicy;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;
import org.apache.flink.table.data.RowData;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;


/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 14:56
 * @Package: com.yuchen.etl.core.java.flink.sink
 * @ClassName: FileSystemSink
 * @Description:
 **/

public class FileSystemSink<T> implements FlinkSink<T> {
    protected Path path;
    protected FileFormat format = FileFormat.TEXT;
    protected int rolloverInterval;
    protected int inactivityInterval;
    protected String maxPartSize;
    protected String partSuffix = ".dat";
    protected String partPrefix = "";

    protected String bucketFormat = "day=yyyy-MM-dd/hour=${yyyy}/pt=mm";

    protected BucketAssigner<T, String> bucketAssigner;
    protected BulkWriter.Factory<T> bulkWriterFactory;
    protected RollingPolicy<T, String> rollingPolicy;
    private OutputFileConfig outputFileConfig;

    protected FileSink<T> fileSink;

    @Override
    public FlinkSink<T> init() {
        //初始化
        initOutputFileConfig();
        //初始化分桶
        initBucketAssigner();
        //初始化滚动策略
        initRollingPolicy();
        if (format == FileFormat.TEXT) {
            fileSink = createFileRowFormatSink();
        } else {
//            initBulkWriterFactory();
//            fileSink = createBulkFormatSink();
            throw new RuntimeException("non text output is not yet supported");
        }
        return this;
    }

    private void initBulkWriterFactory() {
        switch (format) {
            case PARQUET:
            case ORC:
            case AVRO:
                throw new RuntimeException("non text output is not yet supported");
        }
    }

    public Class<?> getActualTypeArgument(Class<?> clazz) {
        // 获取 Main 的超类 SuperClass 的签名(携带泛型). 这里为: xxx.xxx.xxx.SuperClass<xxx.xxx.xxx.User>
        Type genericSuperclass = clazz.getGenericInterfaces()[0];
        // 强转成 参数化类型 实体.
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        System.out.println(parameterizedType);

        // 获取超类的泛型类型数组. 即SuperClass<User>的<>中的内容, 因为泛型可以有多个, 所以用数组表示
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        String typeName = actualTypeArguments[0].getTypeName();
        Type actualTypeArgument = actualTypeArguments[0];
        System.out.println(typeName);
        Type genericType = actualTypeArguments[0];
        Class<?> t = (Class<?>) genericType;
        return t;
    }

    private void initRollingPolicy() {
        if (format == FileFormat.TEXT) {
            rollingPolicy = DefaultRollingPolicy.<T, String>builder()
                    .withRolloverInterval(Duration.ofMinutes(rolloverInterval))
                    .withInactivityInterval(Duration.ofMinutes(inactivityInterval))
                    .withMaxPartSize(MemorySize.parse(maxPartSize))
                    .build();
        } else {
            //其它数据格式只支持checkpoint
            OnCheckpointRollingPolicy<T, String> policy = OnCheckpointRollingPolicy.<T, String>build();
            rollingPolicy = policy;
        }

    }

    private void initBucketAssigner() {
        this.bucketAssigner = new DateTimeBucketAssigner<>("yyyy-MM-dd");
    }

    private FileSink<T> createBulkFormatSink() {
        FileSink.DefaultBulkFormatBuilder<T> builder = FileSink.<T>forBulkFormat(path, bulkWriterFactory);
        builder.withRollingPolicy((CheckpointRollingPolicy<T, String>) rollingPolicy);
        builder.withBucketAssigner(bucketAssigner);
        builder.withOutputFileConfig(outputFileConfig);
        return builder.build();
    }

    private FileSink<T> createFileRowFormatSink() {
        FileSink.DefaultRowFormatBuilder<T> builder = FileSink.<T>forRowFormat(path, new SimpleStringEncoder<>("utf-8"));
        builder.withRollingPolicy(rollingPolicy);
        builder.withBucketAssigner(bucketAssigner);
        builder.withOutputFileConfig(outputFileConfig);
        return builder.build();
    }

    private void initOutputFileConfig() {
        OutputFileConfig.OutputFileConfigBuilder outputFileConfigBuilder = OutputFileConfig.builder();
        outputFileConfigBuilder.withPartSuffix(partSuffix);
        if (StringUtils.isNotBlank(partPrefix)) {
            outputFileConfigBuilder.withPartPrefix(partPrefix);
        }
        outputFileConfig = outputFileConfigBuilder.build();
    }

    @Override
    public Sink<T> getSink() {
        return fileSink;
    }


    public static final class FileSystemSinkBuilder<T> {
        private FileSystemSink<T> fileSystemSink;

        private FileSystemSinkBuilder() {
            fileSystemSink = new FileSystemSink();
        }

        public static <T> FileSystemSinkBuilder<T> builder() {
            return new FileSystemSinkBuilder<T>();
        }

        public FileSystemSinkBuilder<T> path(String path) {
            fileSystemSink.path = new Path(path);
            return this;
        }

        public FileSystemSinkBuilder<T> format(FileFormat format) {
            fileSystemSink.format = format;
            return this;
        }

        public FileSystemSinkBuilder<T> partSuffix(String partSuffix) {
            fileSystemSink.partSuffix = partSuffix;
            return this;
        }

        public FileSystemSinkBuilder<T> partPrefix(String partPrefix) {
            fileSystemSink.partPrefix = partPrefix;
            return this;
        }

        public FileSystemSinkBuilder<T> maxPartSize(String maxPartSize) {
            fileSystemSink.maxPartSize = maxPartSize;
            return this;
        }

        public FileSystemSinkBuilder<T> rolloverInterval(int rolloverInterval) {
            fileSystemSink.rolloverInterval = rolloverInterval;
            return this;
        }

        public FileSystemSinkBuilder<T> inactivityInterval(int inactivityInterval) {
            fileSystemSink.inactivityInterval = inactivityInterval;
            return this;
        }

        public FileSystemSink<T> init() {
            FlinkSink<T> init = fileSystemSink.init();
            return fileSystemSink;
        }
    }
}
