package com.yuchen.etl.core.java.resolve;

import com.alibaba.fastjson.JSONObject;
import com.sun.istack.NotNull;
import com.yuchen.etl.core.java.common.SpeedLimiter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 13:21
 * @description: 异常数据收集器的抽象类
 */
public abstract class AbstractErrorInfoCollector<T extends ErrorInfoCollectorConfig> implements ErrorInfoCollector {

    private static final Logger _LOGGER = LoggerFactory.getLogger(AbstractErrorInfoCollector.class);
    private static final int ERROR_MSG_QUEUE_MAX = 1000;
    //如果是异步采样时,这个队列会被构造
    private LinkedBlockingQueue<LogInfo> errorMsgQueue;
    private ErrorInfoHandlerThread handler;
    private SpeedLimiter limiter;
    private ErrorInfoCollectorConfig collectorConfig;
    private volatile boolean runing;
    private long maxSamplingRecord;
    private long samplingInterval;


    public AbstractErrorInfoCollector() {
    }


    @Override
    public void open(ErrorInfoCollectorConfig config) {
        this.collectorConfig = config;
        this.errorMsgQueue = new LinkedBlockingQueue<LogInfo>(ERROR_MSG_QUEUE_MAX);
        this.limiter = new SpeedLimiter(maxSamplingRecord, samplingInterval, TimeUnit.SECONDS);
        this.handler = new ErrorInfoHandlerThread(this);
        this.init((T) config);
        this.runing = true;
        //启动异步处理线程
        handler.setDaemon(true);
        handler.start();
        Runtime.getRuntime().addShutdownHook(new ErrorInfoCollectorShutdownHook(this));
    }

    /**
     * 初始化
     *
     * @param config
     */
    public abstract void init(T config);


    /**
     * 处理未解析日志
     *
     * @param info
     */
    public abstract void handler(LogInfo info);

    /**
     * 关闭
     */
    public abstract void stop();


    @Override
    public void close() {
        runing = false;
        try {
            ArrayList<LogInfo> infos = new ArrayList<>();
            errorMsgQueue.drainTo(infos);
            for (LogInfo info : infos) {
                handler(info);
            }
            this.stop();
        } finally {
            runing = false;
            if (handler.isAlive()) {
                handler.interrupt();
            }
        }
    }

    @Override
    public void collect(LogType type, LogLevel level, LogSource source, String model, String content, Throwable error) {
        LogInfo info;
        if (content != null) {
            long logTimestamp = System.currentTimeMillis();
            info = new LogInfo(type, level, source, model, content, error, logTimestamp);
        } else {
            return;
        }
        this.collect(info);

//        String kafkaTopic = this.collectorConfig.getStringVal("kafkaTopic");
//        KafkaProducer<String, String> producer = new KafkaProducer<>(this.collectorConfig);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("type",info.getType().getCode());
//        jsonObject.put("level",info.getLevel().getCode());
//        jsonObject.put("source",info.getSource().getCode());
//        jsonObject.put("model",info.getModel());
//        jsonObject.put("content",info.getContent());
//        jsonObject.put("logTimestamp",info.getLogTimestamp());
//        // 传入报错信息
//        Throwable errorInfo = info.getError();
//        if(errorInfo == null){
//            jsonObject.put("error","");
//        }else{
//            jsonObject.put("error",errorInfo.getMessage());
//        }
//
//        try {
//            producer.send(new ProducerRecord<>(kafkaTopic,jsonObject.toJSONString()));
//            System.out.println("发送日志成功:" + jsonObject);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public void syncCollect(LogType type, LogLevel level, LogSource source, String model, String content, Throwable error) {
        LogInfo info;
        if (content != null) {
            long logTimestamp = System.currentTimeMillis();
            info = new LogInfo(type, level, source, model, content, error, logTimestamp);
        } else {
            return;
        }
        String kafkaTopic = this.collectorConfig.getStringVal("kafkaTopic");
        KafkaProducer<String, String> producer = new KafkaProducer<>(this.collectorConfig);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",info.getType().getCode());
        jsonObject.put("level",info.getLevel().getCode());
        jsonObject.put("source",info.getSource().getCode());
        jsonObject.put("model",info.getModel());
        jsonObject.put("content",info.getContent());
        jsonObject.put("logTimestamp",info.getLogTimestamp());
        // 传入报错信息
        Throwable errorInfo = info.getError();
        if(errorInfo == null){
            jsonObject.put("error","");
        }else{
            jsonObject.put("error",errorInfo.getMessage());
        }

        try {
            producer.send(new ProducerRecord<>(kafkaTopic,jsonObject.toJSONString()));
            System.out.println("发送日志成功:" + jsonObject);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void collect(LogInfo logInfo) {
        try {
            boolean offer = errorMsgQueue.offer(logInfo);
            if (!offer) {
                _LOGGER.debug("The current error message queue is full, please check.");
            }
        } catch (Exception e) {
            _LOGGER.error("Exception occurred in processing error info", e);
        }
    }

    class ErrorInfoHandlerThread extends Thread {

        private final AbstractErrorInfoCollector<ErrorInfoCollectorConfig> errorCollector;

        public ErrorInfoHandlerThread(AbstractErrorInfoCollector errorInfoCollector) {
            this.errorCollector = errorInfoCollector;
        }

        @Override
        public void run() {
            while (runing) {
                try {
                    LogInfo logInfo = errorCollector.errorMsgQueue.poll();
                    if (logInfo != null) {
                        errorCollector.limiter.limit();
                        errorCollector.handler(logInfo);
                    }
                } catch (Exception e) {
                    _LOGGER.error("Exception occurred in processing error info", e);
                }
            }
        }
    }


}
