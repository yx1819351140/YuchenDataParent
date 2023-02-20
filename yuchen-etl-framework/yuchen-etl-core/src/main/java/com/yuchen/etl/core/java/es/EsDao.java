package com.yuchen.etl.core.java.es;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 14:32
 * @Package: com.yuchen.etl.core.java.es
 * @ClassName: EsDao
 * @Description: EsDao对象
 **/
public class EsDao {

    private final RestHighLevelClient esClient;

    public EsDao(RestHighLevelClient esClient) {
        this.esClient = esClient;
        //
    }

    public JSONObject getDocumentById(String indexName, String typeName, String... ids) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        searchRequest.source().query(QueryBuilders.idsQuery().addIds(ids));
        try {
            SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
            List<JSONObject> result = getResult(search);
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<JSONObject> getResult(SearchResponse response) {
        List<JSONObject> resultList = new ArrayList<>();
        if (null != response) {
            //取到hits
            SearchHit[] hits = response.getHits().getHits();
            if (null != hits) {
                for (SearchHit hit : hits) {
                    //这就是取到的数据的Json格式数据
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    long version = hit.getVersion();
                    float score = hit.getScore();
                    //使用Gson转换成Bean放进结果list中
                    JSONObject jsonObject = new JSONObject(sourceAsMap);
                    resultList.add(jsonObject);
                }
            }
        }
        return resultList;
    }

    public void insert(JSONObject obj) {
        UpdateRequest version = new UpdateRequest("posts", "_doc", "123")
                .doc(XContentType.JSON, "other", "test")
                .version(1);

    }

    public void insertAll(List<JSONObject> objs) {
        //创建bulkRequest
        //创建Linster

//        esClient.bulkAsync();
    }


}
