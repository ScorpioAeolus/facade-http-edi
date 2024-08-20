package com.facade.edi.starter.request;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class InvokeRequest {

    private String url;

    private String httpMethod;

    private Map<String,String> header;

    private String body;

    private Map<String, String> params=new HashMap<>();

    private Map<String, String> fields;

    private boolean needResponseHead;

}
