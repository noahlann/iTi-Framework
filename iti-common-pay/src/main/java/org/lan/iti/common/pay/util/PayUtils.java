package org.lan.iti.common.pay.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付工具
 */
@UtilityClass
@Slf4j
public class PayUtils {

    public String sign(String content, String privateKeyPem) {
        try {
            byte[] encodedKey = privateKeyPem.getBytes();
            encodedKey = Base64.decode(encodedKey);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(privateKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (Exception var7) {
            String errorMessage = "签名遭遇异常，content=" + content + " privateKeySize=" + privateKeyPem.length() + " reason=" + var7.getMessage();
            log.error(errorMessage, var7);
            throw new RuntimeException(errorMessage, var7);
        }
    }

    public boolean verifySign(String content, String sign, String publicKeyPem) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = publicKeyPem.getBytes();
            encodedKey = Base64.decode(encodedKey);
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(publicKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decode(sign.getBytes()));
        } catch (Exception var7) {
            String errorMessage = "验签遭遇异常，content=" + content + " sign=" + sign + " publicKey=" + publicKeyPem + " reason=" + var7.getMessage();
            log.error(errorMessage, var7);
            throw new RuntimeException(errorMessage, var7);
        }
    }

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
        log.debug("Request Message:[" + reqstr + "]");
        return reqstr;
    }


}
