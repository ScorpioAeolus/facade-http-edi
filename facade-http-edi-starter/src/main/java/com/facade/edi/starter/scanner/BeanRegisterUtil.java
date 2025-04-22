package com.facade.edi.starter.scanner;

import com.facade.edi.starter.support.EdiServiceFactoryBean;
import com.facade.edi.starter.util.ILogInject;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.beans.Introspector;
import java.util.Set;

/**
 * Bean注册工具类
 *
 * @author typhoon
 */
public class BeanRegisterUtil implements ILogInject {

    public static void registerBeanDefinitions(Set<BeanDefinitionHolder> beanDefHolders, BeanDefinitionRegistry registry) {
        if (CollectionUtils.isEmpty(beanDefHolders)) {
            return;
        }
        for (BeanDefinitionHolder beanDefHolder : beanDefHolders) {
            BeanDefinitionReaderUtils.registerBeanDefinition(beanDefHolder, registry);
            log.info("edi-api-bean registered. bean name: {}, bean class: {}, edi interface: {}.",
                    beanDefHolder.getBeanName(),
                    beanDefHolder.getBeanDefinition().getBeanClassName(),
                    beanDefHolder.getBeanDefinition().getConstructorArgumentValues().getGenericArgumentValues().get(0).getValue());
        }
    }

    public static BeanDefinitionHolder createEdiFactoryBeanBeanDefinitionHolder(Class<?> ediClass, AdviceMode adviceMode,boolean preheat) {
        GenericBeanDefinition beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(EdiServiceFactoryBean.class);
        beanDef.getConstructorArgumentValues().addGenericArgumentValue(ediClass);
        beanDef.getConstructorArgumentValues().addGenericArgumentValue(adviceMode);
        beanDef.getConstructorArgumentValues().addGenericArgumentValue(preheat);
        String beanName = generateEdiFactoryBeanName(ediClass);
        log.info("edi-api-bean created. bean name: {}, bean class: {}, edi interface: {}.",
                beanName, EdiServiceFactoryBean.class.getName(), ediClass.getName());
        return new BeanDefinitionHolder(beanDef, beanName);
    }

    private static String generateEdiFactoryBeanName(Class<?> ediClass) {

        return BeanRegisterUtil.generateDefaultBeanName(ediClass);
    }

    private static String generateDefaultBeanName(Class<?> ediClass) {
        final String shortClassName = ClassUtils.getShortName(ediClass);
        return Introspector.decapitalize(shortClassName);
    }

}
