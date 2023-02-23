package com.yuchen.etl.runtime.java.news.process;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 11:26
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: NewsProcessor
 * @Description:
 **/
public interface NewsProcessor extends Serializable {
    void process(JSONObject value) throws Exception;
}
