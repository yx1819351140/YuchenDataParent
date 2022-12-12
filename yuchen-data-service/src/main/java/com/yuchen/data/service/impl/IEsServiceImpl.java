package com.yuchen.data.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;
import com.yuchen.data.api.service.IEsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Override
    public ServiceResponse query(ServiceRequest request) {
        logger.info("接收到接口请求, 请求参数: {}", JSONObject.toJSONString(request));
        ServiceResponse<Serializable> response = ServiceResponse.newSuccess(System.currentTimeMillis());
        logger.info("接口返回: {}", response.toString());
        return response;
    }

    @Override
    public ServiceResponse insert(ServiceRequest request) {
        return ServiceResponse.newSuccess();
    }

    @Override
    public ServiceResponse update(ServiceRequest request) {
        return ServiceResponse.newSuccess();
    }

    @Override
    public ServiceResponse delete(ServiceRequest request) {
        return ServiceResponse.newSuccess();
    }

}
