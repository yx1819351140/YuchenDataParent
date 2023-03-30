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
        String domain = value.getString("website");  // 域名
        // get不到 1. 媒体板块关联 2. 媒体关联 xzn
        // 数据流关联媒体信息
        MediaInfo mediaInfo = mediaInfos.get(domain);
        // 如果关联到相关媒体, 将关联媒体信息放入report_media字段中, 同时生成信源管理字段(后期替换为稳定信源字段)
        if (mediaInfo != null) {
            JSONArray reportMedia = new JSONArray();
            // 将mediaInfo转为JSONArray嵌套的JSONObject, 以便于写入到report_media字段中
            JSONObject mediaJSONObject = (JSONObject)JSONObject.toJSON(mediaInfo);
            reportMedia.add(mediaJSONObject);
            value.put("media", mediaInfo.getDomain());
            value.put("media_sector", mediaInfo.getMediaSector());
            String countryName = mediaInfo.getCountryNameZh() == null ? mediaInfo.getCountryName() : mediaInfo.getCountryNameZh();
            value.put("media_country", countryName);
            value.put("media_country_code", mediaInfo.getCountryCode());
            value.put("report_media", reportMedia);
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
            // 指定连接类型
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 获取连接
            Connection connection = DriverManager.getConnection(url, user, password);
            // 执行sql
            PreparedStatement preparedStatement = connection.prepareStatement("select `domain`,`mediaName`,`mediaNameZh`,`countryId`,`countryCode`,`countryName`,`countryNameZh` ,`mediaLang`,`mediaSector` from t_media_info");
            ResultSet resultSet = preparedStatement.executeQuery();
            // 获取数据
            while (resultSet.next()) {
                String domain = resultSet.getString("domain");
                String mediaName = resultSet.getString("mediaName");
                String mediaNameZh = resultSet.getString("mediaNameZh");
                String countryId = resultSet.getString("countryId");
                String countryCode = resultSet.getString("countryCode");
                String countryName = resultSet.getString("countryName");
                String countryNameZh = resultSet.getString("countryNameZh");
                String mediaLang = resultSet.getString("mediaLang");
                String mediaSector = resultSet.getString("mediaSector");

                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setDomain(domain);
                mediaInfo.setMediaName(mediaName);
                mediaInfo.setMediaNameZh(mediaNameZh);
                mediaInfo.setCountryId(countryId);
                mediaInfo.setCountryCode(countryCode);
                mediaInfo.setCountryName(countryName);
                mediaInfo.setCountryNameZh(countryNameZh);
                mediaInfo.setMediaLang(mediaLang);
                mediaInfo.setMediaSector(mediaSector);

                mediaInfos.put(mediaInfo.getDomain(), mediaInfo); // 媒体信息的键为域名, 值为媒体对象的各种信息
                //mediaSectorInfos.put(mediaInfo.getMediaSectorUrl(), mediaInfo);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println(mediaInfos);
    }

    @Override
    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

}
