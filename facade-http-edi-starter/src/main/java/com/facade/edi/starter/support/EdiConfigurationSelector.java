package com.facade.edi.starter.support;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.config.EdiClientConfig;
import com.facade.edi.starter.config.EdiHttpClientConfig;
import com.facade.edi.starter.config.EdiOkHttpConfig;
import com.facade.edi.starter.config.EdiRestTemplateConfig;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 配置选择器
 *
 * @author typhoon
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
        } else {
            throw new UnsupportedOperationException("Unknown clientType: " + clientType);
        }
    }
}
