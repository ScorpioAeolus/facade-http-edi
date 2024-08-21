package com.facade.edi.starter.support;

import com.facade.edi.starter.annotation.param.Header;
import com.facade.edi.starter.annotation.param.Host;
import com.facade.edi.starter.annotation.param.ResponseConvert;
import com.facade.edi.starter.converter.Converter;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.request.ParameterHandler;
import com.facade.edi.starter.annotation.api.DELETE;
import com.facade.edi.starter.annotation.api.GET;
import com.facade.edi.starter.annotation.api.OPTIONS;
import com.facade.edi.starter.annotation.api.PATCH;
import com.facade.edi.starter.annotation.api.POST;
import com.facade.edi.starter.annotation.api.PUT;
import com.facade.edi.starter.annotation.param.Body;
import com.facade.edi.starter.annotation.param.Field;
import com.facade.edi.starter.annotation.param.FieldMap;
import com.facade.edi.starter.annotation.param.FormUrlEncoded;
import com.facade.edi.starter.annotation.param.Multipart;
import com.facade.edi.starter.annotation.param.Path;
import com.facade.edi.starter.annotation.param.Query;
import com.facade.edi.starter.annotation.param.QueryMap;
import com.facade.edi.starter.util.EdiUtil;
import com.facade.edi.starter.annotation.EdiApi;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.facade.edi.starter.util.EdiUtil.getParameterUpperBound;
import static com.facade.edi.starter.util.EdiUtil.getRawType;
import static com.facade.edi.starter.util.EdiUtil.getSupertype;
import static com.facade.edi.starter.util.EdiUtil.parameterError;

@Slf4j
public class RequestFactory {
    public static RequestFactory parseAnnotations(Method method, EdiApiProxyFactory apiProxyFactory) {
        return new Builder(method, apiProxyFactory).build();
    }

    private final Method method;
    private final String httpMethod;
    private final String relativeUrl;
    private final boolean hasBody;
    private final boolean isFormEncoded;
    private final boolean isMultipart;


    private ParameterHandler<?>[] parameterHandlers;

    public RequestFactory(Builder builder) {
        method = builder.method;
        httpMethod = builder.httpMethod;
        relativeUrl = builder.relativeUrl;
        hasBody = builder.hasBody;
        isFormEncoded = builder.isFormEncoded;
        isMultipart = builder.isMultipart;
        parameterHandlers = builder.parameterHandlers;
        //this.outTarget = builder.outTarget;

        //this.userType = builder.userType;
        //this.tokenService=builder.tokenService;
    }


    public HttpApiRequest create(Object[] args,String host) throws IOException {
        ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) parameterHandlers;
        int argumentCount = args.length;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException(
                    "Argument count ("
                            + argumentCount
                            + ") doesn't match expected count ("
                            + handlers.length
                            + ")");
        }


        HttpApiRequest request = new HttpApiRequest();
        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(request, args[p]);
        }
        request.setHttpMethod(httpMethod);
        //构造完整url的时候,如果method维度制定了host那么优先使用method的host,否则使用EdiApi指定的host
        request.setUrl(this.buildFullUrl(null != request.getHost()? request.getHost() : host,relativeUrl));
        return request;
    }

    /**
     * 构造完整的url
     *
     * @param host
     * @param relativeUrl
     * @return
     */
    private String buildFullUrl(String host, String relativeUrl) {
        // 移除host末尾的斜杠（如果存在）
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }

        // 移除uri开头的斜杠（如果存在）
        if (relativeUrl.startsWith("/")) {
            relativeUrl = relativeUrl.substring(1);
        }

        // 拼接host和uri
        return host + "/" + relativeUrl;
    }

    public Method getMethod() {
        return method;
    }

    /**
     * Inspects the annotations on an interface method to construct a reusable service method. This
     * requires potentially-expensive reflection so it is best to build each service method only once
     * and reuse it. Builders cannot be reused.
     */
    static final class Builder {
        private static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
        private static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
        private static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

        final Method method;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotationsArray;
        final Type[] parameterTypes;


        boolean gotField;
        boolean gotPart;
        boolean gotBody;
        boolean gotPath;
        boolean gotQuery;
        boolean gotQueryName;
        boolean gotQueryMap;
        boolean gotUrl;

        boolean gotHost;

        boolean gotHeader;

        boolean gotConverter;

        String httpMethod;
        boolean hasBody;
        boolean isFormEncoded;
        boolean isMultipart;
        String relativeUrl;

        Set<String> relativeUrlParamNames;
        ParameterHandler<?>[] parameterHandlers;
        EdiApiProxyFactory ediApiProxyFactory;
        //OutTarget outTarget;
        String hostKey;
//        UserTypeEnum userType;
//        TokenService tokenService;

        Builder(Method method, EdiApiProxyFactory apiProxyFactory) {
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterTypes = method.getGenericParameterTypes();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
            this.ediApiProxyFactory = apiProxyFactory;
        }

        RequestFactory build() {
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            if (httpMethod == null) {
                throw EdiUtil.methodError(method, "HTTP method annotation is required (e.g., @GET, @POST, etc.).");
            }

            if (!hasBody) {
                if (isMultipart) {
                    throw EdiUtil.methodError(
                            method,
                            "Multipart can only be specified on HTTP methods with request body (e.g., @POST).");
                }
                if (isFormEncoded) {
                    throw EdiUtil.methodError(
                            method,
                            "FormUrlEncoded can only be specified on HTTP methods with "
                                    + "request body (e.g., @POST).");
                }
            }

            int parameterCount = parameterAnnotationsArray.length;
            parameterHandlers = new ParameterHandler<?>[parameterCount];
            for (int p = 0; p < parameterCount; p++) {
                parameterHandlers[p] = parseParameter(p, parameterTypes[p], parameterAnnotationsArray[p], ediApiProxyFactory);
            }

            if (relativeUrl == null && !gotUrl) {
                throw EdiUtil.methodError(method, "Missing either @%s URL or @Url parameter.", httpMethod);
            }
            if (!isFormEncoded && !isMultipart && !hasBody && gotBody) {
                throw EdiUtil.methodError(method, "Non-body HTTP method cannot contain @Body.");
            }
            if (isFormEncoded && !gotField) {
                throw EdiUtil.methodError(method, "Form-encoded method must contain at least one @Field.");
            }
            if (isMultipart && !gotPart) {
                throw EdiUtil.methodError(method, "Multipart method must contain at least one @Part.");
            }
            EdiApi ediApi = method.getDeclaringClass().getAnnotation(EdiApi.class);
            //outTarget = ediApi.site();
            hostKey = ediApi.hostKey();
            return new RequestFactory(this);
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof DELETE) {
                parseHttpMethodAndPath("DELETE", ((DELETE) annotation).value(), false);
            } else if (annotation instanceof GET) {
                parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
            } else if (annotation instanceof PATCH) {
                parseHttpMethodAndPath("PATCH", ((PATCH) annotation).value(), true);
            } else if (annotation instanceof POST) {
                parseHttpMethodAndPath("POST", ((POST) annotation).value(), true);
            } else if (annotation instanceof PUT) {
                parseHttpMethodAndPath("PUT", ((PUT) annotation).value(), true);
            } else if (annotation instanceof OPTIONS) {
                parseHttpMethodAndPath("OPTIONS", ((OPTIONS) annotation).value(), false);
            } else if (annotation instanceof Multipart) {
                if (isFormEncoded) {
                    throw EdiUtil.methodError(method, "Only one encoding annotation is allowed.");
                }
                isMultipart = true;
            } else if (annotation instanceof FormUrlEncoded) {
                if (isMultipart) {
                    throw EdiUtil.methodError(method, "Only one encoding annotation is allowed.");
                }
                isFormEncoded = true;
            }
        }

        private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
            if (this.httpMethod != null) {
                throw EdiUtil.methodError(
                        method,
                        "Only one HTTP method is allowed. Found: %s and %s.",
                        this.httpMethod,
                        httpMethod);
            }
            this.httpMethod = httpMethod;
            this.hasBody = hasBody;

            if (value.isEmpty()) {
                return;
            }

            // Get the relative URL path and existing query string, if present.
            int question = value.indexOf('?');
            if (question != -1 && question < value.length() - 1) {
                // Ensure the query string does not have any named parameters.
                String queryParams = value.substring(question + 1);
                Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
                if (queryParamMatcher.find()) {
                    throw EdiUtil.methodError(
                            method,
                            "URL query string \"%s\" must not have replace block. "
                                    + "For dynamic query parameters use @Query.",
                            queryParams);
                }
            }

            this.relativeUrl = value;
            this.relativeUrlParamNames = parsePathParameters(value);
        }






        private void validateResolvableType(int p, Type type) {
            if (EdiUtil.hasUnresolvableType(type)) {
                throw parameterError(
                        method, p, "Parameter type must not include a type variable or wildcard: %s", type);
            }
        }

        private void validatePathName(int p, String name) {
            if (!PARAM_NAME_REGEX.matcher(name).matches()) {
                throw parameterError(
                        method,
                        p,
                        "@Path parameter name must match %s. Found: %s",
                        PARAM_URL_REGEX.pattern(),
                        name);
            }
            // Verify URL replacement name is actually present in the URL path.
            if (!relativeUrlParamNames.contains(name)) {
                throw parameterError(method, p, "URL \"%s\" does not contain \"{%s}\".", relativeUrl, name);
            }
        }

        private ParameterHandler<?> parseParameter(
                int p, Type parameterType, Annotation[] annotations, EdiApiProxyFactory apiProxy) {
            ParameterHandler<?> result = null;
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    ParameterHandler<?> annotationAction =
                            parseParameterAnnotation(p, parameterType, annotation, apiProxy);

                    if (annotationAction == null) {
                        continue;
                    }

                    if (result != null) {
                        throw parameterError(
                                method, p, "Multiple Retrofit annotations found, only one allowed.");
                    }

                    result = annotationAction;
                }
            }

            if (result == null) {
                throw parameterError(method, p, "No Retrofit annotation found.");
            }

            return result;
        }


        private ParameterHandler<?> parseParameterAnnotation(
                int p, Type type, Annotation annotation, EdiApiProxyFactory apiProxy) {
            Converter<Object, String> converter = apiProxy.stringConverter();
            if (annotation instanceof Path) {
                return processPath(p, type, (Path) annotation, converter);
            } else if (annotation instanceof Query) {
                return processQuery(p, type, (Query) annotation, converter);
            } else if (annotation instanceof QueryMap) {
                return processQueryMap(p, type, converter);
            } else if (annotation instanceof Field) {
                return processField(p, type, (Field) annotation, converter);
            } else if (annotation instanceof FieldMap) {
                return processFieldMap(p, type, converter);
            } else if (annotation instanceof Body) {
                return processBody(p, type, apiProxy);
            } else if (annotation instanceof Header) {
                return processHeader(p,type,(Header) annotation,converter);
            } else if (annotation instanceof Host) {
                return processHost(p,type,(Host)annotation,converter);
            } else if (annotation instanceof ResponseConvert) {
                return processResponseConverter(p,type);
            }

            return null;
        }

        private ParameterHandler<?> processResponseConverter(int p,Type type) {
            validateResolvableType(p, type);
            gotConverter = true;
            return new ParameterHandler.ResponseConverter<>(method,p);
        }

        private ParameterHandler<?> processHost(int p,Type type, Host host, Converter<Object, String> converter) {
            validateResolvableType(p, type);
            gotHost = true;
            String name = host.value();
            return new ParameterHandler.Host<>(method,p,name,converter);
        }

        private ParameterHandler<?> processHeader(int p,Type type, Header header, Converter<Object, String> converter) {
            validateResolvableType(p, type);
            gotHeader = true;
            String name = header.value();
            return new ParameterHandler.Header<>(method,p,name,converter);
        }

        private ParameterHandler.Path<Object> processPath(int p, Type type, Path annotation, Converter<Object, String> converter) {
            validateResolvableType(p, type);
            if (gotQuery) {
                throw parameterError(method, p, "A @Path parameter must not come after a @Query.");
            }
            if (gotQueryName) {
                throw parameterError(method, p, "A @Path parameter must not come after a @QueryName.");
            }
            if (gotQueryMap) {
                throw parameterError(method, p, "A @Path parameter must not come after a @QueryMap.");
            }
            if (gotUrl) {
                throw parameterError(method, p, "@Path parameters may not be used with @Url.");
            }
            if (relativeUrl == null) {
                throw parameterError(
                        method, p, "@Path can only be used with relative url on @%s", httpMethod);
            }
            gotPath = true;

            Path path = annotation;
            String name = path.value();
            validatePathName(p, name);

            return new ParameterHandler.Path<>(method, p, name, converter);
        }

        private ParameterHandler.Body<?> processBody(int p, Type type, EdiApiProxyFactory apiProxy) {
            validateResolvableType(p, type);
            if (isFormEncoded || isMultipart) {
                throw parameterError(
                        method, p, "@Body parameters cannot be used with form or multi-part encoding.");
            }
            if (gotBody) {
                throw parameterError(method, p, "Multiple @Body method annotations found.");
            }
            Converter<?, String> requestBodyConverter = apiProxy.requestBodyConverter(type);
            gotBody = true;
            return new ParameterHandler.Body<>(method, p, requestBodyConverter);
        }

        private ParameterHandler.FieldMap<Object> processFieldMap(int p, Type type, Converter<Object, String> converter) {
            validateResolvableType(p, type);
            if (!isFormEncoded) {
                throw parameterError(
                        method, p, "@FieldMap parameters can only be used with form encoding.");
            }
            Class<?> rawParameterType = getRawType(type);
            if (!Map.class.isAssignableFrom(rawParameterType)) {
                throw parameterError(method, p, "@FieldMap parameter type must be Map.");
            }
            Type mapType = getSupertype(type, rawParameterType, Map.class);
            if (!(mapType instanceof ParameterizedType)) {
                throw parameterError(
                        method, p, "Map must include generic types (e.g., Map<String, String>)");
            }
            ParameterizedType parameterizedType = (ParameterizedType) mapType;
            Type keyType = getParameterUpperBound(0, parameterizedType);
            if (String.class != keyType) {
                throw parameterError(method, p, "@FieldMap keys must be of type String: " + keyType);
            }
            gotField = true;
            return new ParameterHandler.FieldMap<>(method, p, converter);
        }

        private ParameterHandler<?> processField(int p, Type type, Field annotation, Converter<Object, String> converter) {
            validateResolvableType(p, type);
            if (!isFormEncoded) {
                throw parameterError(method, p, "@Field parameters can only be used with form encoding.");
            }
            Field field = annotation;
            String name = field.value();

            gotField = true;

            Class<?> rawParameterType = getRawType(type);
            if (Iterable.class.isAssignableFrom(rawParameterType)) {
                if (!(type instanceof ParameterizedType)) {
                    throw parameterError(
                            method,
                            p,
                            rawParameterType.getSimpleName()
                                    + " must include generic type (e.g., "
                                    + rawParameterType.getSimpleName()
                                    + "<String>)");
                }
                return new ParameterHandler.Field<>(name, converter).iterable();
            } else if (rawParameterType.isArray()) {
                return new ParameterHandler.Field<>(name, converter).array();
            } else {
                return new ParameterHandler.Field<>(name, converter);
            }
        }

        private ParameterHandler.QueryMap<Object> processQueryMap(int p, Type type, Converter<Object, String> converter) {
            validateResolvableType(p, type);
            Class<?> rawParameterType = getRawType(type);
            gotQueryMap = true;
            if (!Map.class.isAssignableFrom(rawParameterType)) {
                throw parameterError(method, p, "@QueryMap parameter type must be Map.");
            }
            Type mapType = getSupertype(type, rawParameterType, Map.class);
            if (!(mapType instanceof ParameterizedType)) {
                throw parameterError(
                        method, p, "Map must include generic types (e.g., Map<String, String>)");
            }
            ParameterizedType parameterizedType = (ParameterizedType) mapType;
            Type keyType = getParameterUpperBound(0, parameterizedType);
            if (String.class != keyType) {
                throw parameterError(method, p, "@QueryMap keys must be of type String: " + keyType);
            }

            return new ParameterHandler.QueryMap<>(
                    method, p, converter);
        }

        private ParameterHandler<?> processQuery(int p, Type type, Query annotation, Converter<Object, String> converter) {
            validateResolvableType(p, type);
            Query query = annotation;
            String name = query.value();

            Class<?> rawParameterType = getRawType(type);
            gotQuery = true;
            if (Iterable.class.isAssignableFrom(rawParameterType)) {
                if (!(type instanceof ParameterizedType)) {
                    throw parameterError(method, p, rawParameterType.getSimpleName() + " must include generic type (e.g., " + rawParameterType.getSimpleName() + "<String>)");
                }
                return new ParameterHandler.Query<>(name, converter).iterable();
            } else if (rawParameterType.isArray()) {
                return new ParameterHandler.Query<>(name, converter).array();
            } else {
                return new ParameterHandler.Query<>(name, converter);
            }
        }

        /**
         * Gets the set of unique path parameters used in the given URI. If a parameter is used twice in
         * the URI, it will only show up once in the set.
         */
        static Set<String> parsePathParameters(String path) {
            Matcher m = PARAM_URL_REGEX.matcher(path);
            Set<String> patterns = new LinkedHashSet<>();
            while (m.find()) {
                patterns.add(m.group(1));
            }
            return patterns;
        }
    }
}
