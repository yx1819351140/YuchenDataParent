package com.yuchen.etl.core.java.resolve;

import com.sun.istack.NotNull;
import com.yuchen.etl.core.java.common.SpeedLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            this.stop();
        } finally {
            runing = false;
            if (handler.isAlive()) {
                handler.interrupt();
            }
        }
    }


//    @Override
//    public void collect(@NotNull ErrorInfoType type, Object obj) {
//        ErrorInfo msgInfo;
//        if (obj != null) {
//            long timeMillis = System.currentTimeMillis();
//            msgInfo = new ErrorInfo(obj, type, timeMillis);
//        } else {
//            return;
//        }
//        //1000最大值
//        //30s
//        this.collect(msgInfo);
//    }

//    private void collect(ErrorInfo info) {
//        try {
//            errorMsgQueue.offer(info, 10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            if (_LOGGER.isDebugEnabled()) {
//                _LOGGER.debug("The error message queue is full, discard the message directly, message: {}", info);
//            }
//        }
//    }

    @Override
    public void collect(LogType type, LogLevel level, LogSource source, String model, String content, Throwable error) {
        LogInfo logInfo;
        if (content != null) {
            long logTimestamp = System.currentTimeMillis();
            logInfo = new LogInfo(type, level, source, model, content, error, logTimestamp);
        } else {
            return;
        }
        this.collect(logInfo);
    }

    private void collect(LogInfo logInfo) {
        try {
            errorMsgQueue.offer(logInfo, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            if (_LOGGER.isDebugEnabled()) {
                _LOGGER.info("The error message queue is full, discard the message directly, message: {}", logInfo);
            }
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
                    LogInfo logInfo = errorCollector.errorMsgQueue.poll(10, TimeUnit.SECONDS);
                    errorCollector.limiter.limit();
                    if (logInfo != null) {
                        errorCollector.handler(logInfo);
                    }
                } catch (InterruptedException e) {
                        _LOGGER.info("no error info is currently generated");
                } catch (Exception e) {
                        _LOGGER.info("Exception occurred in processing error info", e);
                }
            }
        }
    }


}
