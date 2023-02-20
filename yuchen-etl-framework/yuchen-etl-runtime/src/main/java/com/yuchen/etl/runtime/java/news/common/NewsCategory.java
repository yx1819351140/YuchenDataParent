package com.yuchen.etl.runtime.java.news.common;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 14:01
 * @Package: com.yuchen.etl.runtime.java.news.common
 * @ClassName: NewsCategory
 * @Description: 新闻类别
 **/
public enum NewsCategory {
    MILITARY("军事", 1),
    POLITICS("政治", 2),
    ECONOMY("经济", 3);

    private final String category;
    private final int code;

    NewsCategory(String category, int code) {
        this.category = category;
        this.code = code;
    }
}
