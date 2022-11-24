package com.yuchen.data.service.utils.result;


/**
 * 记录状态
 *
 * @author pl
 * @date 2020年12月1日
 */
public enum StateEnum {
    /**
     * 启用
     */
    STATE_ABMORMAL(0, "停用"),
    /**
     * 停用
     */
    STATE_NORMAL(1, "正常"),
    /**
     * 性别
     */
    SEX_WUMEN(2, "女"),
    /**
     * 性别
     */
    SEX_MAN(1, "男"),

    // ****文档状态****
    /**
     * 未发布
     */
    NORMAL(1, "未发布"),
    /**
     * 发布
     */
    RELEASE(2, "发布"),
    /**
     * 下架
     */
    OFFLINE(3, "下架"),

    /**
     * 下架
     */
    NOT_DELETE(1, "正常"),

    /**
     * 下架
     */
    DELETE(2, "删除"),

    //***是否是管理员****
    /**
     * 是
     */
    ISADMIN_YES(0, "是"),
    /**
     * 否
     */
    ISADMIN_NO(1, "否")
    ;

    private int key;
    private String message;

    StateEnum(int key, String message) {
        this.key = key;
        this.message = message;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static RestResultEnum valueOf(Integer key) {
        for (RestResultEnum result : RestResultEnum.values()) {
            if (result.getKey() == key) {
                return result;
            }
        }
        return null;
    }

}
