package com.facade.edi.samples.demo.proxy.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayoutResult implements Serializable {

    private static final long serialVersionUID = -5980407373010674487L;

    private String orderNo;

    private Integer transferStatus;
}
