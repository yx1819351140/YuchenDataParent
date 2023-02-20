package com.yuchen.etl.runtime.java.news.common;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 14:00
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: NewsSource
 * @Description: 数据来源
 **/
public enum NewsSource {
    GDELT("yuchen_news_gdelt"),
    COLLECT("yuchen_news_collect"),
    HS("yuchen_news_hs"),
    OTHER("yuchen_news_other");

    private final String topic;

    NewsSource(String topic) {
        this.topic = topic;
    }
}
