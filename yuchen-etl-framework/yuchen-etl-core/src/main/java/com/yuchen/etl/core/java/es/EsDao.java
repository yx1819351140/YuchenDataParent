package com.yuchen.etl.core.java.es;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.ElasticSearchHelper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 14:32
 * @Package: com.yuchen.etl.core.java.es
 * @ClassName: EsDao
 * @Description: EsDao对象
 **/
public class EsDao {

    private final RestHighLevelClient esClient;
    private BulkProcessor bulkProcessor;

    public EsDao(RestHighLevelClient esClient) {
        this.esClient = esClient;
        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer =
                (request, bulkListener) -> esClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener);
        EsBulkListener listener = new EsBulkListener();
        bulkProcessor = createBulkProcessor(bulkConsumer, listener);
        listener.setProcessor(bulkProcessor);
    }

    private static BulkProcessor createBulkProcessor(BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer, EsBulkListener listener) {
        return BulkProcessor.builder(bulkConsumer, listener).setBulkActions(1000) //达到刷新的条数
                .setBulkSize(new ByteSizeValue(2, ByteSizeUnit.MB)) //达到刷新的大小
                .setFlushInterval(TimeValue.timeValueSeconds(5)) //固定刷新的时间频率
                .setConcurrentRequests(1) //并发线程数
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueSeconds(3), 6)) // 重试补偿策略
                .build();
    }

    public EsRecord searchById(String indexName, String typeName, String... ids) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
//        searchRequest.source().query(QueryBuilders.termsQuery("_id", ids));
        searchRequest.source().query(QueryBuilders.matchQuery("id", ids[0]));
        try {
            SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
            List<EsRecord> result = getResult(search);
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<EsRecord> search(String indexName, String typeName, QueryBuilder queryBuilder) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        searchRequest.source().query(queryBuilder).version(true).seqNoAndPrimaryTerm(true);
        try {
            SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
            List<EsRecord> result = getResult(search);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<EsRecord> getResult(SearchResponse response) {
        List<EsRecord> resultList = new ArrayList<>();
        if (null != response) {
            //取到hits
            SearchHit[] hits = response.getHits().getHits();
            if (null != hits) {
                for (SearchHit hit : hits) {
                    //这就是取到的数据的Json格式数据
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    JSONObject jsonObject = new JSONObject(sourceAsMap);
                    EsRecord esRecord = new EsRecord();
                    esRecord.setData(jsonObject);
                    esRecord.setIndexName(hit.getIndex());
                    esRecord.setIndexType(hit.getType());
                    esRecord.setPrimaryTerm(hit.getPrimaryTerm());
                    esRecord.setSedNo(hit.getSeqNo());
                    esRecord.setId(hit.getId());
                    resultList.add(esRecord);
                }
            }
        }
        return resultList;
    }

    public void insert(EsRecord record) {
        if (record == null) return;
        IndexRequest indexRequest = new IndexRequest()
                .id(record.getId())
                .index(record.getIndexName())
                .type(record.getIndexType())
                .source(record.getData(), XContentType.JSON);
        bulkProcessor.add(indexRequest);
    }


    /**
     * 更新ES数据
     *
     * @param record 需要
     */
    public void update(EsRecord record) {
        if (record == null) return;
        UpdateRequest updateRequest = new UpdateRequest()
                .id(record.getId())
                .index(record.getIndexName())
                .type(record.getIndexType())
                .docAsUpsert(true)
                .retryOnConflict(10) //发生版本冲突时的重试次数
//                .setIfSeqNo(record.getSedNo())
//                .setIfPrimaryTerm(record.getPrimaryTerm()) //这里暂时不要额外的并发控制
                .doc(record.getData(), XContentType.JSON);
        bulkProcessor.add(updateRequest);
    }

    public void upsert(EsRecord record) {
        if (record == null) return;
        UpdateRequest updateRequest = new UpdateRequest()
                .id(record.getId())
                .index(record.getIndexName())
                .type(record.getIndexType())
                .retryOnConflict(10)
                .upsert(record.getData(), XContentType.JSON);
        bulkProcessor.add(updateRequest);
    }

    public void deleteById(String indexName, String indexType, String id) {
        DeleteRequest updateRequest = new DeleteRequest()
                .id(id)
                .index(indexName)
                .type(indexType == null ? "_doc" : indexType);
        bulkProcessor.add(updateRequest);
    }

    public void insertAll(List<EsRecord> objs) {
        if (objs != null) {
            objs.forEach(obj -> insert(obj));
        }
    }

    public static void main(String[] args) {
        Map<String, Object> esConfig = new HashMap<>();
        esConfig.put("elasticsearch.hosts", "192.168.12.197,192.168.12.198,192.168.12.199");
        esConfig.put("elasticsearch.port", "9200");
        ElasticSearchHelper.config(esConfig);
        ElasticSearchHelper esHelper = ElasticSearchHelper.getInstance();
        RestHighLevelClient esClient = esHelper.getEsClient();
        EsDao esDao = new EsDao(esClient);
        EsRecord esRecord = esDao.searchById("yuchen_news", "_doc", "9d346f730632e9bcf6811e2b19e5ba79");
        System.out.println(esRecord.getData());
    }


}
