package com.facade.edi.starter.config;

import com.facade.edi.starter.service.IInvokeHttpFacade;
import com.facade.edi.starter.service.impl.HttpClientInvokeHttpFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * httpClient客户端配置
 *
 * @author typhoon
 * @date 2024-08-19 16:23 Monday
 */
@Slf4j
//@Configuration
public class HttpClientConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SSLConnectionSocketFactory sslSocketFactory() {
        try {
            //信任任何链接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            log.error("[HttpClientConfig::sslSocketFactory] error", e);
        }
        return null;
    }

    @Bean
    public CloseableHttpClient httpClient() {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("https", sslSocketFactory())
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .build();
        MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(200)
                .setMaxLineLength(2000)
                .build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(StandardCharsets.UTF_8)
                .setMessageConstraints(messageConstraints)
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connManager.setDefaultSocketConfig(socketConfig);
        connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setMaxTotal(200);
        connManager.setDefaultMaxPerRoute(100);

        return HttpClients.custom()
                .setConnectionManager(connManager)
                .build();
    }


    @Bean
    public IInvokeHttpFacade httpClientInvokeHttpFacade(CloseableHttpClient httpClient) {
        return new HttpClientInvokeHttpFacade(httpClient);
    }
}