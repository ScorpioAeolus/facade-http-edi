package com.facade.edi.starter.scanner;

/**
 * 类型过滤器
 *
 * @author typhoon
 * @date 2024-08-15 20:04 Thursday
 */
public interface IClassTypeFilter {

    boolean match(Class<?> clazz);
}
