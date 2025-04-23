package com.facade.edi.starter.support;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.config.EdiClientConfig;
import com.facade.edi.starter.config.EdiHttpAsyncClientConfig;
import com.facade.edi.starter.config.EdiHttpClientConfig;
import com.facade.edi.starter.config.EdiNativeClientConfig;
import com.facade.edi.starter.config.EdiOkHttpConfig;
import com.facade.edi.starter.config.EdiRestTemplateConfig;
import com.facade.edi.starter.config.EdiWebfluxClientConfig;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置选择器
 *
 * @author typhoon
 */
public class EdiConfigurationSelector implements ImportSelector, EnvironmentAware {

    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {
        // 强制转换为可配置的 Environment
        this.environment = (ConfigurableEnvironment) environment;
    }
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableEdiApiScan.class.getName(), false));
        EnableEdiApiScan.ClientType clientType = attributes.getEnum("clientType");
        long timeout = (long)attributes.getOrDefault("timeout",6000L);

        Map<String, Object> dynamicProps = new HashMap<>();
        dynamicProps.put("edi.timeout", timeout); // 自定义属性键
        this.environment.getPropertySources()
                .addFirst(new MapPropertySource("ediTimeoutSource", dynamicProps));

        if(clientType == EnableEdiApiScan.ClientType.REST_TEMPLATE) {
            return new String[] {
                    //AutoProxyRegistrar.class.getName(),
                    EdiClientConfig.class.getName(),
                    EdiRestTemplateConfig.class.getName()
            };
        } else if(clientType == EnableEdiApiScan.ClientType.OK_HTTP) {
            return new String[] {
                    //AutoProxyRegistrar.class.getName(),
                    EdiClientConfig.class.getName(),
                    EdiOkHttpConfig.class.getName()
            };
        } else if(clientType == EnableEdiApiScan.ClientType.HTTP_CLIENT) {
            return new String[] {
                    //AutoProxyRegistrar.class.getName(),
                    EdiClientConfig.class.getName(),
                    EdiHttpClientConfig.class.getName()
            };
        } else if(clientType == EnableEdiApiScan.ClientType.NATIVE) {
            return new String[] {
                    EdiClientConfig.class.getName(),
                    EdiNativeClientConfig.class.getName()
            };
        } else if(clientType == EnableEdiApiScan.ClientType.HTTP_ASYNC_CLIENT) {
            return new String[]{
                    EdiClientConfig.class.getName(),
                    EdiHttpAsyncClientConfig.class.getName()
            };
        } else if(clientType == EnableEdiApiScan.ClientType.WEB_CLIENT) {
            return new String[]{
                    EdiClientConfig.class.getName(),
                    EdiWebfluxClientConfig.class.getName()
            };
        }  else {
            throw new UnsupportedOperationException("Unknown clientType: " + clientType);
        }
    }
}
