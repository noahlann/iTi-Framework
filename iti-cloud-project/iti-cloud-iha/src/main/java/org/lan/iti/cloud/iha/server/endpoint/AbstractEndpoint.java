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

package org.lan.iti.cloud.iha.server.endpoint;

import org.lan.iti.cloud.iha.server.model.User;
import org.lan.iti.cloud.iha.server.pipeline.Pipeline;
import org.lan.iti.cloud.iha.server.service.OAuth2Service;
import org.lan.iti.cloud.iha.server.service.impl.OAuth2ServiceImpl;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Abstract classes common to various endpoints
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public abstract class AbstractEndpoint {
    protected final OAuth2Service oAuth2Service;

    public AbstractEndpoint() {
        this.oAuth2Service = new OAuth2ServiceImpl();
    }

    protected Pipeline<User> getUserInfoIdsPipeline(Pipeline<User> idsSigninPipeline) {
        if (null == idsSigninPipeline) {
            idsSigninPipeline = new Pipeline<User>() {
                @Override
                public boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) {
                    return Pipeline.super.preHandle(servletRequest, servletResponse);
                }

                @Override
                public User postHandle(ServletRequest servletRequest, ServletResponse servletResponse) {
                    return Pipeline.super.postHandle(servletRequest, servletResponse);
                }
            };
        }
        return idsSigninPipeline;
    }
}
