package com.facade.edi.starter.scanner;

import com.facade.edi.starter.annotation.EdiApi;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * spi注解扫描器
 *
 * @author typhoon
 */
@Slf4j
public class EdiApiScanner {

    private ClassLoader classLoader;

    private BeanDefinitionRegistry registry;

    private AdviceMode adviceMode;

    public EdiApiScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public EdiApiScanner(BeanDefinitionRegistry registry, AdviceMode adviceMode) {
        this.registry = registry;
        this.adviceMode = adviceMode;
        if(null == this.adviceMode) {
            this.adviceMode = AdviceMode.PROXY;
        }
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
            log.warn("biz-spi-scan scan packages is empty.");
            return null;
        }
        log.info("biz-spi-scan scan packages: {}", Arrays.toString(scanPackages));
        Set<Class<?>> ediClasses = scanEdiInterfaces(scanPackages);
        if (CollectionUtils.isEmpty(ediClasses)) {
            logScannedSpiInterfacesResultEmpty(scanPackages);
            return null;
        }
        logScannedSpiInterfacesResult(ediClasses);
        Set<BeanDefinitionHolder> beanDefHolders = Sets.newHashSet();
        for (Class<?> ediClass : ediClasses) {
            beanDefHolders.add(BeanRegisterUtil.createSpiFactoryBeanBeanDefinitionHolder(ediClass,this.adviceMode));
        }
        return beanDefHolders;
    }

    private Set<Class<?>> scanEdiInterfaces(String... scanPackages) {
        AbstractClassCandidateScanner fastClassPathScanner = new FastClassPathScanner();
        //fastClassPathScanner.addClassTypeFilter(new ISpiProviderClassTypeFilter());
        fastClassPathScanner.addClassLoader(this.classLoader);
        return fastClassPathScanner.scan(EdiApi.class, scanPackages);
    }

    private void logScannedSpiInterfacesResult(Set<Class<?>> spiClasses) {
        for (Class<?> spiClass : spiClasses) {
            log.info("biz-spi-scan scanned annotated with @Edi: {}", spiClass);
        }
    }

    private void logScannedSpiInterfacesResultEmpty(String... scanPackages) {
        log.warn("biz-spi-scan scan packages {} not any interfaces which annotated with @Edi present.", Arrays.toString(scanPackages));
    }
}
