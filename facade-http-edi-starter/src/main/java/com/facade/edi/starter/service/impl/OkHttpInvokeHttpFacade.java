package com.facade.edi.starter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.service.AbstractInvokeHttpFacade;
import com.facade.edi.starter.util.MapUtil;
import kotlin.Pair;
import kotlin.text.Charsets;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpMethod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * OkHttp调用实现
 *
 * @author typhoon
 */
@Slf4j
public class OkHttpInvokeHttpFacade extends AbstractInvokeHttpFacade {

    private OkHttpClient okHttpClient;


    public OkHttpInvokeHttpFacade(OkHttpClient okHttpClient) {
        super();
        this.okHttpClient = okHttpClient;
    }

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Override
    public HttpApiResponse invoke(HttpApiRequest request) {
        log.info("OkHttpInvokeHttpFacade.invoke request={}",request);
        HttpApiResponse resp = new HttpApiResponse();
        Headers requestHeaders = processRequestHeader(request);
        RequestBody body = processRequestBody(request);
        HttpUrl url = this.buildUrl(request);
        Request req = new Request.Builder()
                .url(url)
                .method(request.getHttpMethod(), body)
                .headers(requestHeaders)
                .build();

        Response response = null;
        try {
            log.info("OkHttpInvokeHttpFacade.invoke url={}", request.getUrl());
            response = this.okHttpClient.newCall(req).execute();
            resp.setHttpCode(response.code());
            resp.setResponse(resolver(response.body()));
            resp.setHttpErrorMsg(response.message());
            log.info("OkHttpInvokeHttpFacade.invokeResponse result={}", JSONObject.toJSON(resp));
            if (request.isNeedResponseHead()) {
                Iterator<Pair<String, String>> iterator = response.headers().iterator();
                Map<String, String> responseHeaders = new HashMap<>(8);
                if (iterator.hasNext()) {
                    kotlin.Pair<String, String> next = iterator.next();
                    responseHeaders.put(next.component1(), next.component2());
                }
                resp.setHeader(responseHeaders);
            }
        } catch (Exception e) {
            log.error("OkHttpInvokeHttpFacade.invokeResponse http invoke error;url={}",request.getUrl(), e);
            resp.setHttpCode(500);
            resp.setHttpErrorMsg(e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return resp;
    }

    private Headers processRequestHeader(HttpApiRequest request) {
        Headers.Builder headerBuilder = new Headers.Builder();
        Map<String, String> headers = request.getHeaders();
        if (MapUtil.isNotEmpty(headers)) {
            for (String header : headers.keySet()) {
                headerBuilder.add(header, headers.get(header));
            }
        }
        return headerBuilder.build();
    }

    private HttpUrl buildUrl(HttpApiRequest request) {
        String url = request.getUrl();
        Map<String, String> params = request.getParams();
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(url), "invalid url: " + url);

        HttpUrl.Builder builder = httpUrl.newBuilder();

        if (MapUtil.isNotEmpty(params)) {
            addQueryParameter(builder,params);
        }

        return builder.build();
    }

    protected static void addQueryParameter(HttpUrl.Builder builder, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
    }

    private RequestBody processRequestBody(HttpApiRequest request) {
        if (!HttpMethod.requiresRequestBody(request.getHttpMethod())) {
            return null;
        }
        if (null != request.getBody() && !request.getBody().isEmpty()) {
            return RequestBody.create(request.getBody(), JSON_TYPE);

        } else if (MapUtil.isNotEmpty(request.getFields())) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : request.getFields().entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            return builder.build();
        } else {
            return RequestBody.create("", JSON_TYPE);
        }
    }

    private static String resolver(ResponseBody responseBody) {
        InputStream is = null;
        String result = null;
        try {
            is = responseBody.byteStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8.name()));
            String body = null;
            StringBuilder sb = new StringBuilder();
            while ((body = br.readLine()) != null) {
                sb.append(body);
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @Override
    public EnableEdiApiScan.ClientType getClientType() {
        return EnableEdiApiScan.ClientType.OK_HTTP;
    }
}
