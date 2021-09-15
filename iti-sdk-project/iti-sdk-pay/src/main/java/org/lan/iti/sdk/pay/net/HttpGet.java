package org.lan.iti.sdk.pay.net;

import cn.hutool.http.Method;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.constants.PayFieldKeyConstants;

/**
 * @author I'm
 * @date 2021/9/10
 */
public class HttpGet implements IHttpRequest {

    @Override
    public HttpUriRequest getHttpUriRequest(String appId, String authorization, String url, String body) {
        //声明请求方式
        //设置content-Type
        return RequestBuilder.create(Method.GET.name())
                .setUri(url)
                .addHeader(PayConstants.HTTP_HEADER_AUTHORIZATION, authorization)
                .addParameter(PayFieldKeyConstants.APP_ID, appId).build();
    }
}
