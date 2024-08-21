package com.facade.edi.samples.demo.proxy;

import com.alibaba.fastjson.JSONObject;
import com.facade.edi.starter.annotation.EdiApi;
import com.facade.edi.starter.annotation.api.GET;
import com.facade.edi.starter.annotation.param.Header;
import com.facade.edi.starter.annotation.param.Host;
import com.facade.edi.starter.annotation.param.Query;
import com.facade.edi.starter.annotation.param.ResponseConvert;
import com.facade.edi.starter.converter.ClientResponseConverter;

import java.io.IOException;

@EdiApi(hostKey = "currencyapi.host")
public interface RateApi {


    @GET("/v3/latest")
    ExchangeRate getRate(@Query("base_currency") String baseCurrency
            , @Query("currencies") String currencies
            , @Header("Content-Type") String contentType
            , @Header("apiKey") String apiKey
            , @Host("host") String host);

    @GET("/v3/latest")
    ExchangeRate getRateV2(@Query("base_currency") String baseCurrency
            , @Query("currencies") String currencies
            , @Header("Content-Type") String contentType
            , @Header("apiKey") String apiKey, @ResponseConvert ClientResponseConverter<ExchangeRate> converter);

}
