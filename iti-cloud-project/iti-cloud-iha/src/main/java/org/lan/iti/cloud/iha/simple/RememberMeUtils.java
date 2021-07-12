/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.lan.iti.cloud.iha.simple;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import lombok.experimental.UtilityClass;
import org.lan.iti.cloud.iha.core.exception.IhaException;
import org.lan.iti.cloud.iha.core.result.IhaErrorCode;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static org.lan.iti.cloud.iha.core.IhaConstants.DEFAULT_DELIMITER;

/**
 * @author NorthLan
 * @date 2021-07-07
 * @url https://noahlan.com
 */
@UtilityClass
public class RememberMeUtils {

    private static String digestHex16(String credentialEncryptSalt, String data) {
        MD5 md5 = new MD5(credentialEncryptSalt.getBytes(StandardCharsets.UTF_8));
        return md5.digestHex16(data);
    }

    /**
     * Credential encryption algorithm: MD5 encryption
     *
     * @param request      request
     * @param simpleConfig simpleConfig
     * @return boolean
     */
    public static boolean enableRememberMe(HttpServletRequest request, SimpleConfig simpleConfig) {
        return BooleanUtil.toBoolean(request.getParameter(simpleConfig.getRememberMeField()));
    }

    /**
     * Encrypted acquisition instance.
     *
     * @param simpleConfig config
     * @param principal    principal
     * @return RememberMeDetails
     */
    public static RememberMeDetails encode(SimpleConfig simpleConfig, String principal) {
        long expiryTime = System.currentTimeMillis() + simpleConfig.getRememberMeCookieMaxAge();
        // principal:type:tokenExpiryTime
        String md5Data = principal + DEFAULT_DELIMITER + expiryTime;
        String md5Key = digestHex16(simpleConfig.getCredentialEncryptSalt(), md5Data);
        // principal:tokenExpiryTime:key
        String base64Data = md5Data + DEFAULT_DELIMITER + md5Key;
        return RememberMeDetails.builder()
                .principal(principal)
                .expiryTime(expiryTime)
                .encoded(Base64.encode(base64Data))
                .build();
    }

    /**
     * Decryption acquisition instance.
     *
     * @param simpleConfig config
     * @param cookieValue  cookie value
     * @return RememberMeDetails
     */
    public static RememberMeDetails decode(SimpleConfig simpleConfig, String cookieValue) throws IhaException {
        String base64DecodeValue;
        try {
            base64DecodeValue = Base64.decodeStr(cookieValue);
        } catch (RuntimeException e) {
            throw new IhaException(IhaErrorCode.INVALID_REMEMBER_ME_COOKIE);
        }
        String[] base64DecodeValueSplitArray = StrUtil.splitToArray(base64DecodeValue, DEFAULT_DELIMITER);
        // Check and validate keys
        if (base64DecodeValueSplitArray.length > 2) {
            String principal = base64DecodeValueSplitArray[0];
            long expiryTime;
            try {
                expiryTime = Long.parseLong(base64DecodeValueSplitArray[1]);
            } catch (RuntimeException e) {
                return null;
            }
            // overdue
            if (expiryTime < System.currentTimeMillis()) {
                return null;
            }
            // principal:tokenExpiryTime
            String md5Data = principal + DEFAULT_DELIMITER + expiryTime;
            String md5Key = digestHex16(simpleConfig.getCredentialEncryptSalt(), md5Data);
            // Check pass returns
            if (ObjectUtil.equal(md5Key, base64DecodeValueSplitArray[2])) {
                return RememberMeDetails.builder()
                        .principal(principal)
                        .expiryTime(expiryTime)
                        .encoded(cookieValue)
                        .build();
            }
        }
        return null;
    }
}
