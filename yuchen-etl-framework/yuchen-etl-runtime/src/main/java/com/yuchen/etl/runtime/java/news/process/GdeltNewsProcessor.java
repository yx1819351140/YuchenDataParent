package com.yuchen.etl.runtime.java.news.process;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.core.java.config.TaskConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 13:06
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: GdeltNewsProcessor
 * @Description:
 **/
public class GdeltNewsProcessor extends GenericNewsProcessor {

    public GdeltNewsProcessor(TaskConfig taskConfig) {
        super(taskConfig);
    }


    // TODO
    // 1.title_id处理
    // 2.content,url,lang
    // 3.domain
    // 4.category
    // 5.time
    // 6.建立字段间的映射关系：

    @Override
    public void  process(JSONObject value) throws Exception {
        // 获取原始数据字段
        JSONObject data = value.getJSONObject("data");

        // 生成ID:标题和url组合的md5为id
        handleNewsId(data);

        // 生成title_id:标题的md5为title_id
        handleNewsTitleId(data);

        // 过滤脏数据
        filterFields(data);

        // 获得原始domain, 或者从url中提取domain
        handleWebSite(data);

        // gdelt中的catalog处理为category
        handleCatalog(data);

        // 处理数据时间,从value中获取时间戳,写入到data中
        handleDataTime(value, data);

        // 添加媒体
        handleMediaInfo(data);

        // data数据放回value
        value.put("data", data);
    }

    private static void handleDataTime(JSONObject value, JSONObject data) throws ParseException {
        Long timestamp = value.getLong("timestamp");
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        // 获取正文数据入库时间作为发布时间，非系统入库时间，也就是说发布时间是会变化的
        String pubTime = data.getString("date_v1").replace("T", " ").substring(0, 19);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = simpleDateFormat.parse(pubTime);
        Long pubTimestamp = parse.getTime();
        data.put("create_time", time);
        data.remove("date_v1"); // 删除原始数据中的date_v1
        data.put("pub_time", pubTime);
        data.put("pub_timestamp", pubTimestamp);
    }

    protected void filterFields(JSONObject value) {
        String title = value.getString("title");
        String context = value.getString("content");
        // 标题正文长度小于5的数据不要
        if (title == null || context == null || title.length()<5 || context.length()<5) {
            throw new RuntimeException("非法数据, 文本title或正文或发布时间长度非法.");
        }
    }
}
