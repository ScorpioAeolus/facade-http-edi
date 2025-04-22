package com.facade.edi.starter.request;

import com.facade.edi.starter.converter.ClientResponseConverter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    private boolean needResponseHead;

    private ClientResponseConverter<?> responseConverter;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getPathParam() {
        return pathParam;
    }

    public void setPathParam(Map<String, String> pathParam) {
        this.pathParam = pathParam;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isNeedResponseHead() {
        return needResponseHead;
    }

    public void setNeedResponseHead(boolean needResponseHead) {
        this.needResponseHead = needResponseHead;
    }

    public ClientResponseConverter<?> getResponseConverter() {
        return responseConverter;
    }

    public void setResponseConverter(ClientResponseConverter<?> responseConverter) {
        this.responseConverter = responseConverter;
    }

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


    @Override
    public String toString() {
        return "HttpApiRequest{" +
                "host='" + host + '\'' +
                ", url='" + url + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", params=" + params +
                ", pathParam=" + pathParam +
                ", headers=" + headers +
                ", fields=" + fields +
                ", body='" + body + '\'' +
                ", needResponseHead=" + needResponseHead +
                '}';
    }
}
