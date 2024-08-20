package com.facade.edi.starter.support;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ResponseHeader {
    private Map<String,String> responseHeader;
}
