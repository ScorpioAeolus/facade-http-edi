package com.facade.edi.starter.support;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.config.EdiClientConfig;
import com.facade.edi.starter.config.HttpClientConfig;
import com.facade.edi.starter.config.OkHttpConfig;
import com.facade.edi.starter.config.RestTemplateConfig;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 配置选择器
 *
 * @author typhoon
 * @date 2024-08-19 21:50 Monday
 */
public class EdiConfigurationSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableEdiApiScan.class.getName(), false));
        EnableEdiApiScan.ClientType clientType = attributes.getEnum("clientType");
        if(clientType == EnableEdiApiScan.ClientType.REST_TEMPLATE) {
            return new String[] {
                    //AutoProxyRegistrar.class.getName(),
                    EdiClientConfig.class.getName(),
                    RestTemplateConfig.class.getName()
            };
        } else if(clientType == EnableEdiApiScan.ClientType.OK_HTTP) {
            return new String[] {
                    //AutoProxyRegistrar.class.getName(),
                    EdiClientConfig.class.getName(),
                    OkHttpConfig.class.getName()
            };
        } else if(clientType == EnableEdiApiScan.ClientType.HTTP_CLIENT) {
            return new String[] {
                    //AutoProxyRegistrar.class.getName(),
                    EdiClientConfig.class.getName(),
                    HttpClientConfig.class.getName()
            };
        } else {
            throw new UnsupportedOperationException("Unknown clientType: " + clientType);
        }
    }
}
