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

package org.lan.iti.common.core.feign.decoder;

import feign.Response;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.model.response.ApiResult;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

/**
 * ApiResult的泛型重编码接口
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
public interface ApiResultGenericRecoder {
    /**
     * 重编码实现支持情况
     *
     * @param type Feign返回类型推断
     */
    boolean support(Type type);

    /**
     * 重编码处理
     *
     * @param result   未编码数据
     * @param response Feign返回对象
     * @param type     Feign返回类型推断
     */
    void process(ApiResult<?> result, Response response, Type type);

    /**
     * 获取
     *
     * @param response Feign返回对象
     * @return Optional-Header
     */
    default Optional<String> getGenericHeader(Response response) {
        Collection<String> headers = response.headers().getOrDefault(ITIConstants.FEIGN_GENERICS_HEADER_NAME, null);
        if (CollectionUtils.isEmpty(headers)) {
            return Optional.empty();
        }
        return headers.stream().findFirst();
    }
}
