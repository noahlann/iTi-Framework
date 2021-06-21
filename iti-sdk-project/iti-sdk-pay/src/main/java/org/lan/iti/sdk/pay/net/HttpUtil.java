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
import org.apache.http.util.EntityUtils;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.enums.ErrorLevelEnum;
import org.lan.iti.common.pay.exception.ServiceException;

import java.io.IOException;
import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description http 工具
 */
@UtilityClass
public class HttpUtil {

    public String request(String url, Map<String, Object> requestMap) {
        String entityString;
        HttpEntity httpEntity;
        CloseableHttpResponse closeableHttpResponse = null;
        //创建http client请求对象
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
//        if (!requestMap.containsKey(PayFieldNameConstants.GATEWAY_HOST) || StrUtil.isBlankIfStr(requestMap.get(PayFieldNameConstants.GATEWAY_HOST))) {
//            throw new BusinessException(ValidatePaymentParamException.GatewayHostException.EMPTY_GATEWAY_HOST);
//        }
//        if (!PayCommonUtil.isHttpUrlPatternCorrect(Convert.toStr(requestMap.get(PayFieldNameConstants.GATEWAY_HOST)))) {
//            throw new BusinessException(ValidatePaymentParamException.GatewayHostException.INVALID_GATEWAY_HOST_PATTERN);
//        }
        //声明请求方式
        HttpPost httpPost = new HttpPost(url);
        //设置content-Type
        httpPost.setHeader(PayConstants.CONTENT_TYPE, ContentType.JSON.getValue());
        //声明携带参数
        //设置请求 参数的编码格式
        httpPost.setEntity(new StringEntity(JSON.toJSONString(requestMap), CharsetUtil.UTF_8));
        //执行请求
        try {
            closeableHttpResponse = closeableHttpClient.execute(httpPost);
            //解析返回值
            httpEntity = closeableHttpResponse.getEntity();
            entityString = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(2000, ErrorLevelEnum.PRIMARY.getValue(), PayConstants.HTTP_ERROR);
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
        return entityString;
    }

}
