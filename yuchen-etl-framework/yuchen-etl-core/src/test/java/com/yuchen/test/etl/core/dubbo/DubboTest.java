package com.yuchen.test.etl.core.dubbo;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;
import com.yuchen.data.api.service.IEsService;
import com.yuchen.data.api.service.IHbaseService;
import com.yuchen.data.api.service.IMongoService;
import com.yuchen.etl.core.java.dubbo.DubboServiceHolder;
import org.junit.Test;

import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 11:12
 * @Package: com.yuchen.etl.core.java.dubbo
 * @ClassName: DubboTest
 * @Description: Dubbo服务调用工具
 **/
public class DubboTest {
    @Test
    public void testSimple() {
        Properties properties = new Properties();
        properties.setProperty("dubbo.application.name", "test");
        properties.setProperty("dubbo.registry.address", "zookeeper://datanode01:2181,datanode02:2181,datanode03:2181");
        DubboServiceHolder.config(properties);
        IEsService service = DubboServiceHolder.getInstance().getService(IEsService.class, "1.0.0");
        System.out.println(service);
        ServiceResponse query = service.query(new ServiceRequest());
        System.out.println(query);
    }

    @Test
    public void testHbaseGet() {
        Properties properties = new Properties();
        properties.setProperty("dubbo.application.name", "test");
        properties.setProperty("dubbo.registry.address", "zookeeper://datanode01:2181,datanode02:2181,datanode03:2181");
        DubboServiceHolder.config(properties);



        IHbaseService service = DubboServiceHolder.getInstance().getService(IHbaseService.class, "1.0.0");
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setTable("url_hbase_v1");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rowKey", "0000013e7b2cc1d889b3fc2cf7ab2a11");
        serviceRequest.setRequest(jsonObject);
        ServiceResponse query = service.test(serviceRequest);
        System.out.println(query);
    }
}
