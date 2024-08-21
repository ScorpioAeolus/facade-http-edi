package com.facade.edi.starter.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应码
 *
 * @author typhoon
 * @date 2019-07-11 14:45
 * @since V2.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityError implements Serializable, IError {

    protected int code;

    protected String msg;

    public static final EntityError SUCCESS = new EntityError(0, "success");
    public static final EntityError IP_LIMIT = new EntityError(400, "IP limited");
    public static final EntityError NOT_PERMISSION = new EntityError(403, "Insufficient authority");
    public static final EntityError ILLEGAL_ARGUMENT = new EntityError(410, "illegal argument");

    public static final EntityError TIMEOUT = new EntityError(510, "timeout");
    public static final EntityError SYSTEM_ERROR = new EntityError(500, "system error");

    public static final EntityError HTTP_METHOD_NOT_SUPPORT = new EntityError(501, "http method not support");

    public static final EntityError API_HTTP_ERROR = new EntityError(506, "remote http server occur error");

    public static final EntityError RPC_RESPONSE_NULL= new EntityError(507,"service response null");
    public static final EntityError RPC_RESPONSE_FAILED= new EntityError(580,"service response failed");
    public static final EntityError RPC_RESPONSE_FALSE= new EntityError(509,"service response false");
    public static final EntityError RPC_OCCUR_ERROR= new EntityError(511,"service occur error");
    public static final EntityError RPC_RESPONSE_ERROR      = new EntityError(512,"rpc response error");


}