package com.facade.edi.starter.scanner;

/**
 * 类型过滤器
 *
 * @author typhoon
 */
public interface IClassTypeFilter {

    boolean match(Class<?> clazz);
}
