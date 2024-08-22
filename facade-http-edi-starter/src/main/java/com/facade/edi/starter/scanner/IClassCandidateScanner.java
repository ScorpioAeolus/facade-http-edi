package com.facade.edi.starter.scanner;

import java.util.Set;

/**
 * 类扫描接口定义
 *
 * @author typhoon
 *
 */
public interface IClassCandidateScanner {

    Set<Class<?>> scan(Class<?> annotation, String... scanPackages);
}
