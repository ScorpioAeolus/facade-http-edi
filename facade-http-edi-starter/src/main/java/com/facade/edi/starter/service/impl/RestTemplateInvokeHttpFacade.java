package com.facade.edi.starter.service.impl;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.service.AbstractInvokeHttpFacade;
import com.facade.edi.starter.util.MapUtil;
import com.facade.edi.starter.util.StringUtil;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * spring自带的restTemplate
 *
 * @author typhoon
 * @date 2024-08-19 11:44
 */
@Slf4j
public class RestTemplateInvokeHttpFacade extends AbstractInvokeHttpFacade {


    private RestTemplate restTemplate;


    public RestTemplateInvokeHttpFacade(RestTemplate restTemplate) {
        super();
        this.restTemplate = restTemplate;
    }

    @Override
    public HttpApiResponse invoke(HttpApiRequest request) {
        log.info("RestTemplateInvokeHttpFacade.invoke request={}",request);
        HttpApiResponse resp = new HttpApiResponse();
        HttpHeaders headers = this.processRequestHeader(request);
        HttpEntity<?> httpEntity = this.processRequestBody(headers,request);

        URI uri = this.buildUri(request);
        try {
            ResponseEntity<String> response = this.restTemplate.exchange(uri
                    , Objects.requireNonNull(HttpMethod.resolve(request.getHttpMethod()))
                    , httpEntity
                    , String.class);
            resp.setHttpCode(response.getStatusCodeValue());
            resp.setResponse(response.getBody());
            resp.setHttpErrorMsg(response.getStatusCode().getReasonPhrase());
        } catch (RestClientException e) {
            log.error("RestTemplateInvokeHttpFacade.invokeResponse http invoke error;url={}",request.getUrl(), e);
            resp.setHttpCode(500);
            resp.setHttpErrorMsg(e.getMessage());
        }
        return resp;
    }

    private HttpEntity<?> processRequestBody(HttpHeaders headers, HttpApiRequest request) {

        if(StringUtil.isNotBlank(request.getBody())) {
            return new HttpEntity<>(request.getBody(),headers);
        } else if (MapUtil.isNotEmpty(request.getFields())) {
            return new HttpEntity<>(request.getFields(),headers);
        } else {
            return new HttpEntity<>(headers);
        }
    }

    private HttpHeaders processRequestHeader(HttpApiRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Map<String,String> map = request.getHeaders();
        if(MapUtil.isNotEmpty(map)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                headers.set(entry.getKey(),entry.getValue());
            }
        }
        return headers;

    }

    @Override
    public EnableEdiApiScan.ClientType getClientType() {
        return EnableEdiApiScan.ClientType.REST_TEMPLATE;
    }
}
