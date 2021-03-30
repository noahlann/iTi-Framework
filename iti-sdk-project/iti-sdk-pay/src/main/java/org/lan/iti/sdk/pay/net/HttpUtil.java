package org.lan.iti.sdk.pay.net;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.enums.ErrorLevelEnum;
import org.lan.iti.common.pay.exception.ServiceException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description http 工具
 */
@UtilityClass
public class HttpUtil {

    public HttpEntity request(String url, Map<String, Object> requestMap) {
        HttpEntity httpEntity = null;
        CloseableHttpResponse closeableHttpResponse = null;
        //创建http client请求对象
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
//        if (!map.containsKey(PayFieldNameConstants.GATEWAY_HOST) || StrUtil.isBlankIfStr(map.get(PayFieldNameConstants.GATEWAY_HOST))) {
//            throw new ServiceException(600, ErrorLevelEnum.PRIMARY.getValue(), PayParamErrorConstants.GATEWAY_HOST_EMPTY);
//        }
//        if (!PayCommonUtil.isHttpUrlPatternCorrect(Convert.toStr(map.get(PayFieldNameConstants.GATEWAY_HOST)))) {
//            throw new ServiceException(600, ErrorLevelEnum.PRIMARY.getValue(), PayParamErrorConstants.GATEWAY_HOST_PATTERN);
//        }
        //声明请求方式
        HttpPost httpPost = new HttpPost(url);
        //设置content-Type
        httpPost.setHeader(PayConstants.CONTENT_TYPE, ContentType.FORM_URLENCODED.getValue());
        //声明携带参数
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        //将参数设置到请求对象中
        if (!requestMap.isEmpty()) {
            for (String key : requestMap.keySet()) {
                if (requestMap.get(key) != null && !StrUtil.isBlankIfStr(requestMap.get(key))) {
                    nameValuePairList.add(new BasicNameValuePair(key, Convert.toStr(requestMap.get(key))));
                }
            }
        }
        //设置请求 参数的编码格式
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, CharsetUtil.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(1000, ErrorLevelEnum.PRIMARY.getValue(),"http请求的编码格式有误!");
        }
        //执行请求
        try {
            closeableHttpResponse = closeableHttpClient.execute(httpPost);
            //解析返回值
            httpEntity = closeableHttpResponse.getEntity();
            System.out.println(EntityUtils.toString(httpEntity));
        } catch (IOException e) {
            throw new ServiceException(2000, ErrorLevelEnum.PRIMARY.getValue(),"http请求异常!");
        } finally {
            try {
                closeableHttpClient.close();
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return httpEntity;
    }

}
