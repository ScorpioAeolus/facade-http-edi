package com.facade.edi.starter.constants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author typhoon
 * @date 2021/3/1 6:21 下午
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pair<K,V> implements Serializable {

    private static final long serialVersionUID = 2998829049460781252L;

    private K left;

    private V right;

    public static final <K,V> Pair<K,V> of(K k,V v) {
        return new Pair<>(k,v);
    }

}
