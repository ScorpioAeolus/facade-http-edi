package com.facade.edi.starter.scanner;

import com.facade.edi.starter.annotation.EdiApi;
import com.facade.edi.starter.support.EdiServiceFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Set;

/**
 * 远程接口扫描器
 *
 * @author Typhoon
 * @date 2022-07-05 10:04 Tuesday
 */
public class EdiApiBeanScanner extends ClassPathBeanDefinitionScanner {

    private AdviceMode adviceMode;

//    public EdiApiBeanScanner(BeanDefinitionRegistry registry) {
//        super(registry, false);
//    }

    public EdiApiBeanScanner(BeanDefinitionRegistry registry,AdviceMode adviceMode) {
        super(registry, false);
        this.adviceMode = adviceMode;
        if(null == this.adviceMode) {
            this.adviceMode = AdviceMode.PROXY;
        }
    }

    public void registerFilters() {
        addIncludeFilter(new AnnotationTypeFilter(EdiApi.class));
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No edi api interface  was found in '" + Arrays.toString(basePackages)
                    + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    /**
     * 将扫描的接口注册成 EdiServiceBuilder,它是一个FactoryBean,具体注入使用时调用getObject返回的真实实例
     *
     * @param beanDefinitions
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();

            if (logger.isDebugEnabled()) {
                logger.debug("Creating edi api with name '" + holder.getBeanName() + "' and '"
                        + definition.getBeanClassName());
            }

            /**
             * 传入BeanDefinition的目标类定义
             */
            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClass());
            definition.getConstructorArgumentValues().addGenericArgumentValue(this.adviceMode);
            definition.setBeanClass(EdiServiceFactoryBean.class);
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
            }
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }

}
