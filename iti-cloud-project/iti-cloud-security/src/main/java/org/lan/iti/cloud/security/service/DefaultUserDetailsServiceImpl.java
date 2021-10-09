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

package org.lan.iti.cloud.security.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.iha.security.exception.authentication.UnknownAccountException;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.security.userdetails.UserDetailsService;

import java.util.Map;

/**
 * @author NorthLan
 * @date 2021/9/28
 * @url https://blog.noahlan.com
 */
@Slf4j
public class DefaultUserDetailsServiceImpl implements UserDetailsService {
    private static ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Override
    public UserDetails fromToken(String type, Object token, Map<String, Object> userInfo) throws UnknownAccountException {
        ObjectMapper objectMapper = createMapper();
        UserDetails result = null;
        try {
            result = objectMapper.convertValue(userInfo, UserDetails.class);
        } catch (IllegalArgumentException e) {
            log.error("deserialize to UserDetails failed.", e);
        }
        return result;
    }

    @Override
    public boolean matches(Object params) {
        // 避免被Extension机制获取
        return false;
    }
}
