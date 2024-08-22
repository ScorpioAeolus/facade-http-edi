package com.facade.edi.starter.util;

import java.util.Collection;

/**
 * 参数校验工具类
 *
 * @author typhoon
 * @since V1.0.0
 */
public class ParameterChecker {

    public static final void notNull(Object obj,String parameterName) {
        if(null == obj) {
            throw new IllegalArgumentException(parameterName + " cannot be null ");
        }
    }

    public static final void notBlank(String str,String parameterName) {
        if(null == str || str.trim().length() <= 0) {
            throw new IllegalArgumentException(parameterName + " cannot be blank ");
        }
    }

    public static final <T extends Collection> void notEmpty(T t,String parameterName) {
        if(null == t || t.isEmpty()) {
            throw new IllegalArgumentException(parameterName + " cannot be empty ");
        }
    }

    public static final void multiNotNull(String msg,Object...objects) {
        for (Object obj : objects) {
            if(null == obj) {
                throw new IllegalArgumentException(" param is null ");
            }
        }
    }

    public static final <T extends Number> void assertPositive(T t) {
        if(t.intValue() <= 0) {
            throw new IllegalArgumentException("parameter must be positive");
        }
    }

    public static final <T extends Number> void assertPositive(T t,String parameterName) {
        if(t.intValue() <= 0) {
            throw new IllegalArgumentException(parameterName + " must be positive");
        }
    }


    public static final void assertTrue(boolean state,String msg) {
        if(!state) {
            throw new IllegalArgumentException(msg);
        }
    }

}
