package org.lan.iti.sdk.pay.net;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.Method;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.constants.PayFieldKeyConstants;
import org.lan.iti.common.pay.util.PayCommonUtil;
import org.lan.iti.common.pay.util.SignUtil;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author I'm
 * @date 2021/9/10
 */
@UtilityClass
public class HttpRequestHelper {

    private static final Map<String, IHttpRequest> httpRequestMap = new HashMap<>();

    static {
        httpRequestMap.put(Method.POST.name(), new HttpPost());
        httpRequestMap.put(Method.GET.name(), new HttpGet());
    }

    public HttpResult request(String method, String url, String appId, String primaryKey, String body) {
        if (StrUtil.equals(method, Method.GET.name())) {
            body = "";
        }
        String authorization = getAuthorization(method, URLUtil.url(url), appId, primaryKey, body);
        return httpRequestMap.get(method).request(appId, authorization, url, body);
    }

    // Authorization: ONEPAY-SHA256-RSA2048 getAuthorization
    // GET - getAuthorization("GET", httpurl, "")
    // POST - getAuthorization("POST", httpurl, json)
    String getAuthorization(String method, URL url, String appId, String primaryKey, String body) {
        long timestamp = System.currentTimeMillis() / 1000;
        String nonceStr = RandomUtil.randomString(32);
        String message = PayCommonUtil.buildSignMessage(method, url, Convert.toStr(timestamp), nonceStr, body);
        String signature = SignUtil.sign(message, primaryKey);
        return PayConstants.ONE_PAY_SCHEMA_VALUE + " " +
                PayFieldKeyConstants.APP_ID + PayConstants.SYMBOL_EQUAL + PayConstants.SYMBOL_QUOTES + appId + PayConstants.SYMBOL_QUOTES + PayConstants.SYMBOL_COMMA +
                PayConstants.SIGN_NONCE_STR + PayConstants.SYMBOL_EQUAL + PayConstants.SYMBOL_QUOTES + nonceStr + PayConstants.SYMBOL_QUOTES + PayConstants.SYMBOL_COMMA +
                PayConstants.SIGN_TIMESTAMP + PayConstants.SYMBOL_EQUAL + PayConstants.SYMBOL_QUOTES + timestamp + PayConstants.SYMBOL_QUOTES + PayConstants.SYMBOL_COMMA +
                PayConstants.SIGN_SIGNATURE + PayConstants.SYMBOL_EQUAL + PayConstants.SYMBOL_QUOTES + signature + PayConstants.SYMBOL_QUOTES + PayConstants.SYMBOL_COMMA;
    }

}
