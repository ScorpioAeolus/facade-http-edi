package com.facade.edi.starter.config;

import com.facade.edi.starter.service.IInvokeHttpFacade;
import com.facade.edi.starter.service.impl.NettyInvokerHttpFacade;
import com.facade.edi.starter.service.netty.NettyHttpClient;
import com.facade.edi.starter.service.netty.NettyHttpClientHandler;
import com.facade.edi.starter.util.ILogInject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;

/**
 * 封装netty的http客户端配置
 *
 * @author typhoon
 * @since 2025-04-24 10:54 Thursday
 */
public class EdiNettyClientConfig implements ILogInject {


    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public EventLoopGroup eventLoopGroup() {
        return new NioEventLoopGroup(4); // 4个I/O线程
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Bootstrap bootstrap(EventLoopGroup group, Environment environment) {
        int timeout = Integer.parseInt(environment.getProperty("edi.timeout", "6000"));

        return new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new HttpClientCodec())          // HTTP编解码器
                                .addLast(new HttpObjectAggregator(1024 * 1024)) // 聚合HTTP消息
                                .addLast(new HttpContentDecompressor()) // 解压缩
                                .addLast(new NettyHttpClientHandler()); // 自定义处理器
                    }
                });
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(NettyHttpClient.class)
    public NettyHttpClient nettyHttpClient(Bootstrap bootstrap, EventLoopGroup group) {
        return new NettyHttpClient(bootstrap,group);
    }


    @Bean
    public IInvokeHttpFacade nettyInvokerHttpFacade(NettyHttpClient nettyHttpClient) {
        return new NettyInvokerHttpFacade(nettyHttpClient);
    }


}
