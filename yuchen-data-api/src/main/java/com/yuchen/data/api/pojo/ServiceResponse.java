package com.yuchen.data.api.pojo;

import com.yuchen.data.api.enums.ResponseStatus;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class ServiceResponse<T extends Serializable> implements Serializable {


    private static final long serialVersionUID = 7148323151393906109L;

    /**
     * 接口调用成功，不需要返回对象
     */
    public static <T extends Serializable> ServiceResponse<T> newSuccess() {
        ServiceResponse<T> serviceResponse = new ServiceResponse<>();
        serviceResponse.setCode(ResponseStatus.SUCCESS.getCode());
        serviceResponse.setMessage(ResponseStatus.SUCCESS.getMessage());
        return serviceResponse;
    }

    /**
     * 接口调用成功，有返回对象
     */
    public static <T extends Serializable> ServiceResponse<T> newSuccess(T data) {
        ServiceResponse<T> serviceResponse = new ServiceResponse<>();
        serviceResponse.setCode(ResponseStatus.SUCCESS.getCode());
        serviceResponse.setMessage(ResponseStatus.SUCCESS.getMessage());
        serviceResponse.setData(data);
        return serviceResponse;
    }

    /**
     * 接口调用失败，有错误码和描述，没有返回对象
     */
    public static <T extends Serializable> ServiceResponse<T> newFailure(int code, String message) {
        ServiceResponse<T> serviceResponse = new ServiceResponse<>();
        serviceResponse.setCode(code != 0 ? code : -1);
        serviceResponse.setMessage(message);
        return serviceResponse;
    }

    /**
     * 接口调用失败，有错误字符串码和描述，没有返回对象
     */
    public static <T extends Serializable> ServiceResponse<T> newFailure(String error, String message) {
        ServiceResponse<T> serviceResponse = new ServiceResponse<>();
        serviceResponse.setCode(-1);
        serviceResponse.setError(error);
        serviceResponse.setMessage(message);
        return serviceResponse;
    }

    /**
     * 转换或复制错误结果
     */
    public static <T extends Serializable> ServiceResponse<T> newFailure(ServiceResponse<?> failure) {
        ServiceResponse<T> serviceResponse = new ServiceResponse<>();
        serviceResponse.setCode(failure.getCode() != 0 ? failure.getCode() : -1);
        serviceResponse.setError(failure.getError());
        serviceResponse.setMessage(failure.getMessage());
        serviceResponse.setException(failure.getException());
        return serviceResponse;
    }

    /**
     * 接口调用失败，返回异常信息
     */
    public static <T extends Serializable> ServiceResponse<T> newException(Exception e) {
        ServiceResponse<T> serviceResponse = new ServiceResponse<>();
        serviceResponse.setCode(-1);
        serviceResponse.setException(e);
        serviceResponse.setMessage(e.getMessage());
        return serviceResponse;
    }

    private int code;
    private T data;
    private String error;
    private String message;
    private Exception exception;

    public static ServiceResponse newResponse(ResponseStatus status) {
        ServiceResponse response = new ServiceResponse<>();
        response.setCode(status.getCode());
        response.setMessage(status.getMessage());
        response.setError(status.getMessage());
        return response;
    }

    public static <T extends Serializable> ServiceResponse newResponse(ResponseStatus status, T obj) {
        ServiceResponse response = new ServiceResponse<>();
        response.setCode(status.getCode());
        response.setMessage(status.getMessage());
        response.setError(status.getMessage());
        response.setData(obj);
        return response;
    }

    /**
     * 判断返回结果是否成功
     */
    public boolean success() {
        return code == 0;
    }

    /**
     * 判断返回结果是否有结果对象
     */
    public boolean hasData() {
        return code == 0 && data != null;
    }

    /**
     * 判断返回结果是否有异常
     */
    public boolean hasException() {
        return exception != null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String toString() {
        StringBuilder result = new StringBuilder("ServiceResponse");
        if (data != null) result.append("<" + data.getClass().getSimpleName() + ">");
        result.append(": {code=" + code);
        if (data != null) result.append(", data=" + data);
        if (error != null) result.append(", error=" + error);
        if (message != null) result.append(", message=" + message);
        if (exception != null) {
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
            result.append(", exception=" + stringWriter.toString());
        }
        result.append(" }");
        return result.toString();
    }
}