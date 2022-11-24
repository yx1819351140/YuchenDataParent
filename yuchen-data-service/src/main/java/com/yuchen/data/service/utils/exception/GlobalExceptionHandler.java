package com.yuchen.data.service.utils.exception;


import com.yuchen.data.service.base.ResponseResultVO;
import com.yuchen.data.service.utils.result.ResponseResult;
import com.yuchen.data.service.utils.result.RestResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Set;

/**
 * 全局异常处理类
 *
 * @author fan guoping
 * @version 1.0
 * @createDate 2019/6/12 14:47
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ParseException.class)
    public ResponseResultVO handleException(ParseException pe) {
        LOGGER.error("字符串转换异常,请通知管理员。ParseException：{}", pe);
        return new ResponseResultVO<>(RestResultEnum.INFORMATION_TODATE_ERROR.getKey(),
            RestResultEnum.INFORMATION_TODATE_ERROR.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResultVO<String> handleException(Exception e) {
        LOGGER.error("未知异常,请通知管理员。Exception：{}", e);
        return new ResponseResultVO<>(RestResultEnum.UNKNOWN_ERROR.getKey(), RestResultEnum.UNKNOWN_ERROR.getMessage());
    }

    @ExceptionHandler(ArgumentException.class)
    public ResponseResultVO<String> handleArgumentException(ArgumentException a) {
        return new ResponseResultVO<>(RestResultEnum.REQUEST_PARAM_ERROR.getKey(), a.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseResultVO<String> handleRSException(AuthException r) {
        LOGGER.error("系统异常,EtlException：{}");
        int code = r.getCode();
        String msg = r.getErrorMsg();
        RestResultEnum resultEnum = RestResultEnum.valueOf(code);
        if (resultEnum != null) {
            return new ResponseResultVO<>(code, msg == null ? resultEnum.getMessage() : msg);
        }
        return new ResponseResultVO<>(RestResultEnum.ERROR.getKey(), "未知服务异常，重新尝试。");
    }

    @ExceptionHandler(BeansException.class)
    public ResponseResultVO<String> handleIllException(BeansException b) {
        LOGGER.error("对象复制异常：BeansException:{}", b);
        return new ResponseResultVO<>(RestResultEnum.ERROR.getKey(), "对象复制异常");
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseResultVO<String> handleBadSqlGrammarException(BadSqlGrammarException sql) {
        LOGGER.error("sql语句异常：BadSqlGrammarException:{}", sql);
        return new ResponseResultVO<>(RestResultEnum.ERROR.getKey(), "sql语句异常，通知开发者。");
    }

    @ExceptionHandler(IOException.class)
    public ResponseResultVO<String> handleIOException(IOException io) {
        LOGGER.error("io流异常：IOException:{}", io);
        return new ResponseResultVO<>(RestResultEnum.ERROR.getKey(), "io流异常，重新尝试。");
    }

    /**
     * 用来处理bean validation异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResultVO<String> resolveConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            StringBuilder msgBuilder = new StringBuilder();
            for (ConstraintViolation constraintViolation : constraintViolations) {
                msgBuilder.append(constraintViolation.getMessage()).append(",");
            }
            String errorMessage = msgBuilder.toString();
            if (errorMessage.length() > 1) {
                errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
            }
            return new ResponseResultVO<>(RestResultEnum.REQUEST_PARAM_ERROR.getKey(), errorMessage);
        }
        return new ResponseResultVO<>(RestResultEnum.REQUEST_PARAM_ERROR.getKey(), ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = RestResultEnum.REQUEST_PARAM_ERROR.getMessage();
        String messageDetail = "";
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            messageDetail += ", " + fieldError.getDefaultMessage();
        }
        if (messageDetail.trim().length() > 0) {
            errorMessage += ":" + messageDetail.substring(1);
        }
        ResponseResultVO error = new ResponseResultVO<>(RestResultEnum.REQUEST_PARAM_ERROR.getKey(), errorMessage, null);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 获取堆栈信息
     *
     * @param throwable
     * @return String
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    /**
     * json传参无效异常
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        ResponseResult error = new ResponseResult<>(RestResultEnum.REQUEST_PARAM_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
