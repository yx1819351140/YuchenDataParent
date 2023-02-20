package com.yuchen.etl.runtime.java.news;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 11:26
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: NewsProcessor
 * @Description:
 **/
public interface NewsProcessor {
    void process(JSONObject value) throws Exception;
}
