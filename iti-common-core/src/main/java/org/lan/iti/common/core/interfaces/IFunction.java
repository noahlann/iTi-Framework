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

package org.lan.iti.common.core.interfaces;

import org.lan.iti.common.core.util.PropertyNamer;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 函数式接口
 * <p>
 * 实现获取方法名 方法信息 等
 *
 * @author NorthLan
 * @date 2020-04-24
 * @url https://noahlan.com
 */
@FunctionalInterface
public interface IFunction<T, R> extends Function<T, R>, Serializable {

    default SerializedLambda getSerializedLambda() throws Exception {
        Method write = getClass().getDeclaredMethod("writeReplace");
        write.setAccessible(true);
        return (SerializedLambda) write.invoke(this);
    }

    default String getImplClass() {
        try {
            return getSerializedLambda().getImplClass();
        } catch (Exception e) {
            return null;
        }
    }

    default String getImplFieldName() {
        String methodName = getImplMethodName();
        if (methodName == null) {
            return null;
        }
        return PropertyNamer.methodToProperty(methodName, false);
    }

    default String getImplMethodName() {
        try {
            return getSerializedLambda().getImplMethodName();
        } catch (Exception e) {
            return null;
        }
    }
}
