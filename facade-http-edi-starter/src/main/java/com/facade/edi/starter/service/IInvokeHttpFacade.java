package com.facade.edi.starter.service;


import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.util.ParameterChecker;

/**
 * http调用接口门面
 *
 * @author Typhoon
 * @date 2022-07-05 16:13 Tuesday
 */
public interface IInvokeHttpFacade {


    HttpApiResponse invoke(HttpApiRequest request);

    default void checkParam(HttpApiRequest request) {
        ParameterChecker.notNull(request,"request");
    }

    EnableEdiApiScan.ClientType getClientType();
}
