package org.lan.iti.common.pay.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.exception.BusinessException;
import org.lan.iti.common.pay.reason.SignExceptionReason;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author I'm
 * @date 2021/6/21
 * description 签名工具
 */
@UtilityClass
@Slf4j
public class SignUtil {

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
            throw BusinessException.withReason(SignExceptionReason.Configure.SIGN_FAIL);
//            throw new RuntimeException(errorMessage, var7);
        }
    }

    /**
     * 签名
     *
     * @param map        Map<String, Object> 签名参数集合
     * @param primaryKey 签名私钥(RSA)
     * @return signature
     */
    public String sign(Map<String, Object> map, String primaryKey) {
        return sign(PayCommonUtil.getSignContent(map), primaryKey);
    }

    /**
     * 验签
     *
     * @param content      验证签名的参数 形为 a=x&b=y
     * @param signature    content使用私钥签名的签名串
     * @param publicKeyPem 公钥验签
     * @return 验签结果
     */
    public boolean verifySign(String publicKeyPem, String content, String signature) {
        try {
            if (StrUtil.isBlank(signature)) {
                return false;
            }
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            Sign sha256WithRsaSign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, publicKeyPem);
            return sha256WithRsaSign.verify(data, Base64.decode(signature.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            String errorMessage = "验签遭遇异常，content=" + content + " sign=" + signature + " publicKey=" + publicKeyPem + " reason=" + e.getMessage();
            log.error(errorMessage, e);
            throw BusinessException.withReason(SignExceptionReason.Configure.SIGN_VERIFY_FAIL);
        }
    }

    /**
     * 应用调用api验签方法
     *
     * @param publicKey 验签公钥
     * @param method    请求方法
     * @param url       请求url
     * @param timestamp 请求的时间戳
     * @param nonceStr  随机串
     * @param body      请求数据内容
     * @param signature 请求签名
     * @return 验签结果
     */
    public boolean verify(String publicKey, String method, String url, String timestamp, String nonceStr, String signature, String body) {
        String message = PayCommonUtil.buildSignMessage(method, URLUtil.url(url), timestamp, nonceStr, body);
        return verifySign(publicKey, message, signature);
    }

}
