package com.yuchen.data.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/21 15:48
 * @Package: com.yuchen.data.service.model.entity
 * @ClassName: Config
 * @Description: 配置类
 **/
@Data
@TableName("t_config")
public class Config implements Serializable {
    private Integer id;
    private String configId;
    private Integer configType;
    private String configStr;
    private String configClass;
    private String create_by;
    private Date create_time;
    private Date update_time;
}
