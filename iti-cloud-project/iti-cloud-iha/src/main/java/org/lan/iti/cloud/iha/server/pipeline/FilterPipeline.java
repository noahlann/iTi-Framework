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

package org.lan.iti.cloud.iha.server.pipeline;

import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.annotation.Extension;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * For the filter of iha-server, a pipeline interface is provided. When an exception occurs in the filter,
 * <p>
 * you can use {@link org.lan.iti.cloud.iha.server.pipeline.FilterPipeline#errorHandle(ServletRequest, ServletResponse, Throwable)} to handle the exception information .
 * <p>
 * The data in json format is returned by default:
 *
 * <code>
 * {
 * "error": "error",
 * "error_description": "error_description"
 * }
 * </code>
 * <p>
 * Note: Only need to implement the {@link org.lan.iti.cloud.iha.server.pipeline.FilterPipeline#errorHandle(ServletRequest, ServletResponse, Throwable)} method of this interface.
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Extension
public interface FilterPipeline extends Pipeline<Object>, IExtension<Object> {
    @Override
    default boolean matches(Object params) {
        return true;
    }

    /**
     * Callback when the program is abnormal
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     * @param throwable       any exception thrown on handler execution, if any.
     */
    @Override
    default void errorHandle(ServletRequest servletRequest, ServletResponse servletResponse, Throwable throwable) {
        Pipeline.super.errorHandle(servletRequest, servletResponse, throwable);
    }

    /**
     * Operations before business process processing, such as initializing resources, etc.
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     * @return boolean
     */
    @Deprecated
    @Override
    default boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) {
        return Pipeline.super.preHandle(servletRequest, servletResponse);
    }

    /**
     * Intercept the execution of a handler
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     * @return Object
     */
    @Deprecated
    @Override
    default Object postHandle(ServletRequest servletRequest, ServletResponse servletResponse) {
        return Pipeline.super.postHandle(servletRequest, servletResponse);
    }

    /**
     * Callback after business process processing is completed, such as recycling resources, recording status, etc.
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     */
    @Deprecated
    @Override
    default void afterHandle(ServletRequest servletRequest, ServletResponse servletResponse) {
        Pipeline.super.afterHandle(servletRequest, servletResponse);
    }
}
