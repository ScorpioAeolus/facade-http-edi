package com.facade.edi.samples.demo.proxy;

import com.facade.edi.starter.annotation.EdiApi;
import com.facade.edi.starter.annotation.api.GET;
import com.facade.edi.starter.annotation.param.Header;
import com.facade.edi.starter.annotation.param.Host;
import com.facade.edi.starter.annotation.param.Query;

@EdiApi
public interface RiskApi {


    @GET("/v1/risk_score")
    AddressRiskResult queryRisk(@Query("coin") String coin
            , @Query("address") String address
            , @Header("api_key") String apiKey
            , @Header("Content-Type") String contentType
            , @Host("host") String host);

}
