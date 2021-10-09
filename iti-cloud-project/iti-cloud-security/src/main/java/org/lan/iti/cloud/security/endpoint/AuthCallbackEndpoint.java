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

package org.lan.iti.cloud.security.endpoint;


import cn.hutool.core.util.URLUtil;
import lombok.RequiredArgsConstructor;
import org.jose4j.jwt.JwtClaims;
import org.lan.iti.cloud.api.IgnoreResponseBodyWrapper;
import org.lan.iti.cloud.security.properties.CacheConfig;
import org.lan.iti.cloud.security.properties.SecurityProperties;
import org.lan.iti.common.core.api.ApiResult;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.common.core.util.idgen.IdGenerator;
import org.lan.iti.iha.oauth2.security.OAuth2AuthenticationToken;
import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.oidc.security.OidcRequestParameter;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.cache.Cache;
import org.lan.iti.iha.security.context.SecurityContextHolder;
import org.lan.iti.iha.security.jwt.JwtConfig;
import org.lan.iti.iha.security.jwt.JwtUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * auth 回调接口
 * <p>
 * 支持协议 oauth2/oidc
 * 更多陆续添加中
 * <p>
 * 同时支持获取重定向地址功能,空参数访问即可获取重定向登录中心地址
 *
 * @author NorthLan
 * @date 2021/9/26
 * @url https://blog.noahlan.com
 */
@RestController
@RequestMapping(value = {"/auth/callback", "/auth/redirect"})
@IgnoreResponseBodyWrapper
@RequiredArgsConstructor
public class AuthCallbackEndpoint {
    private final SecurityProperties properties;

    @RequestMapping(value = "/oauth2", method = {RequestMethod.GET, RequestMethod.POST})
    public Object oauth2(HttpServletRequest request, HttpServletResponse response) {
        OAuth2RequestParameter parameter = new OAuth2RequestParameter(request, response);
        parameter.setConfig(properties.getOauth2());

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) IhaSecurity.getSecurityManager().authenticate(parameter);
        return buildRedirect(request, token);
    }

    @RequestMapping(value = "/oidc", method = {RequestMethod.GET, RequestMethod.POST})
    public Object oidc(HttpServletRequest request, HttpServletResponse response) {
        OidcRequestParameter parameter = new OidcRequestParameter(request, response);
        parameter.setConfig(properties.getOidc());

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) IhaSecurity.getSecurityManager().authenticate(parameter);
        return buildRedirect(request, token);
    }

    private Object buildRedirect(HttpServletRequest request, OAuth2AuthenticationToken authentication) {
        if (authentication.isRedirect()) {
            if (StringUtil.startWith(request.getRequestURI(), "/auth/redirect")) {
                return ApiResult.ok(authentication.getRedirectUri());
            }
            return new RedirectView(authentication.getRedirectUri());
        }
        String frontUri = properties.getFrontUri();
        String clientId = properties.getOauth2().getClientId();
        AccessToken accessToken = authentication.getAccessToken();
        Long akExpiresIn = accessToken.getExpiresIn();
        // 生成新的token并保存
        JwtConfig jwtConfig = properties.getJwt();

        JwtClaims claims = JwtUtil.createClaims(
                accessToken.getUserId(),
                clientId,
                accessToken.getUserId(),
                null, akExpiresIn);

        String token = JwtUtil.createJwtToken(claims,
                jwtConfig.getJwksKeyId(),
                jwtConfig.getJwksJson(),
                jwtConfig.getTokenSigningAlg());

        AccessToken platformToken = accessToken.toBuilder()
                .id(IdGenerator.nextStr())
                .refreshToken(null)
                .refreshTokenExpiresIn(null)
                .idToken(null)
                .idTokenExpiresIn(null)
                .build();
        platformToken.setAccessToken(token)
                .setExpiresIn(akExpiresIn);

        SecurityContextHolder.getContext().setAuthentication(new OAuth2AuthenticationToken(platformToken, authentication.getPrincipal(), authentication.getAuthorities()));

        /*
         * $IHA_SECURITY:TOKEN:{id}: accessToken(object)
         * $IHA_SECURITY:PLATFORM_TOKEN:{id}: newToken(object)
         * $IHA_SECURITY:PLATFORM_TOKEN:REV:{newToken(string)}: newTokenId
         */
        CacheConfig cacheConfig = properties.getCache();
        String tokenPrefix = cacheConfig.getSecurityPrefix() + cacheConfig.getTokenPrefix() + accessToken.getId();
        String platformTokenPrefix = cacheConfig.getSecurityPrefix() + cacheConfig.getPlatformTokenPrefix() + platformToken.getId();
        String platformTokenRevPrefix = cacheConfig.getSecurityPrefix() + cacheConfig.getPlatformTokenPrefix() + "REV:" + token;

        Cache cache = IhaSecurity.getContext().getCache();
        cache.put(tokenPrefix, accessToken, accessToken.getRefreshTokenExpiresIn(), TimeUnit.SECONDS);
        cache.put(platformTokenPrefix, platformToken, platformToken.getExpiresIn(), TimeUnit.SECONDS);
        cache.put(platformTokenRevPrefix, platformToken.getId(), platformToken.getExpiresIn(), TimeUnit.SECONDS);

        Map<String, String> param = new HashMap<>();
        param.put("token", token);

        String urlParams = URLUtil.buildQuery(param, StandardCharsets.UTF_8);
        return new RedirectView(frontUri + "?" + urlParams);
    }
}
