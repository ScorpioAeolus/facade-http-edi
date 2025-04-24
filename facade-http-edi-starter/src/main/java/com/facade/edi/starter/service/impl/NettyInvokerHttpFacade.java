package com.facade.edi.starter.service.impl;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.service.AbstractInvokeHttpFacade;
import com.facade.edi.starter.service.netty.NettyHttpClient;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;

/**
 * 基于netty封装的http客户端
 *
 * @author typhoon
 * @since 2025-04-24 11:03 Thursday
 */
public class NettyInvokerHttpFacade extends AbstractInvokeHttpFacade {

    private NettyHttpClient nettyHttpClient;

    public NettyInvokerHttpFacade(NettyHttpClient nettyHttpClient) {
        super();
        this.nettyHttpClient = nettyHttpClient;
    }

    @Override
    public HttpApiResponse invoke(HttpApiRequest request) {
        log.info("NettyInvokerHttpFacade.invoke request={}",request);
        HttpApiResponse resp = new HttpApiResponse();


        try {
            FullHttpResponse response = this.nettyHttpClient.execute(HttpMethod.valueOf(request.getHttpMethod())
                    , request.getUrl()
                    , request.getParams()
                    , request.getBody()
                    , request.getFields()
                    ,request.getHeaders());
            resp.setHttpCode(response.status().code());
            resp.setResponse(response.content().toString(CharsetUtil.UTF_8));
        } catch (Exception e) {
            log.error("NettyInvokerHttpFacade.invokeResponse http invoke error;url={}",request.getUrl(), e);
            resp.setHttpCode(500);
            resp.setHttpErrorMsg(e.getMessage());
        }

        return resp;
    }

    @Override
    public void preheat(String host) {
        log.info("NettyInvokerHttpFacade.preheat trigger preheat;host={}",host);

    }

    @Override
    public EnableEdiApiScan.ClientType getClientType() {
        return EnableEdiApiScan.ClientType.NETTY;
    }
}
