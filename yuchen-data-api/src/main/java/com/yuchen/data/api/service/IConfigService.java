package com.yuchen.data.api.service;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.data.api.enums.ConfigType;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/21 15:09
 * @Package: com.yuchen.data.api.service
 * @ClassName: IConfigService
 * @Description: 配置服务
 **/
public interface IConfigService {

    String getConfig(String configId);

}
