package com.facade.edi.starter.support;


import com.facade.edi.starter.annotation.EdiApi;
import com.facade.edi.starter.converter.Converter;
import com.facade.edi.starter.converter.ConverterFactory;
import com.facade.edi.starter.service.IInvokeHttpFacade;
import com.facade.edi.starter.util.StringUtil;
import lombok.Getter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EdiApiProxyFactory {
    private final Map<Method, HttpServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();
    @Resource
    private ConverterFactory converterFactory;

    @Resource
    private Environment environment;

    @Resource
    @Getter
    IInvokeHttpFacade iInvokeHttpFacade;

    public String getHost(Method method) {
        EdiApi ediApi = method.getDeclaringClass().getAnnotation(EdiApi.class);
        if(StringUtil.isBlank(ediApi.hostKey())) {
            return null;
        }
        return this.environment.getProperty(ediApi.hostKey());
    }

    public Converter<Object, String> stringConverter() {
        return converterFactory.stringConverter();
    }


    public Converter<?, String> requestBodyConverter(Type type) {
        return converterFactory.requestBodyConverter(type);
    }

    public Converter<String, ?> responseConverter(Type type) {
        return converterFactory.responseBodyConverter(type);
    }

    public <T> T newInstance(final Class<T> service) {
        validateServiceInterface(service);
        return (T)
                Proxy.newProxyInstance(
                        service.getClassLoader(),
                        new Class<?>[]{service},
                        new InvocationHandler() {
                            private final Object[] emptyArgs = new Object[0];

                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args)
                                    throws Throwable {
                                if (method.getDeclaringClass() == Object.class || method.isDefault()) {
                                    return method.invoke(this, args);
                                }
                                args = args != null ? args : emptyArgs;
                                return loadServiceMethod(EdiApiProxyFactory.this, method).invoke(args);
                            }
                        });
    }

    /**
     * 远程API调用必须定义成接口
     * 加载接口方法到本地缓存
     *
     * @param service
     */
    private void validateServiceInterface(Class<?> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }

        Deque<Class<?>> check = new ArrayDeque<>(1);
        check.add(service);
        while (!check.isEmpty()) {
            Class<?> candidate = check.removeFirst();
            if (candidate.getTypeParameters().length != 0) {
                StringBuilder message =
                        new StringBuilder("Type parameters are unsupported on ").append(candidate.getName());
                if (candidate != service) {
                    message.append(" which is an interface of ").append(service.getName());
                }
                throw new IllegalArgumentException(message.toString());
            }
            Collections.addAll(check, candidate.getInterfaces());
        }

        for (Method method : service.getDeclaredMethods()) {
            if (!method.isDefault() && !Modifier.isStatic(method.getModifiers())) {
                loadServiceMethod(this, method);
            }
        }
    }

    HttpServiceMethod loadServiceMethod(EdiApiProxyFactory ediApiProxyFactory, Method method) {
        HttpServiceMethod result = serviceMethodCache.get(method);
        if (result != null) {
            return result;
        }

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = HttpServiceMethod.parseAnnotations(ediApiProxyFactory, method);
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public <T> T newCglibInstance(Class<T> targetClass) {
        validateServiceInterface(targetClass);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new MethodInterceptor() {
            private final Object[] emptyArgs = new Object[0];
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                if (method.getDeclaringClass() == Object.class || method.isDefault()) {
                    return method.invoke(this, args);
                }
                args = args != null ? args : emptyArgs;
                return loadServiceMethod(EdiApiProxyFactory.this, method).invoke(args);
            }
        });
        return targetClass.cast(enhancer.create());
    }
}
