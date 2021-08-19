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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lan.iti.iha.security.util.RequestUtil;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.simple.RememberMeDetails;
import org.lan.iti.iha.simple.RememberMeUtils;
import org.lan.iti.iha.simple.SimpleConfig;

import javax.servlet.http.Cookie;

/**
 * 2. Pre RememberMe
 * <p>
 * (parameter contains rememberMe field)
 *
 * @author NorthLan
 * @date 2021/8/5
 * @url https://blog.noahlan.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PreSimpleRememberMeProcessor extends AbstractSimpleAuthenticationProcessor {

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        return authentication instanceof SimpleAuthenticationToken &&
                parameter instanceof SimpleRequestParameter &&
                this.support((SimpleRequestParameter) parameter);
    }

    private boolean support(SimpleRequestParameter parameter) {
        SimpleConfig config = parameter.getSimpleConfig();
        return parameter.containsKey(config.getRememberMeField());
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        return chain.process(parameter, checkCookie((SimpleRequestParameter) parameter, (SimpleAuthenticationToken) authentication));
    }

    private SimpleAuthenticationToken checkCookie(SimpleRequestParameter parameter, SimpleAuthenticationToken token) {
        SimpleConfig config = parameter.getSimpleConfig();
        if (!token.isRememberMe()) {
            return token;
        }
        Cookie cookie = RequestUtil.getCookie(parameter.getRequest(), config.getRememberMeCookieKey());
        if (cookie == null) {
            return token;
        }

        RememberMeDetails rememberMeDetails = this.decodeCookieValue(config, cookie.getValue());
        if (rememberMeDetails == null) {
            return token;
        }
        UserDetails userDetails = IhaSecurity.getContext().getUserDetailsService()
                .loadByType(rememberMeDetails.getPrincipal(), rememberMeDetails.getType(), null);
        if (userDetails == null) {
            return token;
        }
        return new SimpleAuthenticationToken(userDetails,
                userDetails.getCredentials(),
                rememberMeDetails.getType(),
                userDetails.getAuthorities());
    }

    private RememberMeDetails decodeCookieValue(SimpleConfig config, String cookieValue) {
        return RememberMeUtils.decode(config.getCredentialEncryptSalt(), cookieValue);
    }
}
