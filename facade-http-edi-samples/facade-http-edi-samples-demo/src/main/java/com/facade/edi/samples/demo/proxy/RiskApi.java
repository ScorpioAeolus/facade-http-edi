package com.facade.edi.samples.demo.proxy;

import com.facade.edi.starter.annotation.EdiApi;
import com.facade.edi.starter.annotation.api.GET;
import com.facade.edi.starter.annotation.param.Header;
import com.facade.edi.starter.annotation.param.Host;
import com.facade.edi.starter.annotation.param.Query;
import com.facade.edi.starter.annotation.param.ResponseConvert;
import com.facade.edi.starter.converter.ClientResponseConverter;

@EdiApi(hostKey = "misttrack.host")
public interface RiskApi {


    @GET("/v1/risk_score")
    AddressRiskResult queryRisk(@Query("coin") String coin
            , @Query("address") String address
            , @Query("api_key") String apiKey
            , @Header("Content-Type") String contentType
            , @Host("host") String host);

    @GET("/v1/risk_score")
    AddressRiskResult.DataBean queryRiskV2(@Query("coin") String coin
            , @Query("address") String address
            , @Query("api_key") String apiKey
            , @Header("Content-Type") String contentType
            , @ResponseConvert ClientResponseConverter<AddressRiskResult.DataBean> converter);
}
