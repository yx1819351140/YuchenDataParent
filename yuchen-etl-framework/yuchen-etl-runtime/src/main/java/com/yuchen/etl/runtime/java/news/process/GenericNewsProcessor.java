package com.yuchen.etl.runtime.java.news.process;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.HttpClientResult;
import com.yuchen.common.pub.HttpClientUtil;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.runtime.java.news.operator.MediaInfo;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 11:29
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: GenericNewsProcessor
 * @Description: 通用新闻处理器
 **/
public class GenericNewsProcessor implements NewsProcessor {
    private TaskConfig taskConfig;
    //key是媒体的domain value是媒体的信息
    private final Map<String, MediaInfo> mediaInfos = new HashMap<>();


    public GenericNewsProcessor(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

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


    protected void handleMediaInfo(JSONObject value) {
        String website = value.getString("website");
        MediaInfo mediaInfo = mediaInfos.get(website);
        if (mediaInfo != null) {
            value.put("media", mediaInfo);
        }
    }

    @Override
    public void process(JSONObject value) throws Exception {
        //默认的GenericNews处理器,不做任何特殊处理.
    }

    @Override
    public void init() {
        //加载媒体表
//        String stringVal = taskConfig.getStringVal("news.etl.api.media", "http://127.0.0.1:8080/dict");
//        HttpClientResult httpClientResult = HttpClientUtil.doGet(stringVal, false);
//        JSONObject result = httpClientResult.getJSON();
//        JSONArray data = result.getJSONArray("data");
//        for (Object obj : data) {
//            if (obj != null && obj instanceof JSONObject) {
//                JSONObject json = (JSONObject) obj;
//                MediaInfo mediaInfo = new MediaInfo();
//                BeanUtil.copyProperties(json, mediaInfo);
//                mediaInfos.put(mediaInfo.getDomain(), mediaInfo);
//            }
//        }
        //TODO 这里需要临时加载下,等接口可以正常使用后,废弃
    }

    @Override
    public TaskConfig getTaskConfig() {
        return taskConfig;
    }


}
