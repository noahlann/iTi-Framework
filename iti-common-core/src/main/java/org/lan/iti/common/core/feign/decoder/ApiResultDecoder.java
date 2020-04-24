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

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.AllArgsConstructor;
import org.lan.iti.common.model.response.ApiResult;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * ApiResult 解码器
 * {@link ApiResult}
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class ApiResultDecoder implements Decoder {
    private Decoder decoder;
    private List<ApiResultGenericRecoder> genericRecodes;

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        ApiResult<?> returnObject;
        if (isParameterizeApiResult(type)) {
            // 实际传递数据为整个ApiResult->Json化的值
            returnObject = (ApiResult<?>) this.decoder.decode(response, type);
        } else if (isApiResult(type)) {
            returnObject = createApiResult(null, response);
        } else {
            // 其它类型，走原有逻辑
            return this.decoder.decode(response, type);
        }
        // 重编码
        if (returnObject != null && returnObject.getData() != null) {
            for (ApiResultGenericRecoder it : genericRecodes) {
                Class<?> clazz = (Class<?>) ((ParameterizedType)
                        ((ParameterizedType) type).getActualTypeArguments()[0]).getRawType();
                if (it.support(clazz)) {
                    it.process(returnObject, response, type, clazz);
                }
            }
        }
        return returnObject;
    }

    /**
     * 是否为参数化的 ApiResult<*>
     *
     * @param type 类型
     */
    private boolean isParameterizeApiResult(Type type) {
        if (type instanceof ParameterizedType) {
            return isApiResult(((ParameterizedType) type).getRawType());
        }
        return false;
    }

    /**
     * 类型是否为ApiResult
     *
     * @param type 类型
     */
    private boolean isApiResult(Type type) {
        if (type instanceof Class<?>) {
            return ApiResult.class.isAssignableFrom((Class<?>) type);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T> ApiResult<T> createApiResult(Object instance, Response response) {
//        Map<String, Collection<String>> headers = response.headers();
//        for (String it : headers.keySet()) {
//
//        }
        return ApiResult.<T>builder()
                .data((T) instance)
                .build();
    }
}
