/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.feign.fallback;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.Formatter;
import org.lan.iti.common.core.api.ApiResult;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 统一Fallback
 *
 * @author NorthLan
 * @date 2020-04-22
 * @url https://noahlan.com
 */
@Slf4j
@AllArgsConstructor
public class ITIFeignFallback<T> implements MethodInterceptor {
    private final Class<T> targetType;
    private final String targetName;
    private final Throwable cause;

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String errorMessage = cause.getLocalizedMessage();
        if (!(cause instanceof FeignException)) {
            if (cause.getCause() instanceof FeignException.FeignClientException) {
                errorMessage = cause.getCause().getLocalizedMessage();
            }
        }
        log.error("ITIFeignFallback [{}.{}] serviceId [{}] message [{}]", targetType.getName(), method.getName(), targetName, errorMessage);
        if (ApiResult.class == method.getReturnType()) {
            return ApiResult.error(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()),
                    Formatter.format("自动降级,服务间调用异常: [{}]", errorMessage),
                    cause);
        }
        throw cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ITIFeignFallback<?> that = (ITIFeignFallback<?>) o;
        return targetType.equals(that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetType);
    }
}
