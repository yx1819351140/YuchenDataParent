package com.yuchen.data.api.service;

import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 9:27
 * @Package: com.yuchen.data.api.service
 * @ClassName: IEsService
 * @Description: Es服务接口
 **/
public interface IEsService extends IDataService {

    ServiceResponse test(ServiceRequest request);

}
