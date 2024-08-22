package com.facade.edi.starter.exception;


import com.facade.edi.starter.constants.IError;

/**
 * edi异常定义
 *
 * @author typhoon
 */
public class EdiException extends RuntimeException {
    private static final long serialVersionUID = 5518061110734720630L;
    private final int code;
    private final String msg;


    public EdiException(IError errorCode) {
        super("[" + errorCode.getCode() + "]" + errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public EdiException(int code, String msg) {
        super("[" + code + "]" + msg);
        this.code = code;
        this.msg = msg;
    }

    public EdiException(Throwable e, int code, String msg) {
        super("[" + code + "]" + msg, e);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public static EdiException of(IError errorCode) {
        return new EdiException(errorCode);
    }
}
