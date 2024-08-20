package com.facade.edi.starter.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 响应类
 *
 * @param <T>
 */
@Data
public class BaseResult <T> implements Serializable {

    private static final long serialVersionUID = 3993914797032369295L;

    private int code;

    private T data;

    private String message;
}
