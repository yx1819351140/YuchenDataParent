package com.yuchen.etl.core.java.resolve;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/29 11:11
 * @Package: com.yuchen.etl.core.java.resolve
 * @ClassName: ErrorInfoCollectorShutdownHook
 * @Description:
 **/
public class ErrorInfoCollectorShutdownHook extends Thread {
    private ErrorInfoCollector collector;
    private static final Logger logger = LoggerFactory.getLogger(ErrorInfoCollectorShutdownHook.class);

    public <T extends ErrorInfoCollectorConfig> ErrorInfoCollectorShutdownHook(AbstractErrorInfoCollector<T> collector) {
        this.collector = collector;
    }


    @Override
    public void run() {
        if (collector != null) {
            logger.info("Start to execute the error information collector and close the thread.");
            try {
                collector.close();
                logger.info("Error message collector shutdown complete.");
            } catch (Exception e) {
                logger.debug("Turn off log collector errors.", e);
            }
        }
    }
}
