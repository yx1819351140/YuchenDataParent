package com.yuchen.data.service.base.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: TODO 查询字段设置
 * @title: QueryWord
 * @projectName parent
 * @author lizhiwei
 * @date 2019/6/17 14:54
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QueryWord {
    // 数据库中字段名,默认为空字符串,则Query类中的字段要与数据库中字段一致
    String column() default "";
    // equal, like, gt, lt...
    MatchType func() default MatchType.equal;
    // object是否可以为null
    boolean nullable() default false;
    // 字符串是否可为空
    boolean emptiable() default false;
    // between...and... 查询语句标识， 0时间  1数字类型
    BetweenType type() default BetweenType.datetime;
}
