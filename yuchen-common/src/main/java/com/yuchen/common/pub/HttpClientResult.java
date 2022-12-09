package com.yuchen.common.pub;

import java.io.Serializable;


/**
 * @Author: xiaozhennan
 * @Date: 2022/12/9 17:46
 * @Package: com.yuchen.common.pub.HttpClientResult
 * @ClassName: HttpClientResult
 * @Description:
 **/
@SuppressWarnings("serial")
public class HttpClientResult implements Serializable {
    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应数据
     */
    private String content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HttpClientResult() {
        super();
    }

    public HttpClientResult(int code) {
        super();
        this.code = code;
    }

    public HttpClientResult(int code, String content) {
        super();
        this.code = code;
        this.content = content;
    }

    @Override
    public String toString() {
        return "HttpClientResult [code=" + code + ", content=" + content + "]";
    }
}