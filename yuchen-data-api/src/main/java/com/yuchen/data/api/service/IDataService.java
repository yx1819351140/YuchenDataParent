package com.yuchen.data.api.service;

import com.yuchen.data.api.enums.ResponseStatus;
import com.yuchen.data.api.pojo.ServiceRequest;
import com.yuchen.data.api.pojo.ServiceResponse;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 9:47
 * @Package: com.yuchen.data.api.service
 * @ClassName: IDataService
 * @Description: 数据服务接口
 **/
public interface IDataService extends Serializable {
    default ServiceResponse query(ServiceRequest request) {
        return ServiceResponse.newResponse(ResponseStatus.UNSUPPORTED_OPERATION);
    }

    default ServiceResponse insert(ServiceRequest request) {
        return ServiceResponse.newResponse(ResponseStatus.UNSUPPORTED_OPERATION);
    }

    default ServiceResponse update(ServiceRequest request) {
        return ServiceResponse.newResponse(ResponseStatus.UNSUPPORTED_OPERATION);
    }

    default ServiceResponse delete(ServiceRequest request) {
        return ServiceResponse.newResponse(ResponseStatus.UNSUPPORTED_OPERATION);
    }
}
