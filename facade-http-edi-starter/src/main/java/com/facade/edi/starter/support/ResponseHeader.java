package com.facade.edi.starter.support;


import java.util.Map;


public class ResponseHeader {
    private Map<String,String> responseHeader;

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Map<String, String> responseHeader) {
        this.responseHeader = responseHeader;
    }
}
