package com.facade.edi.starter.annotation.param;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 请求头注解
 *
 * @author typhoon
 *
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Header {

  String value();

}
