package com.yuchen.data.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.data.api.enums.ResponseStatus;
import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;
import com.yuchen.data.api.service.IEsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 11:04
 * @Package: com.yuchen.data.service.service
 * @ClassName: IEsServiceImpl
 * @Description: ES服务实现类
 **/
@DubboService(version = "1.0.0")
public class IEsServiceImpl implements IEsService {

    private static final Logger logger = LoggerFactory.getLogger(IEsServiceImpl.class);

    @Autowired
    private ElasticSearchHelper esHelper;


    @Override
    public ServiceResponse query(ServiceRequest request) {
        logger.info("接收到接口请求, 请求参数: {}", JSONObject.toJSONString(request));
        ServiceResponse<Serializable> response = ServiceResponse.newSuccess(System.currentTimeMillis());
        logger.info("接口返回: {}", response.toString());
        return response;
    }

    @Override
    public ServiceResponse test(ServiceRequest request) {
        logger.info("接受到测试请求, 请求参数: {}", request.toString());
        return ServiceResponse.newResponse(ResponseStatus.SUCCESS);
    }
}
