package com.yuchen.data.service.controller;

import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;
import com.yuchen.data.api.service.IConfigService;
import com.yuchen.data.api.service.IEsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/21 16:25
 * @Package: com.yuchen.data.service.controller
 * @ClassName: ConfigController
 * @Description:
 **/
@RestController()
@RequestMapping("config")
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
    @Autowired
    private IConfigService iConfigService;

    @RequestMapping(value = "/get/{configId}", method = RequestMethod.GET)
    public ServiceResponse getConfig(@PathVariable String configId) {
        logger.info("ConfigController 接收到请求, 请求参数: {}", configId);
        String config = iConfigService.getConfig(configId);
        return ServiceResponse.newSuccess(config);
    }
}

