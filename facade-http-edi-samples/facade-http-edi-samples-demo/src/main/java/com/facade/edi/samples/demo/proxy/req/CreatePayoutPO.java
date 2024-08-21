package com.facade.edi.samples.demo.proxy.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreatePayoutPO implements Serializable {

    private static final long serialVersionUID = 5495038175269739397L;

    private String appCode;

    private String customerId;

    private String outOrderNo;

    private String accountName;

    private String accountNumber;

    private String mobileNumber;

    private String ifsc;

    private BigDecimal transferAmount;

    private String email;
}
