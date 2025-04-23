package com.facade.edi.starter.config;

import com.facade.edi.starter.service.IInvokeHttpFacade;
import com.facade.edi.starter.service.impl.WebClientInvokeHttpFacade;
import com.facade.edi.starter.util.ILogInject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.channel.ChannelOption;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * webflux 的webclient客户端配置
 *
 * @author typhoon
 * @since 2025-04-22 20:03 Tuesday
 */
public class EdiWebfluxClientConfig implements ILogInject {


    @Bean
    @ConditionalOnMissingBean
    public ReactorClientHttpConnector httpClient(Environment environment) {
        // 创建独立的连接池配置
        int timeout = Integer.parseInt(environment.getProperty("edi.timeout", "6000"));

        ConnectionProvider connectionProvider = ConnectionProvider.builder("customPool")
                .maxConnections(500)                // 最大连接数
                .pendingAcquireMaxCount(1000)       // 等待队列最大长度
                .pendingAcquireTimeout(Duration.ofMillis(timeout)) // 获取连接超时
                .evictInBackground(Duration.ofMillis(timeout))    // 后台回收间隔
                .build();
        // 配置底层 Netty HttpClient
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofMillis(timeout))
                .compress(true)
                .keepAlive(true)                    // 开启长连接
                // 连接池参数
                .option(ChannelOption.SO_KEEPALIVE, true) // 启用 Keep-Alive
                .option(ChannelOption.MAX_MESSAGES_PER_READ, 100) // 每次读取最大消息数
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.TCP_NODELAY, true) // 关闭Nagle算法
                .option(ChannelOption.SO_KEEPALIVE, true) // 开启OS层KeepAlive
                .option(ChannelOption.SO_REUSEADDR, true); // 端口复用

        return new ReactorClientHttpConnector(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient(ReactorClientHttpConnector httpClient) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return WebClient.builder()
                .clientConnector(httpClient)  // 注入自定义的 HttpClient 适配器
                .codecs(config -> {
                    // 优化编解码器
                    config.defaultCodecs().maxInMemorySize(16 * 1024 * 1024); // 16MB
                    config.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
                    config.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
                })
                .build();
    }

    @Bean
    public IInvokeHttpFacade webClientInvokeHttpFacade(WebClient webClient) {
        return new WebClientInvokeHttpFacade(webClient);
    }

}
