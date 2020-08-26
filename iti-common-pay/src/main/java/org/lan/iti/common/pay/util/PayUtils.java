package org.lan.iti.common.pay.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付工具
 */
public class PayUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayUtils.class);

    public static String sign(String content, String privateKeyPem) {
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
            LOGGER.error(errorMessage, var7);
            throw new RuntimeException(errorMessage, var7);
        }
    }

    public static String parseSignContent(Map<String, Object> map) {
        StringBuilder content = new StringBuilder();
        int index = 0;

        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            Map.Entry<String, String> pair = (Map.Entry) stringObjectEntry;
            if (StrUtil.isNotBlank(pair.getKey()) && StrUtil.isNotBlank(Convert.toStr(pair.getValue()))) {
                content.append(index == 0 ? "" : "&").append(pair.getKey()).append("=").append(Convert.toStr(pair.getValue()));
                ++index;
            }
        }
        return content.toString();
    }
}
