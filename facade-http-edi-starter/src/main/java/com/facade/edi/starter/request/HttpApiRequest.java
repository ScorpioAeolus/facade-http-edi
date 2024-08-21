package com.facade.edi.starter.request;

import com.facade.edi.starter.converter.ClientResponseConverter;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class HttpApiRequest implements Serializable {
    private static final long serialVersionUID = -1L;
    private String host;
    private String url;
    private String httpMethod;
    private Map<String, String> params=new HashMap<>();
    private Map<String, String> pathParam=new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> fields;
    private String body;
    private String token;
    private String handle;
    private boolean needResponseHead;

    private ClientResponseConverter<?> responseConverter;

    public void addQueryParam(String name, String queryValue) {
        params.put(name,queryValue);
    }

    public void addPathParam(String name, String pathValue) {
        pathParam.put(name,pathValue);
    }

    public void addFormField(String name, String fieldValue) {
        fields.put(name,fieldValue);
    }

    public void addHeader(String name,String value) {
        headers.put(name,value);
    }

    public void setResponseConverter(ClientResponseConverter<?> responseConverter) {
        this.responseConverter = responseConverter;
    }
}
