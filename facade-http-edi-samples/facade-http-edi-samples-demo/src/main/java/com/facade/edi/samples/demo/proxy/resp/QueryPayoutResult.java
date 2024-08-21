package com.facade.edi.samples.demo.proxy.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class QueryPayoutResult {


    private String code;

    private String message;

    private List<DataBean> data;



    @Data
    public static class DataBean {
        private String orderNo;

        private Integer transferStatus;

        private BigDecimal transferAmount;
    }

}
