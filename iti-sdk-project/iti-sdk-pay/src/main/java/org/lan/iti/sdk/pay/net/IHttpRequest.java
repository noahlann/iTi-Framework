package org.lan.iti.sdk.pay.net;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.lan.iti.common.core.exception.BusinessException;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.reason.HttpExceptionReason;

import java.io.IOException;

/**
 * @author I'm
 * @date 2021/9/10
 */
public interface IHttpRequest {

    HttpUriRequest getHttpUriRequest(String appId, String authorization, String url, String body);

    default HttpResult request(String appId, String authorization, String url, String body) {
        String entityString;
        HttpEntity httpEntity;
        CloseableHttpResponse response = null;
        HttpResult httpResult = new HttpResult();
        //创建http client请求对象
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(PayConstants.HTTP_TIMEOUT).setSocketTimeout(PayConstants.HTTP_TIMEOUT)
                .setConnectionRequestTimeout(PayConstants.HTTP_TIMEOUT).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(defaultRequestConfig).build();
        //执行请求
        try {
            response = httpClient.execute(getHttpUriRequest(appId, authorization, url, body));
            httpResult.setCode(response.getStatusLine().getStatusCode());
            //解析返回值
            httpEntity = response.getEntity();
            entityString = EntityUtils.toString(httpEntity);
            httpResult.setData(entityString);
        } catch (
                IOException e) {
            e.printStackTrace();
            throw BusinessException.withReason(HttpExceptionReason.Configure.HTTP_ERROR);
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return httpResult;
    }

}
