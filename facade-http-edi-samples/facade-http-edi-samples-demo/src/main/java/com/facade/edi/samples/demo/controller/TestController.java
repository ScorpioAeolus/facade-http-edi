package com.facade.edi.samples.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.facade.edi.samples.demo.proxy.ExchangeRate;
import com.facade.edi.samples.demo.proxy.RateApi;
import com.facade.edi.starter.response.BaseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {


    @Value("${currencyapi.apiUrl:}")
    private String apiUrl;

    @Value("${currencyapi.host:}")
    private String host;

    @Value("${currencyapi.apiKey:}")
    private String apiKey;

    @Resource
    RateApi rateApi;

    @GetMapping("rate")
    public String getRate() {
        BaseResult<ExchangeRate> result = rateApi.getRate("AED","INR","application/json",this.apiKey,this.host);
        return JSONObject.toJSONString(result);
    }
}
