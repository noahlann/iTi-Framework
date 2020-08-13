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

package org.lan.iti.common.core.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.interfaces.IFunction;
import org.springframework.lang.Nullable;

import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java 8 lambda 工具类
 *
 * @author NorthLan
 * @date 2020-04-28
 * @url https://noahlan.com
 */
@UtilityClass
public class LambdaUtils {
    /**
     * SerializedLambda 反序列化缓存
     */
    private final Map<String, WeakReference<SerializedLambda>> LAMBDA_CACHE = new ConcurrentHashMap<>(16);

    /**
     * 获取getter的lambda表达式
     *
     * @param getter getter
     * @param <T>    类型
     * @return lambda表达式
     */
    private <T> SerializedLambda getLambda(IFunction<T, ?> getter) {
        String canonicalName = getter.getClass().getCanonicalName();
        return Optional.ofNullable(LAMBDA_CACHE.get(canonicalName))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    try {
                        SerializedLambda lambda = getter.getSerializedLambda();
                        LAMBDA_CACHE.put(canonicalName, new WeakReference<>(lambda));
                        return lambda;
                    } catch (Exception e) {
                        // just catch it
                        return null;
                    }
                });
    }

    /**
     * 获取变量名称,实际取用getXXX中的XXX
     * 故,必须按照标准编码规范进行编码才可正确获取变量名称
     *
     * @param getter getter
     * @param <T>    入参类型
     * @param <R>    出参类型
     * @return 变量名
     */
    @Nullable
    public <T, R> String getFieldName(IFunction<T, R> getter) {
        String methodName = getMethodName(getter);
        if (methodName == null) {
            return null;
        }
        return PropertyNamer.methodToProperty(methodName, false);
    }

    /**
     * 获取Lambda实际的方法名称
     *
     * @param method method
     * @param <T>    入参类型
     * @param <R>    出参类型
     * @return 方法名称
     */
    @Nullable
    public <T, R> String getMethodName(IFunction<T, R> method) {
        SerializedLambda lambda = getLambda(method);
        return lambda.getImplMethodName();
    }

    /**
     * 获取Field名称
     * <p>
     * 转下划线形式
     * </p>
     *
     * @param getter getter
     * @param <T>    入参类型
     * @param <R>    出参类型
     * @return 变量名
     */
    @Nullable
    public <T, R> String getFieldNameCamel(IFunction<T, R> getter) {
        return StrUtil.toUnderlineCase(getFieldName(getter));
    }
}
