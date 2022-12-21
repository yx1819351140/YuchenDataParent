package com.yuchen.data.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.data.api.enums.ResponseStatus;
import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;
import com.yuchen.data.api.service.IHbaseService;
import com.yuchen.common.pub.HbaseHelper;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 16:06
 * @Package: com.yuchen.data.service.impl
 * @ClassName: IHbaseServiceImpl
 * @Description: Hbase服务实现
 **/
@DubboService(version = "1.0.0")
public class IHbaseServiceImpl implements IHbaseService {
    private static final Logger logger = LoggerFactory.getLogger(IHbaseServiceImpl.class);

    @Autowired
    private HbaseHelper hbaseHelper;

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
        JSONObject req = request.getRequest();
        String rowKey = req.getString("rowKey");
        if (rowKey == null) {
            return ServiceResponse.newSuccess("rowKey为空, 直接返回");
        }
        try {
            JSONObject jsonObject = hbaseHelper.selectRow(request.getTable(), rowKey);
            return ServiceResponse.newResponse(ResponseStatus.SUCCESS, jsonObject);
        } catch (IOException e) {
            return ServiceResponse.newException(e);
        }
    }
}
