package com.yuchen.test.etl.core.es;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.etl.core.java.es.EsDao;
import com.yuchen.etl.core.java.es.EsRecord;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 15:48
 * @Package: com.yuchen.test.etl.core.es
 * @ClassName: TestEsDao
 * @Description:
 **/
public class TestEsDao {

    @Test
    public void testDaoGet() throws InterruptedException {
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.INFO);
        root.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %-60c %x - %m%n")));
        Map<String, String> config = new HashMap<>();
        config.put("elasticsearch.hosts", "192.168.12.220,192.168.12.221,192.168.12.222,192.168.12.223");
        config.put("elasticsearch.port", "9200");
        ElasticSearchHelper.config(config);
        ElasticSearchHelper helper = ElasticSearchHelper.getInstance();
        EsDao esDao = new EsDao(helper.getEsClient());

        esDao.deleteById("yuchen_news_202303", "_doc", "1111111");

        JSONObject jsonObject = new JSONObject();
        EsRecord record = EsRecord.Builder.anEsRecord()
                .id("1111111")
                .indexName("yuchen_news_202303")
                .indexType("_doc")
                .data(jsonObject)
                .build();
        jsonObject.put("keywords", new String[]{"test", "222", "ccc"});
        esDao.insert(record);
        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        record = esDao.searchById("yuchen_news", "_doc", "1111111");
        JSONObject data = record.getData();
        data.put("keywords", new String[]{"ddd"});
        esDao.update(record);
        Thread.sleep(4000L);
        record = esDao.searchById(record.getIndexName(), record.getIndexType(), record.getId());
        System.out.println(record);

        int index = -1;
        while (index < 999){
            index++;
            Thread.sleep(10L);
        }
    }
}
