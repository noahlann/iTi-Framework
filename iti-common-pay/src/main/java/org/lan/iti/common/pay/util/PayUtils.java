package org.lan.iti.common.pay.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付工具
 */
@UtilityClass
@Slf4j
public class PayUtils {

    /**
     * 签名
     *
     * @param content       验证签名的参数 形式为 a=x b=y
     * @param privateKeyPem 私钥签名
     * @return 签名串
     */
    public String sign(String content, String privateKeyPem) {
        try {
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            Sign sha256WithRsaSign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, privateKeyPem, null);
            byte[] signed = sha256WithRsaSign.sign(data);
            return Base64.encode(signed);
        } catch (Exception var7) {
            String errorMessage = "签名遭遇异常，content=" + content + " privateKeySize=" + privateKeyPem.length() + " reason=" + var7.getMessage();
            log.error(errorMessage, var7);
            return "签名异常,请检查私钥是否配置正确!";
//            throw new RuntimeException(errorMessage, var7);
        }
    }

    /**
     * 验签
     *
     * @param content      验证签名的参数 形为 a=x&b=y
     * @param sign         content使用私钥签名的签名串
     * @param publicKeyPem 公钥验签
     * @return 验签结果
     */
    public boolean verifySign(String content, String sign, String publicKeyPem) {
        try {
            if (StrUtil.isBlank(sign)) {
                return false;
            }
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            Sign sha256WithRsaSign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, publicKeyPem);
            return sha256WithRsaSign.verify(data, Base64.decode(sign.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception var7) {
            String errorMessage = "验签遭遇异常，content=" + content + " sign=" + sign + " publicKey=" + publicKeyPem + " reason=" + var7.getMessage();
            log.error(errorMessage, var7);
            return false;
//            throw new RuntimeException(errorMessage, var7);
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
     * 应用调用api验签方法
     *
     * @param parameters 请求参数
     * @param publicKey  验签公钥
     * @return 验签结果
     */
    public boolean verify(Map<String, String> parameters, String publicKey) {
        String sign = Convert.toStr(parameters.get("sign"));
        parameters.remove("sign");
        String content = getSignCheckContent(parameters);
        return verifySign(content, sign, publicKey);
    }

    /**
     * 将异步通知的参数转化为Map
     *
     * @param request {HttpServletRequest}
     * @return 转化后的Map
     */
    public Map<String, String> toMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
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
     * 签名
     *
     * @param params 待签名的集合
     * @return 验证签名的参数 形式为 a=x b=y
     */
    public String getSignCheckContent(Map<String, String> params) {
        if (params == null) {
            return null;
        } else {
            StringBuilder content = new StringBuilder();
            List<String> keys = new ArrayList(params.keySet());
            Collections.sort(keys);

            for (int i = 0; i < keys.size(); ++i) {
                String key = keys.get(i);
                Object value = params.get(key);
                content.append(i == 0 ? "" : "&").append(key).append("=").append(value);
            }
            return content.toString();
        }
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


}
