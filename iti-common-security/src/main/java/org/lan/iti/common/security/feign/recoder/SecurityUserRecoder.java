/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.security.feign.recoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.feign.recoder.ApiResultGenericRecoder;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.security.model.SecurityUser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 用户信息 Recode
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Component
@AllArgsConstructor
@Slf4j
public class SecurityUserRecoder implements ApiResultGenericRecoder {
    private final ObjectMapper objectMapper;

    @Override
    public boolean support(Class<?> clazz) {
        return clazz.isAssignableFrom(SecurityUser.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(ApiResult<?> result, Response response, Type type, Class<?> clazz) {
        Object data = result.getData();
        if (data instanceof SecurityUser<?>) {
            Optional<String> header = getGenericHeader(response);
            header.ifPresent(it -> {
                Class<?> typedObject = null;
                try {
                    typedObject = Class.forName(it);
                } catch (ClassNotFoundException e) {
                    log.error("未找到指定类: [{}]", it);
                    // do nothing...
                }
                Object accountInfo = ((SecurityUser<Object>) data).getAccountInfo();
                ((SecurityUser<Object>) data).setAccountInfo(objectMapper.convertValue(accountInfo, typedObject));
            });
        }
    }
}
