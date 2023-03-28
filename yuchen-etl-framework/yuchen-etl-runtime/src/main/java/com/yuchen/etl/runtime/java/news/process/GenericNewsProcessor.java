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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected final Map<String, MediaInfo> mediaInfos = new HashMap<>();
    protected final Map<String, MediaInfo> mediaSectorInfos = new HashMap<>();

    public GenericNewsProcessor(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    // 过滤脏数据
    protected JSONObject getNewsData(JSONObject value) {
        return value == null ? null : value.getJSONObject("data");
    }

    protected void handleNewsTitle(JSONObject value) {
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
        String urlStr = value.getString("origin_url");
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
        //get不到 1. 媒体板块关联 2. 媒体关联
        MediaInfo mediaInfo = mediaInfos.get(domain);
        if (mediaInfo != null) {
            value.put("media", mediaInfo);
        } else {
            MediaInfo mediaInfo1 = new MediaInfo();
            mediaInfo1.setDomain(domain);
            mediaInfo1.setMediaNameZh(value.getString("origin_url"));
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
//        CsvReader reader = CsvUtil.getReader();
//        String filePath = taskConfig.get("news.input.media.dictionary.path").toString();
//        CsvData data = reader.read(FileUtil.file(filePath));
//        List<CsvRow> rows = data.getRows();
//        //遍历行
//        for (CsvRow csvRow : rows) {
//            // 从csv中获取相关的字段
//            List<String> rawList = csvRow.getRawList();
//            String domain = rawList.get(0);
//            String media_name_zh = rawList.get(1);
//            String country_id = rawList.get(2);
//            String country_code = rawList.get(3);
//            String country_name_zh = rawList.get(4);
//            // 构建媒体对象
//            MediaInfo mediaInfo = new MediaInfo();
//            mediaInfo.setDomain(domain);
//            mediaInfo.setMediaNameZh(media_name_zh);
//            mediaInfo.setCountryId(country_id);
//            mediaInfo.setCountryCode(country_code);
//            mediaInfo.setCountryNameZh(country_name_zh);
//            // 构建内存媒体字典
//            mediaInfos.put(mediaInfo.getDomain(), mediaInfo);
//            mediaSectorInfos.put(mediaInfo.getMediaSectorUrl(), mediaInfo);
//        }

        try {
            // Mysql参数，需要提取到配置文件
            String url = "jdbc:mysql://192.168.12.222:3306/data_service?serverTimezone=UTC";
            String user = "root";
            String password = "123456";
            //指定连接类型
            Class.forName("com.mysql.cj.jdbc.Driver");
            //获取连接
            Connection connection = DriverManager.getConnection(url, user, password);
            // 执行sql
            PreparedStatement preparedStatement = connection.prepareStatement("select `domain`,`mediaNameZh`,`countryId`,`countryCode`,`countryNameZh` from t_media_info");
            ResultSet resultSet = preparedStatement.executeQuery();
            // 获取数据
            while (resultSet.next()) {
                String domain = resultSet.getString("domain");
                String media_name_zh = resultSet.getString("mediaNameZh");
                String country_id = resultSet.getString("countryId");
                String country_code = resultSet.getString("countryCode");
                String country_name_zh = resultSet.getString("countryNameZh");
                String media_lang = resultSet.getString("lang");

                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setDomain(domain);
                mediaInfo.setMediaNameZh(media_name_zh);
                mediaInfo.setCountryId(country_id);
                mediaInfo.setCountryCode(country_code);
                mediaInfo.setCountryNameZh(country_name_zh);

                mediaInfos.put(mediaInfo.getDomain(), mediaInfo);
                mediaSectorInfos.put(mediaInfo.getMediaSectorUrl(), mediaInfo);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println(mediaInfos);
    }

    @Override
    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

}
