package com.facade.edi.samples.demo.proxy;

import com.facade.edi.starter.annotation.EdiApi;
import com.facade.edi.starter.annotation.api.GET;
import com.facade.edi.starter.annotation.param.Header;
import com.facade.edi.starter.annotation.param.Host;
import com.facade.edi.starter.annotation.param.Query;
import com.facade.edi.starter.response.BaseResult;

@EdiApi
public interface RateApi {


    @GET("/v3/latest")
    BaseResult<ExchangeRate> getRate(@Query("base_currency") String baseCurrency
            , @Query("currencies") String currencies
            , @Header("Content-Type") String contentType
            , @Header("apiKey") String apiKey
            , @Host("host") String host);

}
