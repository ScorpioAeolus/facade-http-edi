package com.facade.edi.starter.scanner;

import com.google.common.collect.Lists;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.List;

/**
 * 类扫描器
 *
 * @author typhoon
 * @date 2024-08-15 20:03 Thursday
 */
public class FastClassPathScanner extends AbstractClassCandidateScanner {

    @Override
    protected List<Class<?>> doScan(Class<?> annotation, String... scanPackages) {
        List<Class<?>> matchClasses = Lists.newArrayList();

        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(scanPackages).scan()) {
            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(annotation.getName());
            for (ClassInfo classInfo : classInfoList) {
                matchClasses.add(classInfo.loadClass());
            }
        }

        return matchClasses;
    }
}
