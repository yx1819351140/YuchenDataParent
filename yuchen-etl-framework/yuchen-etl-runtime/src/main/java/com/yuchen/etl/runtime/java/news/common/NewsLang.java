package com.yuchen.etl.runtime.java.news.common;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 14:02
 * @Package: com.yuchen.etl.runtime.java.news.common
 * @ClassName: NewsLang
 * @Description: 新闻语种
 **/
public enum NewsLang {
    ZH_CN("中文", 1),
    ZH_TW("繁体中文", 2),
    EN_US("英语", 3),
    FR_FR("法语", 4),
    DE_DE("德语", 5),
    JA_JP("日语", 6);

    private final String lang;
    private final int code;

    NewsLang(String lang, int code) {
        this.lang = lang;
        this.code = code;
    }
}
