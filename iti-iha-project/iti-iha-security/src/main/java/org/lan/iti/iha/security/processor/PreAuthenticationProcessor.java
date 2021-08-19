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

package org.lan.iti.iha.security.processor;

import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;

/**
 * 认证预处理器
 *
 * @author NorthLan
 * @date 2021/8/3
 * @url https://blog.noahlan.com
 */
public interface PreAuthenticationProcessor extends AuthenticationProcessor {
    /**
     * 创建认证请求
     *
     * @param parameter 请求参数
     * @return 新的认证实体
     */
    Authentication create(RequestParameter parameter, ProcessChain chain) throws AuthenticationException;

    @Override
    default int getOrder() {
        return Integer.MIN_VALUE + 1000;
    }

    @Override
    default boolean support(RequestParameter parameter, Authentication authentication) {
        return authentication == null;
    }

    @Override
    default Authentication process(RequestParameter parameter,
                                   Authentication authentication,
                                   ProcessChain chain) throws AuthenticationException {

        return create(parameter, chain);
    }
}
