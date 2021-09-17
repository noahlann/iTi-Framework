package org.lan.iti.sdk.pay.util;

import lombok.experimental.UtilityClass;
import org.lan.iti.sdk.pay.net.HttpRequestHelper;
import org.lan.iti.sdk.pay.net.HttpResult;

/**
 * @author I'm
 * @since 2021/3/27
 * description http 工具
 */
@UtilityClass
public class HttpUtil {

    public HttpResult request(String method, String url, String appId, String primaryKey, String body) {
        return HttpRequestHelper.request(method, url, appId, primaryKey, body);
    }

}
