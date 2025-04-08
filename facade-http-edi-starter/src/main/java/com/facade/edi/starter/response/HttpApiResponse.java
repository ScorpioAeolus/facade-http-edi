package com.facade.edi.starter.response;

import java.io.Serializable;
import java.util.Map;

public class HttpApiResponse implements Serializable {
    private static final long serialVersionUID = -1L;
    private Integer httpCode;
    private String httpErrorMsg;
    private String response;
    private Map<String,String> header;


    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public String getHttpErrorMsg() {
        return httpErrorMsg;
    }

    public void setHttpErrorMsg(String httpErrorMsg) {
        this.httpErrorMsg = httpErrorMsg;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
