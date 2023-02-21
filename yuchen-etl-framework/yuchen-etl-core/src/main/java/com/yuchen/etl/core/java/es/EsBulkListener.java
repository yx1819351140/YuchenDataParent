package com.yuchen.etl.core.java.es;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/21 10:19
 * @Package: com.yuchen.etl.core.java.es
 * @ClassName: EsBulkListener
 * @Description:
 **/
public class EsBulkListener implements BulkProcessor.Listener {

    private static final Logger logger = LoggerFactory.getLogger(EsBulkListener.class);
    private BulkProcessor processor;


    public void setProcessor(BulkProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
        logger.debug("before Bulk: executionId:{}", executionId);
    }

    /**
     * 批次执行完成后的回调
     *
     * @param executionId 批次完成
     * @param request     批次Request
     * @param response    批次Response
     *                    注意这个方法回调的不一定都是成功的,只是代表本次bulkrequest成功
     *                    bulk内的requests具体是否成功需要单独查看
     */
    @Override
    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        RestStatus status = response.status();
        Iterator<BulkItemResponse> iterator = response.iterator();
        if (response.hasFailures()) {
            while (iterator.hasNext()) {
                BulkItemResponse next = iterator.next();
                if (next.isFailed()) {
                    logger.error("es bulk error, id: {}, index:{}, opType: {}, message: {}", next.getId(), next.getIndex(), next.getOpType(), next.getFailureMessage());
                }
            }
        } else {
            logger.info("after bulk success, executionId:{}, status: {}, request size: {}", executionId, status.getStatus(), request.numberOfActions());
        }
    }

    /**
     * 请求失败时会回调此方法
     *
     * @param executionId 批次ID
     * @param request     bulkRequest
     * @param failure     请求错误异常
     */
    @Override
    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        String message = failure.getMessage();
        logger.info("after bulk failure : executionId: {} , request count: {}, message: {}", executionId, request.numberOfActions(), message);
    }
}
