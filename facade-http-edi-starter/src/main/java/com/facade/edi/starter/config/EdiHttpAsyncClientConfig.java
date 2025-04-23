package com.facade.edi.starter.config;

import com.facade.edi.starter.service.IInvokeHttpFacade;
import com.facade.edi.starter.service.impl.HttpAsyncClientInvokeHttpFacade;
import com.facade.edi.starter.util.ILogInject;
import org.apache.http.Consts;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;

import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * httpAsyncClient客户端配置
 *
 * @author typhoon
 */
public class EdiHttpAsyncClientConfig implements ILogInject {


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SSLContext sslContext() {
        //ssl 连接设置 无须证书也能访问 https
        //使用 loadTrustMaterial() 方法实现一个信任策略，信任所有证书
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (Exception e) {
            log.error("HttpAsyncClientConfig.sslContext init error",e);
        }
        return sslContext;
    }


    @Bean(initMethod = "start",destroyMethod = "close")
    @ConditionalOnMissingBean(CloseableHttpAsyncClient.class)
    public CloseableHttpAsyncClient httpAsyncClient(Environment environment) {

        NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory = new ManagedNHttpClientConnectionFactory(
                DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE, HeapByteBufferAllocator.INSTANCE);


        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", new SSLIOSessionStrategy(sslContext(), NoopHostnameVerifier.INSTANCE))
                .build();

        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setConnectTimeout(30000)
                .setSoTimeout(30000)
                .build();
        DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;


        ConnectingIOReactor ioReactor = null;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            log.error("httpAsyncClient init ioReactor error,", e);
        }

        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
                ioReactor, connFactory, sessionStrategyRegistry, dnsResolver);


        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .build();

        connManager.setDefaultConnectionConfig(connectionConfig);

        connManager.setMaxTotal(200);
        connManager.setDefaultMaxPerRoute(100);
        int timeout = Integer.parseInt(environment.getProperty("edi.timeout", "6000"));

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setExpectContinueEnabled(true)
                .build();

        // Create an HttpClientUtils with the given custom dependencies and configuration.
        return HttpAsyncClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

    }


    @Bean
    public IInvokeHttpFacade httpAsyncClientInvokeHttpFacade(CloseableHttpAsyncClient httpAsyncClient) {
        return new HttpAsyncClientInvokeHttpFacade(httpAsyncClient);
    }
}