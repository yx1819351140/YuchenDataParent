package com.yuchen.data.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuchen.common.utils.CheckTool;
import com.yuchen.data.api.enums.ConfigType;
import com.yuchen.data.api.service.IConfigService;
import com.yuchen.data.service.mapper.ConfigMapper;
import com.yuchen.data.service.model.entity.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/21 15:12
 * @Package: com.yuchen.data.service.impl
 * @ClassName: IConfigServiceImpl
 * @Description: 配置服务实现
 **/
@DubboService(version = "1.0.0")
public class IConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

    @Autowired
    private ConfigMapper configMapper;

    @Override
    public String getConfig(String configId) {
        CheckTool.checkNotNull(configId);
        LambdaQueryChainWrapper<Config> queryChainWrapper = new LambdaQueryChainWrapper<>(configMapper)
                .eq(Config::getConfigId, configId);
        Config config = queryChainWrapper.one();
        return config.getConfigStr();
    }
}
