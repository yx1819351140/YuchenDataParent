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
        //生成ID
        JSONObject data = value.getJSONObject("data");
        // 生成ID:title_id
        handleNewsTitle(data);

        // 过滤脏数据
        filterFields(data);

        // 获得原始domain, 或者从url中提取domain
        handleWebSite(data);

        // gdelt中的catalog处理为category
        handleCatalog(data);

        //处理数据时间,从value中获取时间戳,写入到data中
        handleDataTime(value, data);

        //添加媒体
        handleMediaInfo(data);

        //data数据放回value
        value.put("data", data);
    }

    private static void handleDataTime(JSONObject value, JSONObject data) throws ParseException {
        Long timestamp = value.getLong("timestamp");
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        // 获取正文数据入库时间作为发布时间
        String pub_time = data.getString("date_v1").replace("T", " ").substring(0, 19);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = simpleDateFormat.parse(pub_time);
        Long pub_timestamp = parse.getTime();
        data.put("create_time", time);
        data.put("pub_time", pub_time);
        data.put("pub_timestamp", pub_timestamp);
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
