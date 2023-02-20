package com.yuchen.etl.runtime.java.news;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 11:29
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: GenericNewsProcessor
 * @Description: 通用新闻处理器
 **/
public class GenericNewsProcessor implements NewsProcessor {

    protected String generateID(String value) {
        if (StringUtils.isNotBlank(value)) {
            return new String(DigestUtil.md5(value));
        }
        return null;
    }

    protected void handleWebSite(JSONObject value) throws MalformedURLException {
        String urlStr = value.getString("url");
        if (StringUtils.isNotBlank(urlStr)) {
            URL url = null;
            url = new URL(urlStr);
            String domain = url.getHost();
            if (value.get("website") == null) {
                value.put("website", domain);
            }
        }
    }

    @Override
    public void process(JSONObject value) throws Exception {

    }
}
