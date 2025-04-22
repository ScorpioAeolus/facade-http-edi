package com.facade.edi.starter.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author typhoon
 * @since 2025-03-08 10:56 Tuesday
 **/
public interface ILogInject {

    Logger log = LoggerFactory.getLogger(ILogInject.class);

}
