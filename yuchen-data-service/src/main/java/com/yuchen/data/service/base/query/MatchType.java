package com.yuchen.data.service.base.query;

/**
 * @description: TODO 用枚举类表示查询连接条件
 * @title: MatchType
 * @projectName parent
 * @author lizhiwei
 * @date 2019/6/17 14:57
 */
public enum MatchType {
    /**
     * filed = value
     */
    equal,
    /**
     * 下面四个用于Number类型的比较
     * greaterThan  filed > value
     */
    gt,
    /**
     * lessThan field >= value
     */
    ge,
    /**
     *  greaterThanOrEqualTo field < value
     */
    lt,
    /**
     *  lessThanOrEqualTo field <= value
     */
    le,
    /**
     * field != value
     */
    notEqual,
    /**
     * field like value
     */
    like,
    /**
     *  field not like value
     */
    notLike,
    /**
     * 下面四个用于可比较类型(Comparable)的比较
     * field > value
     */
    greaterThan,
    /**
     * field >= value
     */
    greaterThanOrEqualTo,
    /**
     * field < value
     */
    lessThan,
    /**
     * field <= value
     */
    lessThanOrEqualTo,
    /**
     * between value1 and value2 ,Type is Date
     */
    between,
    ;
}
