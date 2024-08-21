package com.facade.edi.samples.demo.proxy.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryPayoutPO implements Serializable {

    private static final long serialVersionUID = 1634191739776120948L;

    private String appCode;

    private List<String> orderNos;
}
