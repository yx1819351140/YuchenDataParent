package com.yuchen.data.api.enums;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 9:29
 * @Package: com.yuchen.data.api.enums
 * @ClassName: OperationType
 * @Description:
 **/
public enum OperationType implements Serializable {

    QUERY,
    INSERT,
    UPDATE,
    DELETE,
    DDL
}
