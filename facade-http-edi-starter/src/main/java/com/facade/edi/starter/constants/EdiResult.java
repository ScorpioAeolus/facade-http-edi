package com.facade.edi.starter.constants;

import com.facade.edi.starter.exception.EdiException;

import java.io.Serializable;

/**
 * 统一的结果封装类
 *
 * @author typhoon
 * @since V2.0.0
 */
public class EdiResult<T> implements Serializable {


    private static final long serialVersionUID = 7368053860381167920L;

    private Integer code;
    private String message;

    private T data;

    public static EdiResult success() {
        return getResult(EntityError.SUCCESS);
    }

    public static <T> EdiResult<T> success(T data) {
        return getResult(EntityError.SUCCESS, data);
    }

    public static EdiResult fail(Integer failureCode, String failureMessage) {
        return getResult(failureCode, failureMessage, null);
    }

    public static <T> EdiResult fail(Integer failureCode, String failureMessage, T data) {
        return getResult(failureCode, failureMessage, data);
    }

    public static EdiResult getResult(Integer code, String message) {
        return getResult(code, message, null);
    }

    public static EdiResult<Void> getResult(EntityError entityError) {
        return getResult(entityError.getCode(), entityError.getMsg(), null);
    }

    public static EdiResult fail(EdiException e) {
        return getResult(e.getCode(), e.getMsg(), null);
    }

    public static <T> EdiResult<T> getResult(EntityError entityError, T data) {
        return getResult(entityError.getCode(), entityError.getMsg(), data);
    }

    public static EdiResult fail(IError entityError) {
        return getResult(entityError.getCode(), entityError.getMsg(), null);
    }


    private static <T> EdiResult<T> getResult(Integer code, String message, T data) {
        EdiResult result = new EdiResult();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    private EdiResult() {
    }

    public static EdiResult fail(String msg) {
        return getResult(EntityError.SYSTEM_ERROR.getCode(), msg, null);
    }

    public boolean isSuccess() {
        return is(EntityError.SUCCESS);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public boolean is(EntityError entityError) {
        return (code != null && code.equals(entityError.getCode()));
    }

    public void setEnum(EntityError entityError) {
        this.setCode(entityError.getCode());
        this.setMessage(entityError.getMsg());
    }

    public void setResult(EdiResult<?> result) {
        this.setCode(result.getCode());
        this.setMessage(result.getMessage());
    }


}
