package com.facade.edi.starter.config;

import com.facade.edi.starter.service.IInvokeHttpFacade;
import com.facade.edi.starter.service.impl.WebClientInvokeHttpFacade;
import com.facade.edi.starter.util.ILogInject;
import io.netty.channel.ChannelOption;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

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
    public ReactorClientHttpConnector httpClient() {
        // 配置底层 Netty HttpClient
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(10))
                .compress(true);

        return new ReactorClientHttpConnector(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient(ReactorClientHttpConnector httpClient) {
        return WebClient.builder()
                .clientConnector(httpClient)  // 注入自定义的 HttpClient 适配器
                .build();
    }

    @Bean
    public IInvokeHttpFacade webClientInvokeHttpFacade(WebClient webClient) {
        return new WebClientInvokeHttpFacade(webClient);
    }

}
