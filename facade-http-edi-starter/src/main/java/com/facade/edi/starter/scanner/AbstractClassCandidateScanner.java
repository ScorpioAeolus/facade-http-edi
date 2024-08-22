package com.facade.edi.starter.scanner;

import com.facade.edi.starter.util.CollectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.List;
import java.util.Set;

/**
 * 抽象目标类扫描器
 *
 * @author typhoon
 */
@Getter
public abstract class AbstractClassCandidateScanner implements IClassCandidateScanner {

    private List<ClassLoader> classLoaders;

    private List<IClassTypeFilter> classTypeFilters;

    public void addClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            return;
        }
        if (classLoaders == null) {
            classLoaders = Lists.newArrayList();
        }
        classLoaders.add(classLoader);
    }

    public void addClassTypeFilter(IClassTypeFilter classTypeFilter) {
        if (classTypeFilter == null) {
            return;
        }
        if (classTypeFilters == null) {
            classTypeFilters = Lists.newArrayList();
        }
        classTypeFilters.add(classTypeFilter);
    }

    @Override
    public Set<Class<?>> scan(Class<?> annotation, String... scanPackages) {
        List<Class<?>> scannedClasses = doScan(annotation, scanPackages);
        if (CollectionUtil.isEmpty(scannedClasses)) {
            return null;
        }
        Set<Class<?>> matchedClasses = Sets.newLinkedHashSet();
        for (Class<?> scannedClass : scannedClasses) {
            if (filter(scannedClass)) {
                matchedClasses.add(scannedClass);
            }
        }
        return matchedClasses;
    }

    private boolean filter(Class<?> clazz) {
        if (CollectionUtil.isEmpty(classTypeFilters)) {
            return true;
        }
        return classTypeFilters.stream().allMatch(filter -> filter.match(clazz));
    }

    protected abstract List<Class<?>> doScan(Class<?> annotation, String... scanPackages);
}
