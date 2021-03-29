package org.lan.iti.sdk.pay.net;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.ContentType;
import com.alibaba.fastjson.JSON;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.lan.iti.common.pay.constants.PayConstants;

import java.io.IOException;
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
        //声明携带参数
        String jsonStr = JSON.toJSONString(requestMap);
        //将参数设置到请求对象中
        StringEntity stringEntity = new StringEntity(jsonStr, CharsetUtil.UTF_8);
        //设置请求 参数的编码格式
        httpPost.setEntity(stringEntity);
        //设置content-Type
        httpPost.setHeader(PayConstants.CONTENT_TYPE, ContentType.FORM_URLENCODED.getValue());
        try {
            //执行请求
            closeableHttpResponse = closeableHttpClient.execute(httpPost);
            //解析返回值
            httpEntity = closeableHttpResponse.getEntity();
            System.out.println(httpEntity.getContent());

        } catch (IOException e) {
            e.printStackTrace();
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
