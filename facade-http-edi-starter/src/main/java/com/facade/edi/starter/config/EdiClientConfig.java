package com.facade.edi.starter.config;

import com.facade.edi.starter.converter.ConverterFactory;
import com.facade.edi.starter.support.EdiApiProxyFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * edi客户端配置类
 *
 * @author typhoon
 */
@Configuration
public class EdiClientConfig {


    @Bean
    @ConditionalOnMissingBean(ConverterFactory.class)
    public ConverterFactory converterFactory() {
        return new ConverterFactory();
    }


    @Bean
    @ConditionalOnMissingBean(EdiApiProxyFactory.class)
    public EdiApiProxyFactory ediApiProxyFactory() {
       return new EdiApiProxyFactory();
    }

}
