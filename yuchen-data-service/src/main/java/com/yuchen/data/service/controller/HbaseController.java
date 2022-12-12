package com.yuchen.data.service.controller;

import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;
import com.yuchen.data.api.service.IEsService;
import com.yuchen.data.api.service.IHbaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 16:20
 * @Package: com.yuchen.data.service.controller
 * @ClassName: HbaseController
 * @Description: HbaseController
 **/
@RestController()
@RequestMapping("hbase")
public class HbaseController {

    private static final Logger logger = LoggerFactory.getLogger(HbaseController.class);
    @Autowired
    private IHbaseService iHbaseService;

    @PostMapping("test")
    public ServiceResponse testHbase(@RequestBody ServiceRequest request){
        logger.info("EsController 接收到请求, 请求参数: {}", request.toString());
        return iHbaseService.test(request);
    }

}
