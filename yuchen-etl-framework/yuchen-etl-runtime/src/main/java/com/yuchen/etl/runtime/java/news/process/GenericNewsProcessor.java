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
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.runtime.java.news.operator.MediaInfo;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import com.yuchen.etl.core.java.config.ConfigFactory;

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
    protected final Map<String, MediaInfo> mediaInfos = new HashMap<>(); // 内存媒体表
    protected final Map<String, MediaInfo> mediaSectorInfos = new HashMap<>();

    public GenericNewsProcessor(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    // 过滤脏数据
    protected JSONObject getNewsData(JSONObject value) {
        return value == null ? null : value.getJSONObject("data");
    }

    protected void handleNewsId(JSONObject value) {
        String title = value.getString("title");
        String origin_url = value.getString("origin_url");
        if (title != null && origin_url != null) {
            String id = generateID(title + origin_url);
            value.put("id",id);
        }
    }

    protected void handleNewsTitleId(JSONObject value) {
        String title = value.getString("title");
        if (title != null) {
            String title_id = generateID(title);
            value.put("title_id",title_id);
        }
    }

    protected String generateID(String value) {
        if (StringUtils.isNotBlank(value)) {
            return DigestUtil.md5Hex(value.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    protected void handleWebSite(JSONObject value) throws MalformedURLException {
        String urlStr = value.getString("origin_url");
        if (StringUtils.isNotBlank(urlStr)) {
            URL url = null;
            url = new URL(urlStr);
            String domain = url.getHost();
            value.putIfAbsent("website", domain);
            value.putIfAbsent("website_name", domain); // 网站名称默认为website,后续可根据需求修改
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
        // 用于判断是否关联到信源媒体
        String originUrl = value.getString("origin_url");  // 域名
        String domain = value.getString("website");  // 域名
        String newDomain = domain.contains("www") ? domain.replace("www.",""):"www." + domain;  // 用于兼容性处理
        // get不到 1. 媒体板块关联 2. 媒体关联 xzn
        // 数据流的报道媒体默认值(主要是reportUrl与reportTime)
        JSONArray reportMedia = new JSONArray();
        JSONObject mediaJSONObject = new JSONObject();
        mediaJSONObject.put("reportUrl", originUrl); // 媒体报道url为新闻的origin_url
        mediaJSONObject.put("reportTime", value.getString("pub_time"));  // 媒体报道时间为新闻的pub_time

        // 先进行媒体板块关联, 如果关联到媒体板块则使用带有媒体板块信息的mediaInfo, 否则关联信源媒体信息,如果都为空则表示没有关联到信源
        MediaInfo mediaInfo = mediaSectorInfos.get(originUrl) == null ? mediaInfos.get(domain) : mediaSectorInfos.get(originUrl);
        // 兼容性处理
        mediaInfo = mediaInfo == null ? mediaInfos.get(newDomain) : mediaInfo;
        // 如果关联到相关媒体, 将关联媒体信息放入report_media字段中, 同时生成信源管理字段(后期替换为稳定信源字段)
        if (mediaInfo != null) {
            // 将关联到的mediaInfo转为JSONObject覆写默认值
            mediaJSONObject.putAll((JSONObject)JSONObject.toJSON(mediaInfo));
            value.put("media", mediaInfo.getDomain());
            value.put("media_sector", mediaInfo.getMediaSector());
            String countryName = mediaInfo.getCountryNameZh() == null ? mediaInfo.getCountryName() : mediaInfo.getCountryNameZh();
            value.put("media_country", countryName);
            value.put("media_country_code", mediaInfo.getCountryCode());
            value.put("keywords", mediaInfo.getKeywords());
        }
        // 将默认值(覆盖值)放入report_media字段中
        reportMedia.add(mediaJSONObject);
        value.put("report_media", reportMedia);
    }

    @Override
    public void process(JSONObject value) throws Exception {
        //默认的GenericNews处理器,不做任何特殊处理.
    }

    @Override
    public void init() {
        //加载媒体表
        try {
            // 加载接口的信源, 除去媒体板块关联的信息
            String mediaApi = taskConfig.getStringVal("news.etl.api.media");
            HttpClientResult result = HttpClientUtil.doPost(mediaApi, false);
            JSONObject mediaObjects = result.getJSON();
            String domain = "";
            String mediaName = "";
            String mediaNameZh = "";
            String countryId = "";
            String countryCode = "";
            String countryName = "";
            String countryNameZh = "";
            String mediaLang = "";
            String sectionName = "";
            String sectionUrl = "";
            List<String> keywords = new ArrayList<>();

            JSONArray data = mediaObjects.getJSONArray("data");
            for (Object datum : data) {
                JSONObject next = (JSONObject) datum;
                //这里组装mediaInfo
                domain = next.getString("domain");
                mediaName = next.getString("enName");
                mediaNameZh = next.getString("cnName");

                JSONObject country = next.getJSONObject("country");
                if(country != null){
                    countryId = country.getString("id");
                    countryCode = country.getString("code");
                    countryName = country.getString("countryEn");
                    countryNameZh = country.getString("countryCn");
                }

                JSONObject language = next.getJSONObject("language");
                if(country != null){
                    mediaLang = language.getString("code");
                }

                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setDomain(domain);
                mediaInfo.setMediaName(mediaName);
                mediaInfo.setMediaNameZh(mediaNameZh);
                mediaInfo.setCountryId(countryId);
                mediaInfo.setCountryCode(countryCode);
                mediaInfo.setCountryName(countryName);
                mediaInfo.setCountryNameZh(countryNameZh);
                mediaInfo.setMediaLang(mediaLang);
                // 关键词字段
                JSONArray keywordsArray = next.getJSONArray("keywords");
                if(keywordsArray != null){
                    keywords = JSONObject.parseArray(keywordsArray.toJSONString(),String.class);
                }
                mediaInfo.setKeywords(keywords);

                // 媒体的字典构建
                mediaInfos.put(mediaInfo.getDomain(), mediaInfo);

                // 板块的字典构建
                JSONArray sectionList = next.getJSONArray("sectionList");
                if(sectionList != null){
                    for (Object o : sectionList) {
                        JSONObject jsonObject = (JSONObject) o;
                        MediaInfo mediaSectorInfo = new MediaInfo();
                        mediaSectorInfo.setDomain(domain);
                        mediaSectorInfo.setMediaName(mediaName);
                        mediaSectorInfo.setMediaNameZh(mediaNameZh);
                        mediaSectorInfo.setCountryId(countryId);
                        mediaSectorInfo.setCountryCode(countryCode);
                        mediaSectorInfo.setCountryName(countryName);
                        mediaSectorInfo.setCountryNameZh(countryNameZh);
                        mediaSectorInfo.setMediaLang(mediaLang);
                        sectionName = jsonObject.getString("sectionName");
                        sectionUrl = jsonObject.getString("sectionUrl");
                        // 设置板块信息以及关键词
                        mediaSectorInfo.setMediaSector(sectionName);
                        mediaSectorInfo.setMediaSectorUrl(sectionUrl);
                        mediaSectorInfo.setKeywords(keywords);
                        // 如果板块信息不为空, 则信源字典也是需要更新的
                        mediaInfos.put(mediaSectorInfo.getDomain(), mediaSectorInfo);
                        mediaSectorInfos.put(sectionUrl, mediaSectorInfo);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("http://www.capsulecomputers.com.au/2023/03/wo-long-fallen-dynasty-gameplay/");
        String domain = url.getHost();
        System.out.println(domain);
    }

    @Override
    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

}
