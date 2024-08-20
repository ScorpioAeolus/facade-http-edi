package com.facade.edi.starter.scanner;

import java.util.Set;

/**
 * 类扫描接口定义
 *
 * @author typhoon
 * @date 2024-08-15 20:03 Thursday
 *
 */
public interface IClassCandidateScanner {

    Set<Class<?>> scan(Class<?> annotation, String... scanPackages);
}
