package com.facade.edi.starter.service.impl;

import com.facade.edi.starter.annotation.EnableEdiApiScan;
import com.facade.edi.starter.request.HttpApiRequest;
import com.facade.edi.starter.response.HttpApiResponse;
import com.facade.edi.starter.service.AbstractInvokeHttpFacade;
import com.facade.edi.starter.util.MapUtil;
import com.facade.edi.starter.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * java native http调用实现
 *
 * @author typhoon
 *
 */
public class NativeClientInvokeHttpFacade extends AbstractInvokeHttpFacade {

    private final SSLSocketFactory sslSocketFactory;

    private static final int CONNECT_TIMEOUT = 60000;

    @Value("${edi.timeout:6000}")
    private int timeout;

    public NativeClientInvokeHttpFacade(SSLSocketFactory sslSocketFactory) {
        super();
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public HttpApiResponse invoke(HttpApiRequest request) {
        log.info("NativeClientInvokeHttpFacade.invoke request={}",request);
        HttpApiResponse resp = new HttpApiResponse();
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {

            URL realUrl = this.buildURL(request);
            connection = (HttpURLConnection) realUrl.openConnection();

            // trust-https
            boolean useHttps = request.getUrl().startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                https.setSSLSocketFactory(sslSocketFactory);
            }

            // connection setting
            connection.setRequestMethod(request.getHttpMethod());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout);
            connection.setConnectTimeout(timeout);
            connection.setRequestProperty("connection", "Keep-Alive");
            this.buildHeaders(connection,request);
            //connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

            // do connection
            connection.connect();

            // write requestBody

            String requestBody = this.buildRequestBody(request);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(requestBody.getBytes(ENCODING));
            dataOutputStream.flush();
            dataOutputStream.close();

            // valid StatusCode
            int statusCode = connection.getResponseCode();
            resp.setHttpCode(statusCode);

            // result
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), ENCODING));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            //String resultJson = result.toString();
            resp.setResponse(result.toString());

        } catch (Exception e) {
            log.error("NativeClientInvokeHttpFacade.invokeResponse http invoke error;url={}",request.getUrl(), e);
            resp.setHttpCode(500);
            resp.setHttpErrorMsg(e.getMessage());
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            try {
                if (null != connection) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                log.error("NativeClientInvokeHttpFacade invoke disconnect connection occur error;url={},e",request.getUrl(),e);
            }
        }
        return resp;
    }

    @Override
    public void preheat(String host) {
        log.info("NativeClientInvokeHttpFacade.preheat trigger preheat;host={}",host);
        HttpURLConnection connection = null;
        try {
            URL url = new URL(host);
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为 HEAD 减少数据传输
            // trust-https
            boolean useHttps = host.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                https.setSSLSocketFactory(sslSocketFactory);
            }
            // connection setting
            connection.setRequestMethod("HEAD");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout);
            connection.setConnectTimeout(timeout);
            connection.setRequestProperty("connection", "Keep-Alive");

            connection.getResponseCode();
        } catch (Exception e) {
            log.info("NativeClientInvokeHttpFacade.preheat trigger preheat;host={}",host);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }

    }

    private void buildHeaders(HttpURLConnection connection, HttpApiRequest request) {
        if(MapUtil.isNotEmpty(request.getHeaders())) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }
        }
    }

    private URL buildURL(HttpApiRequest request) throws MalformedURLException {
        String uri = request.getUrl();
        Map<String,String> params = request.getParams();
        boolean isFirstParam = !uri.contains("?");
        if(MapUtil.isNotEmpty(params)) {
            StringBuilder builder = new StringBuilder();
            if(isFirstParam) {
                builder.append("?");
            } else {
                builder.append("&");
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
            uri = uri + builder.substring(0,builder.length() - 1);
        }

        return new URL(uri);
    }


    private String buildRequestBody(HttpApiRequest request) {
        if(StringUtil.isNotBlank(request.getBody())) {
            return request.getBody();
        } else if (MapUtil.isNotEmpty(request.getFields())) {
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : request.getFields().entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(param.getKey());
                postData.append('=');
                postData.append(param.getValue());
            }
            return postData.toString();
        }
        return "";
    }

    @Override
    public EnableEdiApiScan.ClientType getClientType() {
        return EnableEdiApiScan.ClientType.NATIVE;
    }
}
