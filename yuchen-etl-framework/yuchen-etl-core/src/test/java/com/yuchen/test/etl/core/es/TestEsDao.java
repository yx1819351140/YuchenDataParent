package com.yuchen.test.etl.core.es;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.etl.core.java.es.EsDao;
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
    public void testDaoGet(){
        Map<String, String> config = new HashMap<>();
        config.put("elasticsearch.hosts", "192.168.12.220,192.168.12.221,192.168.12.222,192.168.12.223");
        config.put("elasticsearch.port", "9200");
        ElasticSearchHelper.config(config);
        ElasticSearchHelper helper = ElasticSearchHelper.getInstance();
        EsDao esDao = new EsDao(helper.getEsClient());
//        yuchen_news
        JSONObject documentById = esDao.getDocumentById("yuchen_news", "_doc", "1111111");
    }
}
