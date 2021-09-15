package org.lan.iti.sdk.pay.net;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Method;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.constants.PayFieldKeyConstants;

/**
 * @author I'm
 * @date 2021/9/10
 */
public class HttpPost implements IHttpRequest {

    @Override
    public HttpUriRequest getHttpUriRequest(String appId, String authorization, String url, String body) {
        //声明请求方式
        return RequestBuilder.create(Method.POST.name())
                .setUri(url)
                .addHeader(PayConstants.HTTP_HEADER_AUTHORIZATION, authorization)
                //设置content-Type
                .addHeader(PayConstants.CONTENT_TYPE, ContentType.JSON.getValue())
                .addParameter(PayFieldKeyConstants.APP_ID, appId)
                //声明携带参数
                //设置请求 参数的编码格式
                .setEntity(new StringEntity(body, CharsetUtil.UTF_8))
                .build();
    }

}
