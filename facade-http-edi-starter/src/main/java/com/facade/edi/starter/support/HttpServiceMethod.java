package com.facade.edi.starter.support;

import com.facade.edi.starter.enums.HttpState;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.constants.EntityError;
import com.facade.edi.starter.exception.EdiException;
import com.facade.edi.starter.converter.Converter;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.service.IInvokeHttpFacade;
import com.facade.edi.starter.util.EdiUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;


/**
 * 将远程API接口调用封装成http调用
 *
 * @author Typhoon
 */
@Slf4j
public class HttpServiceMethod {
    private final RequestFactory requestFactory;
    private final Converter<String, ?> responseConverter;
    private final IInvokeHttpFacade iInvokeHttpFacade;

    private final String host;


    //private Map<String, AtomicInteger> apiInvokeMap = new ConcurrentHashMap<>();

    public static HttpServiceMethod parseAnnotations(EdiApiProxyFactory ediApiProxyFactory, Method method) {
        RequestFactory requestFactory = RequestFactory.parseAnnotations(method, ediApiProxyFactory);

        Type returnType = method.getGenericReturnType();
        if (EdiUtil.hasUnresolvableType(returnType)) {
            throw EdiUtil.methodError(method, "Method return type must not include a type variable or wildcard: %s", returnType);
        }
        if (returnType == void.class) {
            throw EdiUtil.methodError(method, "Service methods cannot return void.");
        }
        Converter<String, ?> responseConverter = ediApiProxyFactory.responseConverter(method.getGenericReturnType());
        IInvokeHttpFacade iInvokeHttpFacade = ediApiProxyFactory.getIInvokeHttpFacade();
        String host = ediApiProxyFactory.getHost(method);
        return new HttpServiceMethod(requestFactory,host, responseConverter, iInvokeHttpFacade);
    }


    HttpServiceMethod(RequestFactory requestFactory,String host, Converter<String, ?> responseConverter, IInvokeHttpFacade iInvokeHttpFacade) {
        this.requestFactory = requestFactory;
        this.host = host;
        this.responseConverter = responseConverter;
        this.iInvokeHttpFacade = iInvokeHttpFacade;
    }

    public Object invoke(Object[] args) {
        Method method = requestFactory.getMethod();
        Class<?> returnType = method.getReturnType();
        boolean needResponseHead=ResponseHeader.class.isAssignableFrom(returnType);

        HttpApiRequest request;
        try {
            request = requestFactory.create(args,this.host);
        } catch (IOException e) {
            log.error("HttpServiceMethod.invoke 入参转换异常",e);
            throw new EdiException(e, EntityError.ILLEGAL_ARGUMENT.getCode(), "入参转换异常");
        }
        request.setNeedResponseHead(needResponseHead);
        HttpApiResponse response;
        response = this.iInvokeHttpFacade.invoke(request);

        if (!HttpState.isSuccess(response.getHttpCode())) {
            log.error("HttpServiceMethod.invoke 远程服务商http接口错误,code={},msg={}",response.getHttpCode(), response.getHttpErrorMsg());
            throw new EdiException(EntityError.API_HTTP_ERROR.getCode(), response.getHttpErrorMsg());
        }
        try {
            //如果用户传了自定义转换器,优先使用传入的;如果没有传入则使用默认的
            Object returnObj = null != request.getResponseConverter() ? request.getResponseConverter().convert(response.getResponse()) : responseConverter.convert(response.getResponse());
            if(needResponseHead){
                ((ResponseHeader)returnObj).setResponseHeader(response.getHeader());
            }
            return returnObj;
        } catch (Exception e) {
            log.error("HttpServiceMethod.invoke 返回值转换异常 response:{}",response.getResponse());
            throw new EdiException(e, EntityError.API_HTTP_ERROR.getCode(), "返回值转换异常");
        }
    }


}
