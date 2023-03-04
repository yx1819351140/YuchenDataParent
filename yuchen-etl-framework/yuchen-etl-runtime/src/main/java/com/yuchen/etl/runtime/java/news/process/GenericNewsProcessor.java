package com.yuchen.etl.runtime.java.news.process;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.HttpClientResult;
import com.yuchen.common.pub.HttpClientUtil;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.runtime.java.news.operator.MediaInfo;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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

    // 过滤脏数据
    protected void filterFields(JSONObject value) throws MalformedURLException {
        String title = value.getString("title");
        String context = value.getString("context");
        // 标题正文长度小于5的数据不要
        if (title == null || context == null || title.length()<5 || context.length()<5) {
            value.put("validNews",false);
        }else{
            value.put("validNews",true);
        }
    }

    protected JSONObject getNewsData(JSONObject value) throws MalformedURLException {
        return value == null ? null : value.getJSONObject("data");
    }

    protected void handleNewsTitle(JSONObject value) throws MalformedURLException {
        String title = value.getString("title");
        if (title != null) {
            String id = generateID(title);
            value.put("id",id);
        }
    }

    protected String generateID(String value) {
        if (StringUtils.isNotBlank(value)) {
            return DigestUtil.md5Hex(value.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    protected void handleWebSite(JSONObject value) throws MalformedURLException {
        JSONObject data = getNewsData(value);
        String urlStr = data.getString("url");
        if (StringUtils.isNotBlank(urlStr)) {
            URL url = null;
            url = new URL(urlStr);
            String domain = url.getHost();
            value.putIfAbsent("website", domain);
        }
    }

    // gdelt的新闻分类,多分类且极有可能不存在分类的字段
    protected void handleCatalog(JSONObject value) throws MalformedURLException {
        if(value.containsKey("yc_news_catalogue")){
            JSONArray ycNewsCatalogue = JSON.parseArray(value.getString("yc_news_catalogue"));
            value.put("category", ycNewsCatalogue);
        }else{
            value.put("category", null);
        }
    }

    protected void handleMediaInfo(JSONObject value) {
        String domain = value.getString("website");
        MediaInfo mediaInfo = mediaInfos.get(domain);
        value.put("media", mediaInfo);
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
        CsvReader reader = CsvUtil.getReader();
        String filePath = taskConfig.get("news.input.media.dictionary.path").toString();
        CsvData data = reader.read(FileUtil.file(filePath));
        List<CsvRow> rows = data.getRows();
        //遍历行
        for (CsvRow csvRow : rows) {
            // 从csv中获取相关的字段
            List<String> rawList = csvRow.getRawList();
            String domain = rawList.get(0);
            String media_name_zh = rawList.get(1);
            String country_id = rawList.get(2);
            String country_code = rawList.get(3);
            String country_name_zh = rawList.get(4);
            // 构建媒体对象
            MediaInfo mediaInfo = new MediaInfo();
            mediaInfo.setDomain(domain);
            mediaInfo.setMediaNameZh(media_name_zh);
            mediaInfo.setCountryId(country_id);
            mediaInfo.setCountryCode(country_code);
            mediaInfo.setCountryNameZh(country_name_zh);
            // 构建内存媒体字典
            mediaInfos.put(mediaInfo.getDomain(), mediaInfo);
        }
        System.out.println(mediaInfos);
    }

    @Override
    public TaskConfig getTaskConfig() {
        return taskConfig;
    }
}
