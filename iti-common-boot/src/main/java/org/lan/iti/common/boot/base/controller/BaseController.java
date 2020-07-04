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

package org.lan.iti.common.boot.base.controller;

import org.lan.iti.common.core.base.BaseService;
import org.lan.iti.common.core.util.ReflectUtils;
import org.lan.iti.common.model.response.ApiResult;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Controller顶层接口
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public interface BaseController<E extends Serializable> {
    /**
     * 获取实体类型
     *
     * @return 实体类型
     */
    Class<E> getEntityClass();

    /**
     * 获取基本Service,可转型
     */
    BaseService<E> getBaseService();

    /**
     * 获取调用此方法的方法，带缓存
     * <pre>
     *     由于Hutools反射工具将会缓存方法信息,此处就不再缓存
     * </pre>
     */
    default Method getCallerMethod() {
        final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        final String methodName = stackTraceElement.getMethodName();
        return ReflectUtils.getMethodByName(getClass(), methodName);
    }

    /**
     * 结果处理
     *
     * @param result 内部已生成结果
     * @param method 触发方法,便于判断条件处理
     * @return 最终返回值
     */
    default ApiResult<?> processResult(ApiResult<?> result, Method method) {
        return result;
    }
}
