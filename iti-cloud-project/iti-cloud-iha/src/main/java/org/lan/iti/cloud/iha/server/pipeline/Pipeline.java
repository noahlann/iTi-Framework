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

import com.xkcoding.json.JsonUtil;
import org.lan.iti.cloud.iha.server.exception.IhaServerException;
import org.lan.iti.cloud.iha.server.model.IhaServerResponse;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * The pipeline is an enhanced interface for the business process of {@code I Hate Auth}<br>
 * <p>
 * The workflow interface allows custom processing program execution chains, <br>
 * and based on pipeline, common preprocessing logic can be added to some processing programs.<br>
 * <p>
 * The pipeline interface can be implemented to enhance the business process of {@code I Hate Auth}.
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public interface Pipeline<T> {
    /**
     * Callback when the program is abnormal
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     * @param throwable       any exception thrown on handler execution, if any.
     */
    default void errorHandle(ServletRequest servletRequest, ServletResponse servletResponse, Throwable throwable) {
        IhaServerResponse<String, Object> response = new IhaServerResponse<>();
        if (throwable instanceof IhaServerException) {
            IhaServerException idsException = (IhaServerException) throwable;
            response.error(idsException.getError())
                    .errorDescription(idsException.getErrorDescription());
        } else {
            response.errorDescription(throwable.getMessage());
        }
        String errorResponseStr = JsonUtil.toJsonString(response);
        servletResponse.setContentType("text/html;charset=UTF-8");
        servletResponse.setContentLength(errorResponseStr.getBytes(StandardCharsets.UTF_8).length);
        try {
            servletResponse.getWriter().write(errorResponseStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Operations before business process processing, such as initializing resources, etc.
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     * @return boolean
     */
    default boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) {
        return true;
    }

    /**
     * Intercept the execution of a handler
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     * @return Object
     */
    default T postHandle(ServletRequest servletRequest, ServletResponse servletResponse) {
        return null;
    }

    /**
     * Callback after business process processing is completed, such as recycling resources, recording status, etc.
     *
     * @param servletRequest  current HTTP request
     * @param servletResponse current HTTP response
     */
    default void afterHandle(ServletRequest servletRequest, ServletResponse servletResponse) {
    }
}
