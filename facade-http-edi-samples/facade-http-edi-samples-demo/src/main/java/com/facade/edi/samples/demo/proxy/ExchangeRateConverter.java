package com.facade.edi.samples.demo.proxy;

import com.alibaba.fastjson.JSONObject;
import com.facade.edi.starter.converter.ClientResponseConverter;

import java.io.IOException;

public class ExchangeRateConverter implements ClientResponseConverter<ExchangeRate> {
    @Override
    public ExchangeRate convert(String t) throws IOException {
        return JSONObject.parseObject(t,ExchangeRate.class);
    }
}
