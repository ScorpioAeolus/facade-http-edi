package com.facade.edi.samples.demo.proxy;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 查询汇率响应,数据格式如下:
 *
 * {
 *     "meta": {
 *         "last_updated_at": "2023-06-23T10:15:59Z"
 *     },
 *     "data": {
 *         "AED": {
 *             "code": "AED",
 *             "value": 3.67306
 *         },
 *         "AFN": {
 *             "code": "AFN",
 *             "value": 91.80254
 *         },
 *         "ALL": {
 *             "code": "ALL",
 *             "value": 108.22904
 *         },
 *         "AMD": {
 *             "code": "AMD",
 *             "value": 480.41659
 *         },
 *         "...": "150+ more currencies"
 *     }
 * }
 *
 * @author typhoon
 * @date 2024-08-06 11:32 Tuesday
 */
@Data
public class ExchangeRate implements Serializable {

    private static final long serialVersionUID = 7701571620534280933L;

    private Map<String,Map<String,String>> data;
}
