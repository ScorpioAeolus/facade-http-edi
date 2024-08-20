package com.facade.edi.starter.enums;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.Arrays;

public enum HttpState {
    CONTINUE(100, HttpState.Series.INFORMATIONAL, "Continue"),
    SWITCHING_PROTOCOLS(101, HttpState.Series.INFORMATIONAL, "Switching Protocols"),
    PROCESSING(102, HttpState.Series.INFORMATIONAL, "Processing"),
    CHECKPOINT(103, HttpState.Series.INFORMATIONAL, "Checkpoint"),
    OK(200, HttpState.Series.SUCCESSFUL, "OK"),
    CREATED(201, HttpState.Series.SUCCESSFUL, "Created"),
    ACCEPTED(202, HttpState.Series.SUCCESSFUL, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, HttpState.Series.SUCCESSFUL, "Non-Authoritative Information"),
    NO_CONTENT(204, HttpState.Series.SUCCESSFUL, "No Content"),
    RESET_CONTENT(205, HttpState.Series.SUCCESSFUL, "Reset Content"),
    PARTIAL_CONTENT(206, HttpState.Series.SUCCESSFUL, "Partial Content"),
    MULTI_STATUS(207, HttpState.Series.SUCCESSFUL, "Multi-Status"),
    ALREADY_REPORTED(208, HttpState.Series.SUCCESSFUL, "Already Reported"),
    IM_USED(226, HttpState.Series.SUCCESSFUL, "IM Used"),
    MULTIPLE_CHOICES(300, HttpState.Series.REDIRECTION, "Multiple Choices"),
    MOVED_PERMANENTLY(301, HttpState.Series.REDIRECTION, "Moved Permanently"),
    FOUND(302, HttpState.Series.REDIRECTION, "Found"),
    /** @deprecated */
    @Deprecated
    MOVED_TEMPORARILY(302, HttpState.Series.REDIRECTION, "Moved Temporarily"),
    SEE_OTHER(303, HttpState.Series.REDIRECTION, "See Other"),
    NOT_MODIFIED(304, HttpState.Series.REDIRECTION, "Not Modified"),
    /** @deprecated */
    @Deprecated
    USE_PROXY(305, HttpState.Series.REDIRECTION, "Use Proxy"),
    TEMPORARY_REDIRECT(307, HttpState.Series.REDIRECTION, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, HttpState.Series.REDIRECTION, "Permanent Redirect"),
    BAD_REQUEST(400, HttpState.Series.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401, HttpState.Series.CLIENT_ERROR, "Unauthorized"),
    PAYMENT_REQUIRED(402, HttpState.Series.CLIENT_ERROR, "Payment Required"),
    FORBIDDEN(403, HttpState.Series.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404, HttpState.Series.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405, HttpState.Series.CLIENT_ERROR, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, HttpState.Series.CLIENT_ERROR, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, HttpState.Series.CLIENT_ERROR, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, HttpState.Series.CLIENT_ERROR, "Request Timeout"),
    CONFLICT(409, HttpState.Series.CLIENT_ERROR, "Conflict"),
    GONE(410, HttpState.Series.CLIENT_ERROR, "Gone"),
    LENGTH_REQUIRED(411, HttpState.Series.CLIENT_ERROR, "Length Required"),
    PRECONDITION_FAILED(412, HttpState.Series.CLIENT_ERROR, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, HttpState.Series.CLIENT_ERROR, "Payload Too Large"),
    /** @deprecated */
    @Deprecated
    REQUEST_ENTITY_TOO_LARGE(413, HttpState.Series.CLIENT_ERROR, "Request Entity Too Large"),
    URI_TOO_LONG(414, HttpState.Series.CLIENT_ERROR, "URI Too Long"),
    /** @deprecated */
    @Deprecated
    REQUEST_URI_TOO_LONG(414, HttpState.Series.CLIENT_ERROR, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, HttpState.Series.CLIENT_ERROR, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, HttpState.Series.CLIENT_ERROR, "Requested range not satisfiable"),
    EXPECTATION_FAILED(417, HttpState.Series.CLIENT_ERROR, "Expectation Failed"),
    I_AM_A_TEAPOT(418, HttpState.Series.CLIENT_ERROR, "I'm a teapot"),
    /** @deprecated */
    @Deprecated
    INSUFFICIENT_SPACE_ON_RESOURCE(419, HttpState.Series.CLIENT_ERROR, "Insufficient Space On Resource"),
    /** @deprecated */
    @Deprecated
    METHOD_FAILURE(420, HttpState.Series.CLIENT_ERROR, "Method Failure"),
    /** @deprecated */
    @Deprecated
    DESTINATION_LOCKED(421, HttpState.Series.CLIENT_ERROR, "Destination Locked"),
    UNPROCESSABLE_ENTITY(422, HttpState.Series.CLIENT_ERROR, "Unprocessable Entity"),
    LOCKED(423, HttpState.Series.CLIENT_ERROR, "Locked"),
    FAILED_DEPENDENCY(424, HttpState.Series.CLIENT_ERROR, "Failed Dependency"),
    TOO_EARLY(425, HttpState.Series.CLIENT_ERROR, "Too Early"),
    UPGRADE_REQUIRED(426, HttpState.Series.CLIENT_ERROR, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, HttpState.Series.CLIENT_ERROR, "Precondition Required"),
    TOO_MANY_REQUESTS(429, HttpState.Series.CLIENT_ERROR, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, HttpState.Series.CLIENT_ERROR, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, HttpState.Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),
    INTERNAL_SERVER_ERROR(500, HttpState.Series.SERVER_ERROR, "Internal Server Error"),
    NOT_IMPLEMENTED(501, HttpState.Series.SERVER_ERROR, "Not Implemented"),
    BAD_GATEWAY(502, HttpState.Series.SERVER_ERROR, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, HttpState.Series.SERVER_ERROR, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, HttpState.Series.SERVER_ERROR, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, HttpState.Series.SERVER_ERROR, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, HttpState.Series.SERVER_ERROR, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, HttpState.Series.SERVER_ERROR, "Insufficient Storage"),
    LOOP_DETECTED(508, HttpState.Series.SERVER_ERROR, "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED(509, HttpState.Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED(510, HttpState.Series.SERVER_ERROR, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, HttpState.Series.SERVER_ERROR, "Network Authentication Required");

    private static final HttpState[] VALUES = values();
    private final int value;
    private final HttpState.Series series;
    private final String reasonPhrase;

    private HttpState(int value, HttpState.Series series, String reasonPhrase) {
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public HttpState.Series series() {
        return this.series;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public boolean is1xxInformational() {
        return this.series() == HttpState.Series.INFORMATIONAL;
    }

    public boolean is2xxSuccessful() {
        return this.series() == HttpState.Series.SUCCESSFUL;
    }

    public boolean is3xxRedirection() {
        return this.series() == HttpState.Series.REDIRECTION;
    }

    public boolean is4xxClientError() {
        return this.series() == HttpState.Series.CLIENT_ERROR;
    }

    public boolean is5xxServerError() {
        return this.series() == HttpState.Series.SERVER_ERROR;
    }

    public boolean isError() {
        return this.is4xxClientError() || this.is5xxServerError();
    }


    public static boolean isSuccess(int code) {
        return Arrays.stream(values())
                .anyMatch(item -> item.value == code && item.is2xxSuccessful());
    }

    public static boolean isFailed(int code) {
        return Arrays.stream(values())
                .anyMatch(item -> item.value == code
                        && (item.is4xxClientError() || item.is5xxServerError()));
    }

    public String toString() {
        return this.value + " " + this.name();
    }

    public static HttpState valueOf(int statusCode) {
        HttpState status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        } else {
            return status;
        }
    }

    @Nullable
    public static HttpState resolve(int statusCode) {
        HttpState[] var1 = VALUES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            HttpState status = var1[var3];
            if (status.value == statusCode) {
                return status;
            }
        }

        return null;
    }

    public static enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        private Series(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        /** @deprecated */
        @Deprecated
        public static HttpState.Series valueOf(HttpState status) {
            return status.series;
        }

        public static HttpState.Series valueOf(int statusCode) {
            HttpState.Series series = resolve(statusCode);
            if (series == null) {
                throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
            } else {
                return series;
            }
        }

        @Nullable
        public static HttpState.Series resolve(int statusCode) {
            int seriesCode = statusCode / 100;
            HttpState.Series[] var2 = values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                HttpState.Series series = var2[var4];
                if (series.value == seriesCode) {
                    return series;
                }
            }

            return null;
        }
    }
}
