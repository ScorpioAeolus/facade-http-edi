package com.facade.edi.starter.config;

import com.facade.edi.starter.service.IInvokeHttpFacade;
import com.facade.edi.starter.service.impl.RestTemplateInvokeHttpFacade;
import com.facade.edi.starter.util.ILogInject;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * restTemplate客户端配置
 *
 * @author typhoon
 */
public class EdiRestTemplateConfig implements ILogInject {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(Environment environment) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory(environment));
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(StandardCharsets.UTF_8);
                break;
            }
        }
        return restTemplate;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(Environment environment) {
        try {
            int timeout = Integer.parseInt(environment.getProperty("edi.timeout", "6000"));
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }).build();
            httpClientBuilder.setSSLContext(sslContext);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    hostnameVerifier);
            // 注册http和https请求
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();
            // 开始设置连接池
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            // 最大连接数2700
            poolingHttpClientConnectionManager.setMaxTotal(3000);
            // 同路由并发数100
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(1000);
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);

            this.preheatConnections(poolingHttpClientConnectionManager);

           /* // 重试次数
            httpClientBuilder.setRetryHandler(defaultHttpRequestRetryHandler);*/
            HttpClient httpClient = httpClientBuilder.build();
            // httpClient连接配置
            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                    httpClient);
            // 连接超时
            clientHttpRequestFactory.setConnectTimeout(timeout);
            // 数据读取超时时间
            clientHttpRequestFactory.setReadTimeout(timeout);
            // 连接不够用的等待时间
            clientHttpRequestFactory.setConnectionRequestTimeout(60 * 1000);
            return clientHttpRequestFactory;
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("初始化HTTP连接池出错", e);
        }
        return null;
    }

    private void preheatConnections(PoolingHttpClientConnectionManager connectionManager) {
        log.info("EdiRestTemplateConfig.preheatConnections connection pool preheat;default preheat count={}",10);
        // 主动初始化连接
        for (int i = 0; i < 10; i++) {
            try {
                // 打开一个到目标服务器的连接
                connectionManager.requestConnection(null, null).get(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("EdiRestTemplateConfig.preheatConnections occur error",e);
            }
        }
    }

    @Bean
    public IInvokeHttpFacade restTemplateInvokeHttpFacade(RestTemplate restTemplate) {
        return new RestTemplateInvokeHttpFacade(restTemplate);
    }

}
