package com.yuchen.data.service.controller;

import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;
import com.yuchen.data.api.service.IEsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 11:07
 * @Package: com.yuchen.data.service.controller
 * @ClassName: EsController
 * @Description: Es查询服务
 **/
@RestController()
@RequestMapping("es")
public class EsController {

    private static final Logger logger = LoggerFactory.getLogger(EsController.class);
    @Autowired
    private IEsService iEsService;

    @PostMapping("test")
    public ServiceResponse testEs(@RequestBody ServiceRequest request){
        logger.info("EsController 接收到请求, 请求参数: {}", request.toString());
        return iEsService.query(request);
    }

}

