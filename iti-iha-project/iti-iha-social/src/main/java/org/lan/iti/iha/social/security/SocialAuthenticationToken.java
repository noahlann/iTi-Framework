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

package org.lan.iti.iha.social.security;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthRequest;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.security.authentication.AbstractAuthenticationToken;

import java.util.Collection;

/**
 * @author NorthLan
 * @date 2021/8/14
 * @url https://blog.noahlan.com
 */
@Getter
@Accessors(chain = true)
public class SocialAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 4309788539789414077L;

    private AccessToken accessToken;
    private Object principal;
    private AuthRequest authRequest;
    private AuthCallback authCallback;

    @Setter
    private String redirectUrl;

    public SocialAuthenticationToken(String redirectUrl) {
        super(null);
        this.redirectUrl = redirectUrl;
    }

    public SocialAuthenticationToken(AuthRequest authRequest, AuthCallback authCallback) {
        super(null);
        this.authRequest = authRequest;
        this.authCallback = authCallback;
    }

    public SocialAuthenticationToken(AccessToken accessToken, Object principal, Collection<String> authorities) {
        super(authorities);
        this.accessToken = accessToken;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return authCallback.getOauth_token();
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
