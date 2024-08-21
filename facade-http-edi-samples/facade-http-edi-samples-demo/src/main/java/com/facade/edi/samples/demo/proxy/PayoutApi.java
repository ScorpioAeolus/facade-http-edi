package com.facade.edi.samples.demo.proxy;

import com.facade.edi.samples.demo.proxy.req.CreatePayoutPO;
import com.facade.edi.samples.demo.proxy.resp.PayoutResult;
import com.facade.edi.starter.annotation.EdiApi;
import com.facade.edi.starter.annotation.api.POST;
import com.facade.edi.starter.annotation.param.Body;
import com.facade.edi.starter.annotation.param.Header;
import com.facade.edi.starter.annotation.param.ResponseConvert;
import com.facade.edi.starter.converter.ClientResponseConverter;

@EdiApi(hostKey = "payout.host")
public interface PayoutApi {

    @POST("/support/transfer")
    //@POST("/in-api/support/user/sync")
    PayoutResult createPayout(@Header("ACCESS-SECRET") String accessSecret
            , @Header("Content-Type") String contentType
            , @Body CreatePayoutPO createPayoutPO, @ResponseConvert ClientResponseConverter<PayoutResult> converter);

}
