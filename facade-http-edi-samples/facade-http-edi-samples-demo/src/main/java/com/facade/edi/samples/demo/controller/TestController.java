package com.facade.edi.samples.demo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facade.edi.samples.demo.proxy.AddressRiskResult;
import com.facade.edi.samples.demo.proxy.ExchangeRate;
import com.facade.edi.samples.demo.proxy.RateApi;
import com.facade.edi.samples.demo.proxy.RiskApi;
import com.facade.edi.starter.converter.ClientResponseConverter;
import com.facade.edi.starter.converter.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {



    @Value("${currencyapi.host:}")
    private String host;

    @Value("${currencyapi.apiKey:}")
    private String apiKey;

    @Value("${misttrack.host:}")
    private String riskHost;

    @Value("${misttrack.apiKey:}")
    private String riskApiKey;

    @Resource
    RateApi rateApi;

    @Resource
    RiskApi riskApi;

    @GetMapping("rate")
    public String getRate() {
        ExchangeRate result = rateApi.getRate("AED","INR","application/json",this.apiKey,this.host);
        return JSONObject.toJSONString(result);
    }

    @GetMapping("/risk")
    public String queryRisk() {
        Converter<String,AddressRiskResult.DataBean> converter = (ClientResponseConverter<AddressRiskResult.DataBean>) t -> {
            AddressRiskResult result = JSON.parseObject(t, AddressRiskResult.class);
            if(Boolean.TRUE.equals(result.getSuccess())) {
                return result.getData();
            }
            throw new RuntimeException("remote call failed");
        };
        AddressRiskResult result = this.riskApi.queryRisk("USDT-TRC20","TJWwwpnfKNx2EMTaDrJ6KuEoj6ERpUcVKj",this.riskApiKey,"application/json",this.riskHost);
        return JSONObject.toJSONString(result);
    }
}
