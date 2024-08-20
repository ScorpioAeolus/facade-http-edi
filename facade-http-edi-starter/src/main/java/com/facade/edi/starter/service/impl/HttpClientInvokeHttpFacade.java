package com.facade.edi.starter.service.impl;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.service.AbstractInvokeHttpFacade;
import com.facade.edi.starter.util.MapUtil;
import com.facade.edi.starter.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient调用实现
 *
 * @author typhoon
 * @date 2024-08-19 11:49 Monday
 *
 */
@Slf4j
public class HttpClientInvokeHttpFacade extends AbstractInvokeHttpFacade {

    private CloseableHttpClient httpClient;

    private static int CONNECT_TIMEOUT = 60000;
    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(CONNECT_TIMEOUT)
            .setConnectTimeout(CONNECT_TIMEOUT)
            .setConnectionRequestTimeout(CONNECT_TIMEOUT)
            .setExpectContinueEnabled(false)
            .build();

    public HttpClientInvokeHttpFacade(CloseableHttpClient httpClient) {
        super();
        this.httpClient = httpClient;
    }

    @Override
    public HttpApiResponse invoke(HttpApiRequest request) {
        log.info("HttpClientInvokeHttpFacade.invoke request={}",request);
        HttpApiResponse resp = new HttpApiResponse();
        CloseableHttpResponse response = null;
        try {
            HttpEntityEnclosingRequestBase req = this.buildRequest(request);
            response = this.httpClient.execute(req);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            resp.setHttpCode(statusCode);
            resp.setResponse(EntityUtils.toString(entity, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("HttpClientInvokeHttpFacade.invokeResponse http invoke error;url={}",request.getUrl(), e);
            resp.setHttpCode(500);
            resp.setHttpErrorMsg(e.getMessage());
        } finally {
            IOUtils.closeQuietly(response);
        }
        return resp;
    }

    private HttpEntityEnclosingRequestBase buildRequest(HttpApiRequest request) throws UnsupportedEncodingException {
        URI uri = this.buildUri(request);
        HttpEntityEnclosingRequestBase http = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return request.getHttpMethod();
            }
        };
        http.setURI(uri);
        Map<String,String> headers = request.getHeaders();
        if(MapUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                http.setHeader(entry.getKey(), entry.getValue());
            }
        }
        HttpEntity httpEntity = this.buildEntity(request);
        http.setEntity(httpEntity);
        http.setConfig(requestConfig);
        return http;
    }

    private HttpEntity buildEntity(HttpApiRequest request) throws UnsupportedEncodingException {
        if(StringUtil.isNotBlank(request.getBody())) {
            return new StringEntity(request.getBody(),ENCODING);
        } else if (MapUtil.isNotEmpty(request.getFields())) {
            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, String> entry : request.getFields().entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params,ENCODING);
            formEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            return formEntity;
        } else {
            return new StringEntity("",ENCODING);
        }
    }

    @Override
    public EnableEdiApiScan.ClientType getClientType() {
        return EnableEdiApiScan.ClientType.HTTP_CLIENT;
    }
}
