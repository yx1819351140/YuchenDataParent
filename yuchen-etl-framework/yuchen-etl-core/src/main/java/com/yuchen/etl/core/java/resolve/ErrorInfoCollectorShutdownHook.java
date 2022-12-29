package com.yuchen.etl.core.java.resolve;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/29 11:11
 * @Package: com.yuchen.etl.core.java.resolve
 * @ClassName: ErrorInfoCollectorShutdownHook
 * @Description:
 **/
public class ErrorInfoCollectorShutdownHook extends Thread {
    private ErrorInfoCollector collector;

    public <T extends ErrorInfoCollectorConfig> ErrorInfoCollectorShutdownHook(AbstractErrorInfoCollector<T> collector) {
        this.collector = collector;
    }


    @Override
    public void run() {
        if (collector != null) {
            System.out.println("ErrorInfoCollectorShutdownHook 执行!");
            collector.close();
        }
    }
}
