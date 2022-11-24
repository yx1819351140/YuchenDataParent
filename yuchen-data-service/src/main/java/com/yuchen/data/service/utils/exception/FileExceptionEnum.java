package com.yuchen.data.service.utils.exception;

import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 文件异常枚举类
 * @description
 * @author: mazhenwei
 * @date: 2021年8月3日
 */
public enum FileExceptionEnum {
    /**
     * 没有文件异常
     */
    NO_SUCH_FILE(1001,"文件不存在"),
    /**
     * 无效的端口
     */
    INVALID_PORT(1002,"无效的端口"),
    /**
     * 无效的终结点
     */
    INVALID_ENDPOINT(1003,"无效的终结点"),
    /**
     * 无效的桶名称
     */
    INVALID_BUCKETNAME(1004,"无效的桶名称"),
    /**
     * 没有相关算法
     */
    NO_SUCH_ALGORITHM(1005,"没有相关算法"),
    /**
     * 不饱和的数据
     */
    INSUFFICIENT_DATA(1006,"不饱和的数据"),
    /**
     * io流异常
     */
    IO(1007,"io流异常"),
    /**
     * 无效的密钥
     */
    INVALID_KEY(1008,"无效的密钥"),
    /**
     * 没有响应
     */
    NO_RESPONSE(1009,"没有响应"),
    /**
     * xml解析异常
     */
    XML_PULL_PARSER(1010,"xml解析异常"),
    /**
     * 异常响应
     */
    ERROR_RESPONSE(1011,"异常响应"),
    /**
     * 网络连接异常
     */
    INTERNAL(1012,"网络连接异常"),
    /**
     * 无效的参数
     */
    INVALID_ARGUMENT(1013,"无效的参数"),
    /**
     * 无效的响应
     */
    INVALID_RESPONSE(1014,"无效的响应"),
    /**
     * 区域冲突
     */
    REGION_CONFLICT(1015,"区域冲突"),
    /**
     * 未知异常
     */
    OTHERS(9999,"未知异常");

    /**
     * 异常编码
     */
    private Integer code;
    /**
     * 异常提示
     */
    private String message;

    FileExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message=message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static FileExceptionEnum getException(Exception e){
        if (e instanceof NoSuchFileException){
            return FileExceptionEnum.NO_SUCH_FILE;
        }else if (e instanceof InvalidPortException){
            return FileExceptionEnum.INVALID_PORT;
        }else if (e instanceof InvalidEndpointException){
            return FileExceptionEnum.INVALID_ENDPOINT;
        }else if (e instanceof InvalidBucketNameException){
            return FileExceptionEnum.INVALID_BUCKETNAME;
        }else if (e instanceof NoSuchAlgorithmException){
            return FileExceptionEnum.NO_SUCH_ALGORITHM;
        }else if (e instanceof InsufficientDataException){
            return FileExceptionEnum.INSUFFICIENT_DATA;
        }else if (e instanceof IOException){
            return FileExceptionEnum.IO;
        }else if (e instanceof InvalidKeyException){
            return FileExceptionEnum.INVALID_KEY;
        }else if (e instanceof NoResponseException){
            return FileExceptionEnum.NO_RESPONSE;
        }else if (e instanceof XmlPullParserException){
            return FileExceptionEnum.XML_PULL_PARSER;
        }else if (e instanceof ErrorResponseException){
            return FileExceptionEnum.ERROR_RESPONSE;
        }else if (e instanceof InternalException){
            return FileExceptionEnum.INTERNAL;
        }else if (e instanceof InvalidArgumentException){
            return FileExceptionEnum.INVALID_ARGUMENT;
        }else if (e instanceof InvalidResponseException){
            return FileExceptionEnum.INVALID_RESPONSE;
        }else if (e instanceof RegionConflictException){
            return FileExceptionEnum.REGION_CONFLICT;
        } else {
            return FileExceptionEnum.OTHERS;
        }
    }
}
