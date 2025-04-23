package com.facade.edi.starter.service.impl;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.exception.EdiException;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.service.AbstractInvokeHttpFacade;
import com.facade.edi.starter.util.MapUtil;
import com.facade.edi.starter.util.StringUtil;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

public class WebClientInvokeHttpFacade extends AbstractInvokeHttpFacade {

    private WebClient webClient;


    public WebClientInvokeHttpFacade(WebClient webClient) {
        super();
        this.webClient = webClient;
    }

    @Override
    public HttpApiResponse invoke(HttpApiRequest request) {
        log.info("WebClientInvokeHttpFacade.invoke request={}",request);
        HttpApiResponse resp = new HttpApiResponse();

        try {
           String stringResponse =  this.webClient.method(HttpMethod.resolve(request.getHttpMethod()))
                   .uri(uriBuilder -> {
                       // 使用 UriComponentsBuilder 正确解析 URL
                       return this.buildUri(request);
                   })
                    .body(buildBody(request))
                    .retrieve()
                    .onStatus(httpStatus -> {
                       resp.setHttpCode(httpStatus.value());
                       return httpStatus.is4xxClientError() || httpStatus.is5xxServerError();
                       }, response -> response.bodyToMono(String.class)
                                   .flatMap(body -> Mono.error(new EdiException(response.statusCode().value() , body))))
                    .bodyToMono(String.class)
                    .block();
           resp.setResponse(stringResponse);
        } catch (Exception e) {
            log.error("WebClientInvokeHttpFacade.invokeResponse http invoke error;url={}",request.getUrl(), e);
            resp.setHttpCode(500);
            resp.setHttpErrorMsg(e.getMessage());
        }
        return resp;
    }

    private <T> Mono<T> handleResponse(ClientResponse response, Class<T> responseType) {
        if (response.statusCode().isError()) {
            return response.bodyToMono(String.class)
                    .flatMap(errorBody -> Mono.error(new EdiException(
                            response.statusCode().value(),errorBody)));
        }
        return response.bodyToMono(responseType);
    }

    private BodyInserter buildBody(HttpApiRequest request) {
        if(StringUtil.isNotBlank(request.getBody())) {
            return BodyInserters.fromValue(request.getBody());
        } else if(MapUtil.isNotEmpty(request.getFields())) {
            MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
            for (Map.Entry<String, String> entry : request.getFields().entrySet()) {
                map.add(entry.getKey(), entry.getValue());
            }
            return BodyInserters.fromFormData(map);
        } else {
            return BodyInserters.empty();
        }
    }

    @Override
    public void preheat(String host) {
        log.info("WebClientInvokeHttpFacade.preheat trigger preheat;host={}",host);
        try {
            this.webClient.method(HttpMethod.HEAD)
                    .uri(host)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            return Mono.empty();
                        }
                        return Mono.error(new IllegalStateException("Preheat failed: " + response.statusCode()));
                    })
                    .onErrorResume(e -> {
                        // 记录日志但不中断流程
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            log.error("WebClientInvokeHttpFacade.preheat failed,please ignore...,host={}",host);
        }
    }

    @Override
    public EnableEdiApiScan.ClientType getClientType() {
        return EnableEdiApiScan.ClientType.WEB_CLIENT;
    }
}
