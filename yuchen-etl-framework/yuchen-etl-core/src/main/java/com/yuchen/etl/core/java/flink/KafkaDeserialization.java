package com.yuchen.etl.core.java.flink;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

import java.io.IOException;

public class KafkaDeserialization implements KafkaRecordDeserializationSchema<JSONObject> {
    private static final Logger log = Logger.getLogger(KafkaDeserialization.class);
    private static final String ENCODING = "UTF8";
    private static final String DATA_KEY = "data";
    private static final String TOPIC_KEY = "topic";
    private static final String TIMESTAMP_KEY = "timestamp";
    private boolean includeTopic;
    private boolean includeTimestamp;

    public KafkaDeserialization(boolean includeTopic, boolean includeTimestamp) {
        this.includeTopic = includeTopic;
        this.includeTimestamp = includeTimestamp;
    }


    @Override
    public TypeInformation<JSONObject> getProducedType() {
        return TypeInformation.of(JSONObject.class);
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<JSONObject> out) throws IOException {
        if (record != null) {
            try {
                String value = new String(record.value(), ENCODING);
                JSONObject json = new JSONObject();
                json.put(DATA_KEY, value);
                if (includeTopic) json.put(TOPIC_KEY, record.topic());
                if (includeTimestamp) json.put(TIMESTAMP_KEY, record.timestamp());
                out.collect(json);
            } catch (Exception e) {
                log.error("deserialize failed : " + e.getMessage());
            }
        }
    }
}