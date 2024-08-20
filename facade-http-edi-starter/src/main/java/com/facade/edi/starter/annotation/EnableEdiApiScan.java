package com.facade.edi.starter.annotation;


import com.facade.edi.starter.support.EdiApiRegistrar;
import com.facade.edi.starter.support.EdiConfigurationSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 远程API接口扫描注解
 *
 * @author Typhoon
 * @date 2022-07-05 10:03 Tuesday
 */

@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Import({EdiApiRegistrar.class, EdiConfigurationSelector.class})
public @interface EnableEdiApiScan {

    /**
     * 接口扫描路径
     * @return
     */
    String[] scanBasePackages();

    /**
     * 请求客户端类型
     *
     * @return
     */
    ClientType clientType() default ClientType.REST_TEMPLATE;

    /**
     * 生成代理的模式
     *
     * @return
     */
    AdviceMode mode() default AdviceMode.PROXY;


    enum ClientType {

        /**
         * spring restTemplate
         */
        REST_TEMPLATE,
        /**
         * apache httpClient
         */
        HTTP_CLIENT,

        /**
         * okHttp
         */
        OK_HTTP,

        ;
    }
}
