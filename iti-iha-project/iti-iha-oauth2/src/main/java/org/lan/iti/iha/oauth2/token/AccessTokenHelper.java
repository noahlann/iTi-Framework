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

package org.lan.iti.iha.oauth2.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.StringPool;

import java.util.HashMap;
import java.util.Map;

/**
 * AccessToken转换工具类
 *
 * @author NorthLan
 * @date 2021/8/16
 * @url https://blog.noahlan.com
 */
@UtilityClass
@Slf4j
public class AccessTokenHelper {

    private static ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static AccessToken toAccessToken(String jsonStr) {
        ObjectMapper objectMapper = createMapper();
        AccessToken result = null;
        try {
            result = objectMapper.readValue(jsonStr, AccessToken.class);
        } catch (JsonProcessingException e) {
            log.warn("serialize to AccessToken failed.", e);
        }
        return result;
    }

    public static AccessToken toAccessToken(Map<String, Object> map) {
        ObjectMapper objectMapper = createMapper();
        AccessToken result = null;
        try {
            result = objectMapper.convertValue(map, AccessToken.class);
        } catch (IllegalArgumentException e) {
            log.warn("deserialize to AccessToken failed.", e);
        }
        return result;
    }

    public static Map<String, Object> toMap(AccessToken accessToken) {
        ObjectMapper objectMapper = createMapper();
        Map<String, Object> result;
        try {
            result = objectMapper.convertValue(accessToken, new TypeReference<Map<String, Object>>() {
            });
        } catch (IllegalArgumentException e) {
            log.warn("deserialize to Map<String, Object> failed.", e);
            result = new HashMap<>();
        }
        return result;
    }

    public static String toJsonString(AccessToken accessToken) {
        ObjectMapper objectMapper = createMapper();
        String result = StringPool.EMPTY;
        try {
            result = objectMapper.writeValueAsString(accessToken);
        } catch (JsonProcessingException e) {
            log.warn("writeValueAsString failed.", e);
        }
        return result;
    }
}
