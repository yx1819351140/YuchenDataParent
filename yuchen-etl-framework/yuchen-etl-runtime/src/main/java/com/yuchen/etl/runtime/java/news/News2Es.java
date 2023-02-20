package com.yuchen.etl.runtime.java.news;

import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 9:56
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: News2Es
 * @Description: 消费新闻写入到ES,作业配置文件为flink-news2es.json
 **/
public class News2Es {
    public static void main(String[] args) throws IOException {
        FlinkJobConfig config = ConfigFactory.load(args[0], FlinkJobConfig.class);
        TaskConfig taskConfig = config.getTaskConfig();
        String bootstrapServers = taskConfig.getStringVal("bootstrap.servers");
        String groupId = taskConfig.getStringVal("group.id");
        String topics = taskConfig.getStringVal("topics");

    }
}
