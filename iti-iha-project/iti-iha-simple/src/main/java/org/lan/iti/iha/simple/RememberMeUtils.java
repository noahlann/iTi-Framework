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

package org.lan.iti.iha.simple;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.util.StringPool;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * @author NorthLan
 * @date 2021-07-07
 * @url https://noahlan.com
 */
@UtilityClass
public class RememberMeUtils {
    private static final String DEFAULT_DELIMITER = StringPool.COLON;

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
     * @param maxAge    maxAge
     * @param salt      md5 salt
     * @param principal principal
     * @return RememberMeDetails
     */
    public static RememberMeDetails encode(long maxAge, String salt, String principal, String type) {
        long expiryTime = System.currentTimeMillis() + maxAge;
        // principal:type:tokenExpiryTime
        String md5Data = principal + DEFAULT_DELIMITER + type + DEFAULT_DELIMITER + expiryTime;
        String md5Key = digestHex16(salt, md5Data);
        // principal:type:tokenExpiryTime:key
        String base64Data = md5Data + DEFAULT_DELIMITER + md5Key;
        return RememberMeDetails.builder()
                .principal(principal)
                .expiryTime(expiryTime)
                .type(type)
                .encoded(Base64.encode(base64Data))
                .build();
    }

    /**
     * Decryption acquisition instance.
     * <p>
     * principal:type:tokenExpiryTime:key
     *
     * @param salt        MD5 salt
     * @param cookieValue cookie value
     * @return RememberMeDetails
     */
    public static RememberMeDetails decode(String salt, String cookieValue) throws SecurityException {
        String base64DecodeValue;
        try {
            base64DecodeValue = Base64.decodeStr(cookieValue);
        } catch (RuntimeException e) {
            throw new SecurityException("Illegal remember me cookie.");
        }
        String[] base64DecodeValueSplitArray = StrUtil.splitToArray(base64DecodeValue, DEFAULT_DELIMITER);
        // Check and validate keys
        if (base64DecodeValueSplitArray.length > 3) {
            String principal = base64DecodeValueSplitArray[0];
            String type = base64DecodeValueSplitArray[1];
            long expiryTime;
            try {
                expiryTime = Long.parseLong(base64DecodeValueSplitArray[2]);
            } catch (RuntimeException e) {
                return null;
            }
            // overdue
            if (expiryTime < System.currentTimeMillis()) {
                return null;
            }
            // principal:tokenExpiryTime
            String md5Data = principal + DEFAULT_DELIMITER + type + DEFAULT_DELIMITER + expiryTime;
            String md5Key = digestHex16(salt, md5Data);
            // Check pass returns
            if (ObjectUtil.equal(md5Key, base64DecodeValueSplitArray[3])) {
                return RememberMeDetails.builder()
                        .principal(principal)
                        .expiryTime(expiryTime)
                        .type(type)
                        .encoded(cookieValue)
                        .build();
            }
        }
        return null;
    }
}
