package com.facade.edi.starter.support;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.config.EdiClientConfig;
import com.facade.edi.starter.config.EdiHttpClientConfig;
import com.facade.edi.starter.config.EdiNativeClientConfig;
import com.facade.edi.starter.config.EdiOkHttpConfig;
import com.facade.edi.starter.config.EdiRestTemplateConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置选择器
 *
 * @author typhoon
 */
public class EdiConfigurationSelector implements ImportSelector, ApplicationContextAware {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableEdiApiScan.class.getName(), false));
        EnableEdiApiScan.ClientType clientType = attributes.getEnum("clientType");
        long timeout = (long)attributes.getOrDefault("timeout",6000L);
        if (applicationContext != null) {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MutablePropertySources sources = environment.getPropertySources();

            Map<String, Object> dynamicProps = new HashMap<>();
            dynamicProps.put("edi.timeout", timeout); // 自定义属性键
            sources.addFirst(new MapPropertySource("dynamicTimeoutProperties", dynamicProps));
        }
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
        } else {
            throw new UnsupportedOperationException("Unknown clientType: " + clientType);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }
    }
}
