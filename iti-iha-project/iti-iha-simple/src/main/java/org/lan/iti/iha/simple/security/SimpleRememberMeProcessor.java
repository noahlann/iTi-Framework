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

import cn.hutool.core.convert.Convert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lan.iti.iha.security.util.RequestUtil;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.simple.RememberMeUtils;
import org.lan.iti.iha.simple.SimpleConfig;

/**
 * 4. SimpleRememberMeProcessor (parameter contains rememberMe field)
 *
 * @author NorthLan
 * @date 2021/8/5
 * @url https://blog.noahlan.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SimpleRememberMeProcessor extends AbstractSimpleAuthenticationProcessor {
    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        return authentication instanceof SimpleAuthenticationToken &&
                parameter instanceof SimpleRequestParameter &&
                this.support((SimpleRequestParameter) parameter);
    }

    protected boolean support(SimpleRequestParameter parameter) {
        SimpleConfig config = parameter.getSimpleConfig();
        return parameter.containsKey(config.getRememberMeField());
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        SimpleAuthenticationToken token = (SimpleAuthenticationToken) authentication;
        setRememberMe(token, (SimpleRequestParameter) parameter);
        return chain.process(parameter, token);
    }

    private void setRememberMe(SimpleAuthenticationToken token, SimpleRequestParameter parameter) {
        UserDetails userDetails = (UserDetails) token.getPrincipal();
        userDetails.setRememberMe(token.isRememberMe());
        if (token.isRememberMe()) {
            SimpleConfig simpleConfig = parameter.getSimpleConfig();
            RequestUtil.setCookie(parameter.getResponse(),
                    simpleConfig.getRememberMeCookieKey(),
                    encodeCookie(userDetails, simpleConfig, token.getType()),
                    Convert.toInt(simpleConfig.getRememberMeCookieMaxAge()),
                    "/",
                    simpleConfig.getRememberMeCookieDomain()
            );
        }
    }

    private String encodeCookie(UserDetails userDetails, SimpleConfig simpleConfig, String type) {
        return RememberMeUtils.encode(simpleConfig.getRememberMeCookieMaxAge(),
                        simpleConfig.getCredentialEncryptSalt(),
                        userDetails.getPrincipal(),
                        type)
                .getEncoded();
    }
}
