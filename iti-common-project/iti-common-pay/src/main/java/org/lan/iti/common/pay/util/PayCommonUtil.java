package org.lan.iti.common.pay.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付工具
 */
@UtilityClass
@Slf4j
public class PayCommonUtil {

    /**
     * 签名
     *
     * @param params 待签名的集合
     * @return 验证签名的参数 形式为 a=x b=y
     */
    public String getSignContent(Map<String, Object> params) {
        if (params == null) {
            return null;
        } else {
            StringBuilder content = new StringBuilder();
            List<String> keys = new ArrayList<>(params.keySet());
            Collections.sort(keys);
            for (int i = 0; i < keys.size(); ++i) {
                String key = keys.get(i);
                if (!StrUtil.isBlankIfStr(params.get(key))) {
                    String value = Convert.toStr(params.get(key));
                    content.append(key).append("=").append(value).append("\n");
                }
            }
            return content.toString();
        }
    }

    /**
     * 检查给定的key应用的value是否为空
     *
     * @param key map的指定key
     * @param map map
     * @return 验证结果
     */
    public boolean isKeyValueNotBlankOfMapString(Map<String, String> map, String key) {
        return map.containsKey(key) && StrUtil.isNotBlank(key);
    }

    /**
     * 检查给定的key应用的value是否为空
     *
     * @param key map的指定key
     * @param map map
     * @return 验证结果
     */
    public boolean isKeyValueBlankOfMapString(Map<String, String> map, String key) {
        return !map.containsKey(key) || StrUtil.isBlank(key);
    }

    /**
     * 将异步通知的参数转化为Map
     *
     * @param request {HttpServletRequest}
     * @return 转化后的Map
     */
    public Map<String, Object> toMap(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    /**
     * 将Map存储的对象，转换为key=value&key=value的字符
     *
     * @param requestParam 待转换map
     * @param coder        编码
     * @return 转换后的字符
     */
    public String getRequestParamString(Map<String, String> requestParam, String coder) {
        if (null == coder || "".equals(coder)) {
            coder = "UTF-8";
        }
        StringBuilder sf = new StringBuilder();
        String reqstr = "";
        if (null != requestParam && 0 != requestParam.size()) {
            for (Map.Entry<String, String> en : requestParam.entrySet()) {
                try {
                    sf.append(en.getKey()).append("=").append(null == en.getValue() || "".equals(en.getValue()) ? "" : URLEncoder
                            .encode(en.getValue(), coder)).append("&");
                } catch (UnsupportedEncodingException e) {
                    log.error(e.getMessage(), e);
                    return "";
                }
            }
            reqstr = sf.substring(0, sf.length() - 1);
        }
//        log.debug("Request Message:[" + reqstr + "]");
        return reqstr;
    }

    /**
     * 检查Http URL格式
     *
     * @param httpUrl Http URL
     * @return 检查结果
     */
    public boolean isHttpUrlPatternCorrect(String httpUrl) {
        return ReUtil.isMatch(PatternPool.URL_HTTP, httpUrl);
    }

    /**
     * object map to string map
     */
    public Map<String, String> objectMap2StringMap(Map<String, Object> objectMap) {
        Map<String, String> stringObjectMap = new HashMap<>(objectMap.size());
        for (String key : objectMap.keySet()) {
            stringObjectMap.put(key, Convert.toStr(key));
        }
        return stringObjectMap;
    }

    /**
     * string map to object map
     */
    public Map<String, Object> stringMap2ObjectMap(Map<String, String> stringObjectMap) {
        Map<String, Object> objectMap = new HashMap<>(stringObjectMap.size());
        for (String key : stringObjectMap.keySet()) {
            objectMap.put(key, stringObjectMap.get(key));
        }
        return objectMap;
    }

    public boolean isAmountEquals(Double totalAmount, Double notifyAmount) {
        if (!StrUtil.isBlankIfStr(totalAmount) && !StrUtil.isBlankIfStr(notifyAmount)) {
            BigDecimal totalAmountBigDecimal = new BigDecimal(totalAmount);
            BigDecimal notifyAmountBigDecimal = new BigDecimal(notifyAmount);
            return totalAmountBigDecimal.compareTo(notifyAmountBigDecimal) != 0;
        } else {
            return true;
        }
    }

}
