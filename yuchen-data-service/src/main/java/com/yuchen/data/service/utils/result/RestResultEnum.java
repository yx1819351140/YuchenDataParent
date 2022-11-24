package com.yuchen.data.service.utils.result;

/**
 * 自定义异常枚举类
 *
 * @author fan guoping
 * @version 1.0
 * @createDate 2019/6/12 14:47
 */
public enum RestResultEnum {
    //通用 客户端错误导致服务器处理异常 990004xx
    SUCCESS(0, "操作成功"),
    ERROR(99000401, "操作错误"),
    UNKNOWN_ERROR(99000402, "未知异常错误"),
    REQUEST_PARAM_ERROR(99000410, "请求参数错误"),
    ARGUMENT_ERROR(99000411, "缺少请求参数"),
    CONNECT_TIMED_OUT(99000412, "连接超时"),
    NOLOGIN(99000413, "连接超时"),
    REMOTE_ERROR(10010108,"数据服务调用异常"),

    //字符处理错误码"02"
    INFORMATION_TODATE_ERROR(99000420, "字符串转时间异常"),

    //用户模块错误码“100102xx”
    LOGIN_USERNAME_PASSWORD_ERROR(10000001, "用户名或密码错误"),
    PHONE_IS_EXIST(10010216,"手机号已存在"),
    USER_IS_EXIST(10010201, "用户已存在"),
    USER_NOT_EXIST(10010202, "用户不存在"),
    RECIVE_PASSWORD_DISACCORD(10010203, "两次密码输入不一致"),

    //文件管理模块错误码“100103xx”
    UPLOAD_TYPE_ERROR(10010301, "文件格式不正确"),
    UPLOAD_FILE_ERROR(10010302, "上传文件失败"),
    DOC_NOT_DELETE(10010303, "文档处于发布状态，不能删除"),

    //角色模块错误码"100104xx"
    ROLE_NOT_PREMIT(10010401, "没有相应角色的操作权限"),
    ROLE_NOT_EXISTS_ERROR(10010402, "没有相应角色信息"),
    IS_EXIST(10010403, "记录已存在"),
    IS_NO_EXIST(10010404,"记录不存在"),
    IS_SUBSCRIBE(0,"已订阅"),
    HAVE_SUBSCRIBE(10010405,"已有用户订阅无法删除"),
    HAVE_DOCUMENT(10010406,"主题下有文档无法删除"),
    HAVE_CHILDREN(10010407,"存在子主题无法删除"),
    IS_DELETE(10010408,"记录已删除"),
    
    NAME_IS_EXIST(10010501, "名称已存在"),
    ENTITY_IS_NOT_EXIST(10010502, "分类不存在"),

    ;


    private int key;
    private String message;

    RestResultEnum(int key, String message) {
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
