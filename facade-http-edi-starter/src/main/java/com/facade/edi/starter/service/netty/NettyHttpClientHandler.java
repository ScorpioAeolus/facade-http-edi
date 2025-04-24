package com.facade.edi.starter.service.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.TimeUnit;

public class NettyHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final Promise<FullHttpResponse> promise = new DefaultPromise<>(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpResponse fullHttpResponse) throws Exception {
        promise.setSuccess(fullHttpResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        promise.setFailure(cause);
        ctx.close();
    }

    public FullHttpResponse sendRequest(Channel channel, FullHttpRequest request) {
        channel.writeAndFlush(request);
        try {
            return promise.get(30, TimeUnit.SECONDS); // 设置全局超时
        } catch (Exception e) {
            throw new RuntimeException("HTTP Request Timeout", e);
        }
    }
}
