package com.yuchen.etl.core.java.config;

import java.io.Serializable;



/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 9:29
 * @Package: com.yuchen.etl.core.java.config
 * @ClassName: JobConfig
 * @Description: 作业配置类
 **/
public abstract class JobConfig implements Serializable {

    private String jobName;
    private TaskConfig taskConfig;

    public TaskConfig getTaskConfig() {
        return this.taskConfig;
    }

    public String getJobName() {
        return jobName;
    }

    public void printInfo() {
        System.out.println("====================Job Engine Config ====================");
        print();
        System.out.println();
        System.out.println("==================== Job Task Config ====================");
        System.out.println(String.format("JobName: %s", jobName));
        if (taskConfig != null) {
            for (String key : taskConfig.keySet()) {
                System.out.println(String.format("%s: %s", key, taskConfig.get(key)));
            }
        }
    }

    protected abstract void print();
}
