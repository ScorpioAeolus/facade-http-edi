package com.facade.edi.starter.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.lang.reflect.Type;

public class ConverterFactory {

    private final ObjectMapper mapper;

    public ConverterFactory() {
        mapper = new ObjectMapper();
//        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCaseStrategy.SNAKE_CASE);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }


    public Converter<?, String> requestBodyConverter(Type type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectWriter writer = mapper.writerFor(javaType);
        return new JacksonRequestBodyConverter<>(writer);
    }

    public Converter<String, ?> buildJacksonConverter(Type type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectReader reader = mapper.readerFor(javaType);
        return new JacksonStringConverter<>(reader);
    }

    public Converter<String, ?> responseBodyConverter(Type type) {
        switch (defineType(type)) {
            case 1:
                return (Converter<String, Byte>) Byte::valueOf;
            case 2:
                return (Converter<String, Short>) Short::valueOf;
            case 3:
                return (Converter<String, Integer>) Integer::valueOf;
            case 4:
                return (Converter<String, Long>) Long::valueOf;
            case 5:
                return (Converter<String, Double>) Double::valueOf;
            case 6:
                return (Converter<String, Float>) Float::valueOf;
            case 7:
                return (Converter<String, Character>) value -> value.charAt(0);
            case 8:
                return (Converter<String, String>) value -> value;
            default:
                return buildJacksonConverter(type);
        }
    }

    private int defineType(Type type) {
        if (type == Byte.class || type == byte.class) {
            return 1;
        } else if (type == Short.class || type == short.class) {
            return 2;
        } else if (type == Integer.class || type == int.class) {
            return 3;
        } else if (type == Long.class || type == long.class) {
            return 4;
        } else if (type == Double.class || type == double.class) {
            return 5;
        } else if (type == Float.class || type == float.class) {
            return 6;
        } else if (type == Character.class || type == char.class) {
            return 7;
        } else if (type == String.class) {
            return 8;
        }
        return 0;
    }

    public Converter<Object, String> stringConverter() {
        return ToStringConverter.INSTANCE;
    }

    private static class JacksonStringConverter<T> implements Converter<String, T> {
        private final ObjectReader adapter;

        public JacksonStringConverter(ObjectReader adapter) {
            this.adapter = adapter;
        }

        @Override
        public T convert(String value) throws IOException {
            return adapter.readValue(value);
        }
    }

    private static class JacksonRequestBodyConverter<T> implements Converter<T, String> {

        private final ObjectWriter adapter;

        public JacksonRequestBodyConverter(ObjectWriter adapter) {
            this.adapter = adapter;
        }

        @Override
        public String convert(T value) throws IOException {
            return adapter.writeValueAsString(value);
        }

    }

    public static class ToStringConverter implements Converter<Object, String> {
        public static final ToStringConverter INSTANCE = new ToStringConverter();

        @Override
        public String convert(Object o) {
            return o.toString();
        }

    }

}
