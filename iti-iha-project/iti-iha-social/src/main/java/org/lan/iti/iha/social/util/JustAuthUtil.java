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

package org.lan.iti.iha.social.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthToken;

/**
 * @author NorthLan
 * @date 2021/8/17
 * @url https://blog.noahlan.com
 */
@UtilityClass
@Slf4j
public class JustAuthUtil {
    private static ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return objectMapper;
    }

    public static String toJsonString(AuthToken authToken) {
        ObjectMapper objectMapper = createMapper();
        try {
            return objectMapper.writeValueAsString(authToken);
        } catch (JsonProcessingException e) {
            log.error("cannot convert {} to json string.", authToken.getClass().getName(), e);
            return null;
        }
    }
}
