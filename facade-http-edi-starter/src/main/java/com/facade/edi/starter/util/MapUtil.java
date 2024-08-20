package com.facade.edi.starter.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {


    public static final boolean isEmpty(Map map) {
        return null == map || map.size() == 0;
    }


    public static final boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }


    public static final <K,T extends Object> String toPlaintString(Map<K,T> map) {
        if (null == map || map.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (Map.Entry<K, T> entry : map.entrySet()) {
            builder.append(",")
                    .append("\"")
                    .append(entry.getKey())
                    .append("\"")
                    .append(":")
                    .append("\"")
                    .append(entry.getValue())
                    .append("\"");
        }
        builder.append("}");
        return builder.toString().replaceFirst(",","");
    }

    public static final <K,T extends Object> Map<K,T> emptyMap() {
        return new HashMap<>();
    }


}
