package com.facade.edi.starter.request;



import com.facade.edi.starter.converter.ClientResponseConverter;
import com.facade.edi.starter.converter.Converter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import static com.facade.edi.starter.util.EdiUtil.parameterError;


public abstract class ParameterHandler<T> {
    public abstract void apply(HttpApiRequest httpApiRequest, T value) throws IOException;

    public final ParameterHandler<Iterable<T>> iterable() {
        return new ParameterHandler<Iterable<T>>() {
            @Override
            public void apply(HttpApiRequest httpApiRequest, Iterable<T> values) throws IOException {
                if (values == null) {
                    return;
                }
                for (T value : values) {
                    ParameterHandler.this.apply(httpApiRequest, value);
                }
            }
        };
    }

    public final ParameterHandler<Object> array() {
        return new ParameterHandler<Object>() {
            @Override
            public void apply(HttpApiRequest request, Object values) throws IOException {
                if (values == null) {
                    return;
                }

                for (int i = 0, size = Array.getLength(values); i < size; i++) {
                    ParameterHandler.this.apply(request, (T) Array.get(values, i));
                }
            }
        };
    }


    public static final class Path<T> extends ParameterHandler<T> {
        private final Method method;
        private final int p;
        private final String name;
        private final Converter<Object, String> valueConverter;

        public Path(Method method, int p, String name, Converter<Object, String> valueConverter) {
            this.method = method;
            this.p = p;
            this.name = Objects.requireNonNull(name, "name == null");
            this.valueConverter = valueConverter;
        }

        @Override
        public void apply(HttpApiRequest request, T value) throws IOException {
            if (value == null) {
                throw parameterError(method, p, "Path parameter \"" + name + "\" value must not be null.");
            }
            request.addPathParam(name, valueConverter.convert(value));
        }
    }

    public static final class Query<T> extends ParameterHandler<T> {
        private final String name;
        private final Converter<Object, String> valueConverter;

        public Query(String name, Converter<Object, String> valueConverter) {
            this.name = Objects.requireNonNull(name, "name == null");
            this.valueConverter = valueConverter;
        }

        @Override
        public void apply(HttpApiRequest request, T value) throws IOException {
            if (value == null) {
                return;
            }

            String queryValue = valueConverter.convert(value);
            if (queryValue == null) {
                return;
            }

            request.addQueryParam(name, queryValue);
        }
    }

    public static final class Header<T> extends ParameterHandler<T> {
        private final String name;

        private Method method;

        private final int p;
        private final Converter<Object, String> valueConverter;

        public Header(Method method, int p, String name, Converter<Object, String> valueConverter) {
            this.method = method;
            this.p = p;
            this.name = Objects.requireNonNull(name, "name == null");
            this.valueConverter = valueConverter;
        }

        @Override
        public void apply(HttpApiRequest request, T value) throws IOException {
            if (value == null) {
                return;
            }

            String headerValue = valueConverter.convert(value);
            if (headerValue == null) {
                return;
            }

            request.addHeader(name, headerValue);
        }
    }

    public static final class Host<T> extends ParameterHandler<T> {

        private Method method;

        private final String name;
        private final int p;
        private final Converter<Object, String> valueConverter;

        public Host(Method method, int p, String name, Converter<Object, String> valueConverter) {
            this.method = method;
            this.p = p;
            this.name = Objects.requireNonNull(name, "name == null");
            this.valueConverter = valueConverter;
        }

        @Override
        public void apply(HttpApiRequest request, T value) throws IOException {
            if (value == null) {
                return;
            }

            String hostValue = valueConverter.convert(value);
            if (hostValue == null) {
                return;
            }
            request.setHost(hostValue);
        }
    }

    public static final class ResponseConverter<T extends ClientResponseConverter<?>> extends ParameterHandler<T> {

        private Method method;

        private final int p;

        public ResponseConverter(Method method, int p) {
            this.method = method;
            this.p = p;
        }

        @Override
        public void apply(HttpApiRequest request, T value) throws IOException {
            if (value == null) {
                return;
            }
            request.setResponseConverter(value);
        }
    }


    public static final class QueryMap<T> extends ParameterHandler<Map<String, T>> {
        private final Method method;
        private final int p;
        private final Converter<Object, String> valueConverter;

        public QueryMap(Method method, int p, Converter<Object, String> valueConverter) {
            this.method = method;
            this.p = p;
            this.valueConverter = valueConverter;
        }

        @Override
        public void apply(HttpApiRequest request, Map<String, T> value) throws IOException {
            if (value == null) {
                throw parameterError(method, p, "Query map was null");
            }

            for (Map.Entry<String, T> entry : value.entrySet()) {
                String entryKey = entry.getKey();
                if (entryKey == null) {
                    throw parameterError(method, p, "Query map contained null key.");
                }
                T entryValue = entry.getValue();
                if (entryValue == null) {
                    throw parameterError(
                            method, p, "Query map contained null value for key '" + entryKey + "'.");
                }

                String convertedEntryValue = valueConverter.convert(entryValue);
                if (convertedEntryValue == null) {
                    throw parameterError(
                            method,
                            p,
                            "Query map value '"
                                    + entryValue
                                    + "' converted to null by "
                                    + valueConverter.getClass().getName()
                                    + " for key '"
                                    + entryKey
                                    + "'.");
                }

                request.addQueryParam(entryKey, convertedEntryValue);
            }
        }
    }


    public static final class Field<T> extends ParameterHandler<T> {
        private final String name;
        private final Converter<Object, String> valueConverter;

        public Field(String name, Converter<Object, String> valueConverter) {
            this.name = Objects.requireNonNull(name, "name == null");
            this.valueConverter = valueConverter;
        }

        @Override
        public void apply(HttpApiRequest request, T value) throws IOException {
            if (value == null) {
                return;
            }

            String fieldValue = valueConverter.convert(value);
            if (fieldValue == null) {
                return;
            }

            request.addFormField(name, fieldValue);
        }
    }

    public static final class FieldMap<T> extends ParameterHandler<Map<String, T>> {
        private final Method method;
        private final int p;
        private final Converter<Object, String> valueConverter;

        public FieldMap(Method method, int p, Converter<Object, String> valueConverter) {
            this.method = method;
            this.p = p;
            this.valueConverter = valueConverter;
        }

        @Override
        public void apply(HttpApiRequest request, Map<String, T> value) throws IOException {
            if (value == null) {
                throw parameterError(method, p, "Field map was null.");
            }

            for (Map.Entry<String, T> entry : value.entrySet()) {
                String entryKey = entry.getKey();
                if (entryKey == null) {
                    throw parameterError(method, p, "Field map contained null key.");
                }
                T entryValue = entry.getValue();
                if (entryValue == null) {
                    throw parameterError(
                            method, p, "Field map contained null value for key '" + entryKey + "'.");
                }

                String fieldEntry = valueConverter.convert(entryValue);
                if (fieldEntry == null) {
                    throw parameterError(
                            method,
                            p,
                            "Field map value '"
                                    + entryValue
                                    + "' converted to null by "
                                    + valueConverter.getClass().getName()
                                    + " for key '"
                                    + entryKey
                                    + "'.");
                }

                request.addFormField(entryKey, fieldEntry);
            }
        }
    }


    public static final class Body<T> extends ParameterHandler<T> {
        private final Method method;
        private final int p;
        private final Converter<T, String> converter;

        public Body(Method method, int p, Converter<T, String> converter) {
            this.method = method;
            this.p = p;
            this.converter = converter;
        }

        @Override
        public void apply(HttpApiRequest request, T value) throws IOException {
            if (value == null) {
                throw parameterError(method, p, "Body parameter value must not be null.");
            }
            request.setBody(converter.convert(value));
        }
    }


}
