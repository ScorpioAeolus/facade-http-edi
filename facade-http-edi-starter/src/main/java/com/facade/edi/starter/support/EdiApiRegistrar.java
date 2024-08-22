package com.facade.edi.starter.support;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.scanner.EdiApiScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 远程调用接口解析类
 *
 * @author Typhoon
 */
public class EdiApiRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(EnableEdiApiScan.class.getName()));
        assert attributes != null;
        String[] basePackages = attributes.getStringArray("scanBasePackages");
        if(basePackages.length == 0) {
            throw new RuntimeException("开启EDI需要指定扫描路径");
        }
        AdviceMode mode =  (AdviceMode) attributes.get("mode");
        EdiApiScanner scanner = new EdiApiScanner(registry,mode);
        scanner.scan(basePackages);
    }

}
