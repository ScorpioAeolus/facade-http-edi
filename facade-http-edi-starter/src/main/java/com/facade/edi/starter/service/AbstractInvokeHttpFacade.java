package com.facade.edi.starter.service;


import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Map;

/**
 * EDI调用远程http & https接口封装
 *
 *
 * @author Typhoon
 */
@Slf4j
public abstract  class AbstractInvokeHttpFacade implements IInvokeHttpFacade {

    protected static final String BEARER = "Bearer ";
    protected static final String GET = "GET";
    protected static final String POST = "POST";
    protected static final String PUT = "PUT";
    protected static final String DELETE = "DELETE";
    protected static final Integer ERROR__HTTP_CODE = 500;

    protected static final String CONTENT_TYPE_KEY = "Content-Type";

    protected static String ENCODING = "UTF-8";



    protected URI buildUri(HttpApiRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getUrl());
        Map<String,String> params = request.getParams();
        if(MapUtil.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.queryParam(entry.getKey(),entry.getValue());
            }
        }
        return builder.build().toUri();
    }

//    @Resource
//    protected InvokeService invokeService;

//    @Override
//    public HttpApiResponse invoke(HttpApiRequest request) {
//        InvokeRequest invokeRequest = new InvokeRequest();
//
//        invokeRequest.setHttpMethod(request.getHttpMethod());
//        invokeRequest.setBody(request.getBody());
//        invokeRequest.setParams(request.getParams());
//        invokeRequest.setFields(request.getFields());
//        invokeRequest.setNeedResponseHead(request.isNeedResponseHead());
//
//        this.doCustomFillRequest(request,invokeRequest);
//
//        InvokeResponse invokeResponse = invokeService.invoke(invokeRequest);
//        DubboApiResponse response = new DubboApiResponse();
//        response.setHttpCode(invokeResponse.getHttpCode());
//        response.setResponse(invokeResponse.getBody());
//        response.setHeader(invokeResponse.getHeaders());
//        response.setHttpErrorMsg(invokeResponse.getHttpErrorMsg());
//        return response;
//    }

//    /**
//     * 自定义填充请求属性
//     *
//     * @param request
//     * @param invokeRequest
//     */
//    protected abstract void doCustomFillRequest(DubboApiRequest request,InvokeRequest invokeRequest);

}
