package com.facade.edi.starter.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class HttpApiResponse implements Serializable {
    private static final long serialVersionUID = -1L;
    private Integer httpCode;
    private String httpErrorMsg;
    private String response;
    private Map<String,String> header;
}
