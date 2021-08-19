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

package org.lan.iti.iha.simple.security;

import cn.hutool.core.util.BooleanUtil;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.simple.SimpleConfig;

/**
 * 密码模式
 * <p>
 * 用户名+密码
 * 手机号+密码
 * 邮箱+密码
 * <p>
 * 1. PreSimpleAuthenticationProcessor
 * 2. PreSimpleRememberMeProcessor (parameter contains rememberMe field)
 * 3. SimpleAuthenticationProcessor (principal instanceof UserDetails)
 * 4. SimpleRememberMeProcessor (parameter contains rememberMe field)
 *
 * @author NorthLan
 * @date 2021/8/3
 * @url https://blog.noahlan.com
 */
public class PreSimpleAuthenticationProcessor extends AbstractSimpleAuthenticationProcessor {
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1000;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        return authentication == null;
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        if (authentication != null) {
            throw new AuthenticationException("PreAuthenticationProcess must at first of chain position.");
        }
        SimpleRequestParameter simpleRequestParameter = new SimpleRequestParameter(parameter);
        SimpleConfig config = simpleRequestParameter.getSimpleConfig();

        String principal = simpleRequestParameter.getByKey(config.getPrincipalField());
        String credentials = simpleRequestParameter.getByKey(config.getCredentialsField());
        String type = simpleRequestParameter.getByKey(config.getTypeField());
        String rememberMe = simpleRequestParameter.getByKey(config.getRememberMeField());
        String code = simpleRequestParameter.getByKey(config.getCodeField());
        String extra = simpleRequestParameter.getByKey(config.getExtraField());

        // TODO check principal credentials|code type

        return chain.process(simpleRequestParameter,
                new SimpleAuthenticationToken(principal, credentials, type)
                        .setCode(code)
                        .setRememberMe(BooleanUtil.toBoolean(rememberMe))
                        .setExtra(extra)
        );
    }
}
