package com.yuchen.data.api.service;

import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 9:28
 * @Package: com.yuchen.data.api.service
 * @ClassName: IHbaseService
 * @Description: Hbase服务接口
 **/
public interface IHbaseService extends IDataService {
    ServiceResponse test(ServiceRequest request);
}
