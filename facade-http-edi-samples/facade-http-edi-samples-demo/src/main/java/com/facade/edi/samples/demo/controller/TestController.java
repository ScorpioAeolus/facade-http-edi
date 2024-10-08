package com.facade.edi.samples.demo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.facade.edi.samples.demo.proxy.AddressRiskResult;
import com.facade.edi.samples.demo.proxy.ExchangeRate;
import com.facade.edi.samples.demo.proxy.PayoutApi;
import com.facade.edi.samples.demo.proxy.RateApi;
import com.facade.edi.samples.demo.proxy.RiskApi;
import com.facade.edi.samples.demo.proxy.req.CreatePayoutPO;
import com.facade.edi.samples.demo.proxy.req.QueryPayoutPO;
import com.facade.edi.samples.demo.proxy.resp.PayoutResult;
import com.facade.edi.samples.demo.proxy.resp.QueryPayoutResult;
import com.facade.edi.starter.constants.EntityError;
import com.facade.edi.starter.converter.ClientResponseConverter;
import com.facade.edi.starter.exception.EdiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {



    @Value("${currencyapi.host:}")
    private String host;

    @Value("${currencyapi.apiKey:}")
    private String apiKey;

    @Value("${misttrack.host:}")
    private String riskHost;

    @Value("${misttrack.apiKey:}")
    private String riskApiKey;

    @Value("${payout.accessSecret:}")
    private String payoutAccessSecret;

    @Resource
    RateApi rateApi;

    @Resource
    RiskApi riskApi;

    @Resource
    PayoutApi payoutApi;

    private static final String PAYOUT_SUCCESS_CODE = "00000";

    @GetMapping("/rate")
    public String getRate() {
        ExchangeRate result = rateApi.getRate("AED","INR","application/json",this.apiKey,this.host);
        return JSONObject.toJSONString(result);
    }

    @GetMapping("/rate/v2")
    public String getRateV2() {
        ExchangeRate result = rateApi.getRateV2("AED", "INR", "application/json", this.apiKey, t -> JSONObject.parseObject(t,ExchangeRate.class));
        return JSONObject.toJSONString(result);
    }

    @GetMapping("/risk")
    public String queryRisk() {
        AddressRiskResult result = this.riskApi.queryRisk("USDT-TRC20","TJWwwpnfKNx2EMTaDrJ6KuEoj6ERpUcVKj",this.riskApiKey,"application/json",this.riskHost);
        return JSONObject.toJSONString(result);
    }

    @GetMapping("/risk/v2")
    public String queryRiskV2() {
        ClientResponseConverter<AddressRiskResult.DataBean> converter = t -> {
            AddressRiskResult result = JSON.parseObject(t, AddressRiskResult.class);
            if (null == result || !Boolean.TRUE.equals(result.getSuccess())) {
                log.warn("queryRiskV2;result={}", result);
                throw new EdiException(EntityError.RPC_RESPONSE_FALSE.getCode(),null != result ? result.getMsg() : EntityError.RPC_RESPONSE_FALSE.getMsg());
            }
            return result.getData();
        };
        AddressRiskResult.DataBean result = this.riskApi.queryRiskV2("USDT-TRC20", "TJWwwpnfKNx2EMTaDrJ6KuEoj6ERpUcVKj", this.riskApiKey, "application/json",converter );
        return JSONObject.toJSONString(result);
    }

    @GetMapping("/risk/v3")
    public String queryRiskV3() {
        AddressRiskResult.DataBean result = this.riskApi.queryRiskV3("USDT-TRC20", "TJWwwpnfKNx2EMTaDrJ6KuEoj6ERpUcVKj", this.riskApiKey, "application/json","test" );
        return JSONObject.toJSONString(result);
    }

    @PostMapping("/payout/create")
    public String createPayout(@RequestBody CreatePayoutPO createPayoutPO) {
        PayoutResult payoutResult = this.payoutApi.createPayout(this.payoutAccessSecret,"application/json",createPayoutPO, t -> JSONObject.parseObject(t,PayoutResult.class));
        return JSONObject.toJSONString(payoutResult);
    }

    @PostMapping("/payout/query")
    public String queryPayout(@RequestBody QueryPayoutPO queryPayoutPO) {
        List<QueryPayoutResult.DataBean> results = this.payoutApi.queryPayout(this.payoutAccessSecret, "application/json", queryPayoutPO, t -> {
            QueryPayoutResult result = JSONObject.parseObject(t,QueryPayoutResult.class);
            if(null == result || !Objects.equals(PAYOUT_SUCCESS_CODE,result.getCode())) {
                return Collections.emptyList();
            }
            return result.getData();
        });
        return JSONObject.toJSONString(results);
    }
}
