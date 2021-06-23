package org.lan.iti.common.pay.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.pay.constants.PayConstants;

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
            return "签名异常,请检查私钥是否配置正确!";
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
     * 应用调用api验签方法
     *
     * @param parameters 请求参数
     * @param publicKey  验签公钥
     * @return 验签结果
     */
    public boolean verify(Map<String, Object> parameters, String publicKey) {
        if (parameters == null || parameters.isEmpty() || !parameters.containsKey(PayConstants.SIGN_SIGNATURE)) {
            return false;
        }
        String signature = Convert.toStr(parameters.get(PayConstants.SIGN_SIGNATURE));
        parameters.remove(PayConstants.SIGN_SIGNATURE);
        String content = PayCommonUtil.getSignContent(parameters);
        return verifySign(content, signature, publicKey);
    }

}
