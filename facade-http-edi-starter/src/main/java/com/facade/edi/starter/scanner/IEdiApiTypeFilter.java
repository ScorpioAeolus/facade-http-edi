package com.facade.edi.starter.scanner;


import com.facade.edi.starter.annotation.EdiApi;

/**
 * spi类型过滤器
 *
 * @author typhoon
 */
public class IEdiApiTypeFilter implements IClassTypeFilter {

    @Override
    public boolean match(Class<?> clazz) {
        return clazz.isAnnotationPresent(EdiApi.class);
    }
}
