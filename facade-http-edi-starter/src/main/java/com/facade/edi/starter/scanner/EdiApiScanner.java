package com.facade.edi.starter.scanner;

import com.facade.edi.starter.annotation.EdiApi;
import com.facade.edi.starter.util.ILogInject;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * epi注解扫描器
 *
 * @author typhoon
 */
public class EdiApiScanner implements ILogInject {

    private ClassLoader classLoader;

    private BeanDefinitionRegistry registry;

    private AdviceMode adviceMode;

    private boolean preheat;

    public EdiApiScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public EdiApiScanner(BeanDefinitionRegistry registry, AdviceMode adviceMode,boolean preheat) {
        this.registry = registry;
        this.adviceMode = adviceMode;
        if(null == this.adviceMode) {
            this.adviceMode = AdviceMode.PROXY;
        }
        this.preheat = preheat;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void scan(String... scanPackages) {

        Set<BeanDefinitionHolder> beanDefinitions = doScan(scanPackages);

        BeanRegisterUtil.registerBeanDefinitions(beanDefinitions, registry);

        this.processBeanDefinitions(beanDefinitions);
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefHolders) {
        // 暂时留空，后续可能会有用处
    }

    private Set<BeanDefinitionHolder> doScan(String... scanPackages) {
        if (scanPackages == null || scanPackages.length <= 0) {
            log.warn("edi-scan scan packages is empty.");
            return null;
        }
        log.info("edi-scan scan packages: {}", Arrays.toString(scanPackages));
        Set<Class<?>> ediClasses = scanEdiInterfaces(scanPackages);
        if (CollectionUtils.isEmpty(ediClasses)) {
            logScannedEdiInterfacesResultEmpty(scanPackages);
            return null;
        }
        logScannedEdiInterfacesResult(ediClasses);
        Set<BeanDefinitionHolder> beanDefHolders = Sets.newHashSet();
        for (Class<?> ediClass : ediClasses) {
            beanDefHolders.add(BeanRegisterUtil.createEdiFactoryBeanBeanDefinitionHolder(ediClass,this.adviceMode,this.preheat));
        }
        return beanDefHolders;
    }

    private Set<Class<?>> scanEdiInterfaces(String... scanPackages) {
        AbstractClassCandidateScanner fastClassPathScanner = new FastClassPathScanner();
        fastClassPathScanner.addClassLoader(this.classLoader);
        return fastClassPathScanner.scan(EdiApi.class, scanPackages);
    }

    private void logScannedEdiInterfacesResult(Set<Class<?>> ediClasses) {
        for (Class<?> ediClass : ediClasses) {
            log.info("edi-scan scanned annotated with @Edi: {}", ediClass);
        }
    }

    private void logScannedEdiInterfacesResultEmpty(String... scanPackages) {
        log.warn("edi-scan scan packages {} not any interfaces which annotated with @Edi present.", Arrays.toString(scanPackages));
    }
}
