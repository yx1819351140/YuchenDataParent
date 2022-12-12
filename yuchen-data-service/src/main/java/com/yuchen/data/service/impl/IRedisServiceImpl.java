package com.yuchen.data.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;
import com.yuchen.data.api.service.IRedisService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 16:09
 * @Package: com.yuchen.data.service.impl
 * @ClassName: IRedisServiceImpl
 * @Description: Redis服务实现
 **/
@DubboService(version = "1.0.0")
public class IRedisServiceImpl implements IRedisService {

    private static final Logger logger = LoggerFactory.getLogger(IRedisServiceImpl.class);
    @Override
    public ServiceResponse query(ServiceRequest request) {
        logger.info("接收到接口请求, 请求参数: {}", JSONObject.toJSONString(request));
        ServiceResponse<Serializable> response = ServiceResponse.newSuccess(System.currentTimeMillis());
        logger.info("接口返回: {}", response.toString());
        return response;
    }
}
