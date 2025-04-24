package com.facade.edi.starter.service.netty;

import com.facade.edi.starter.util.ILogInject;
import com.facade.edi.starter.util.MapUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class NettyHttpClient implements ILogInject {

    private final Bootstrap bootstrap;
    private final EventLoopGroup group;

    public NettyHttpClient(Bootstrap bootstrap, EventLoopGroup group) {
        this.bootstrap = bootstrap;
        this.group = group;
    }

    // 通用请求方法
    public FullHttpResponse execute(
            HttpMethod method,
            String url,
            Map<String, String> urlParams,
            String body,
            Map<String,String> form,
            Map<String, String> headers
    ) {
        try {
            URI uri = buildUriWithParams(url, urlParams);
            FullHttpRequest request = buildRequest(method, uri, body,form, headers);
            return sendRequest(uri.getHost(), uri.getPort(), request);
        } catch (Exception e) {
            throw new RuntimeException("HTTP Request Failed", e);
        }
    }

    // 构建 HTTP 请求对象
    private FullHttpRequest buildRequest(
            HttpMethod method,
            URI uri,
            String body,
            Map<String,String> form,
            Map<String, String> headers
    ) {
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                method,
                uri.getRawPath()
        );

        // 设置通用头
        headers = headers != null ? headers : new HashMap<>();
        headers.put(HttpHeaderNames.HOST.toString(), uri.getHost());
        headers.put(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.CLOSE.toString());
        headers.forEach(request.headers()::set);

        // 设置请求体和 Content-Type
        if(null != body) {
            // 文本/JSON 请求体
            request.content().writeBytes(((String) body).getBytes(StandardCharsets.UTF_8));
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }
        if(MapUtil.isNotEmpty(form)) {
            setFormDataBody(request,form);
        }

        return request;
    }

    private void setFormDataBody(FullHttpRequest request, Map<String, String> formParam)  {
        // 使用 Netty 的 FormUrlEncodedContent 编码表单数据
        StringBuilder formData = new StringBuilder();
        for (Map.Entry<String, String> entry : formParam.entrySet()) {
            if (formData.length() > 0) {
                formData.append("&");
            }
            try {
                formData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                log.error("NettyHttpClient.setFormDataBody occur error;form={}",formParam,e);
            }
        }

        request.headers().set(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        request.content().writeBytes(formData.toString().getBytes(StandardCharsets.UTF_8));
    }

    // 构建带参数的 URI
    private URI buildUriWithParams(String url, Map<String, String> params) throws URISyntaxException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if(MapUtil.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.queryParam(entry.getKey(),entry.getValue());
            }
        }

         URI uri = builder
                .build()
                .toUri();
        if (uri.getPort() == -1) {
            int defaultPort = uri.getScheme().equals("https") ? 443 : 80;
            uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), defaultPort, uri.getPath(), uri.getQuery(), uri.getFragment());
        }
        return uri;
    }


    private FullHttpResponse sendRequest(String host, int port, FullHttpRequest request) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            NettyHttpClientHandler handler = channel.pipeline()
                    .get(NettyHttpClientHandler.class);

            FullHttpResponse response = handler.sendRequest(channel, request);
            //return response.content().toString(CharsetUtil.UTF_8);
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request Interrupted", e);
        }
    }

    public void close() {
        group.shutdownGracefully();
    }
}
