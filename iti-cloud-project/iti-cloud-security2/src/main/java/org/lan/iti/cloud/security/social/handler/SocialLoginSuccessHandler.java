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

package org.lan.iti.cloud.security.social.handler;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 社交登录成功事件监听，代以处理oauth2-token生成业务
 * <p>暂不使用添加自定义endpoint的形式</p>
 *
 * @author NorthLan
 * @date 2021-03-18
 * @url https://noahlan.com
 */
@Slf4j
public class SocialLoginSuccessHandler implements AuthenticationSuccessHandler {
    private static final String AUTHENTICATION_SCHEME_BASIC = "Basic";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Lazy
    @Autowired
    private AuthorizationServerTokenServices defaultAuthorizationServerTokenServices;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String[] clients = extractAndDecodeClient(request);
        String clientId = clients[0];
        String clientSecret = clients[1];
        if (StrUtil.isBlank(clientId) || StrUtil.isBlank(clientSecret)) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        // 校验 clientSecret
        if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            throw new InvalidClientException("Given client ID does not match authenticated client");
        }
        TokenRequest tokenRequest = new TokenRequest(
                new HashMap<>(0),
                clientId,
                clientDetails.getScope(),
                "social");
        // 校验scope
        new DefaultOAuth2RequestValidator().validateScope(tokenRequest, clientDetails);
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
        OAuth2AccessToken oAuth2AccessToken = defaultAuthorizationServerTokenServices
                .createAccessToken(oAuth2Authentication);
        log.info("获取token 成功：{}", oAuth2AccessToken.getValue());

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        @Cleanup
        PrintWriter writer = response.getWriter();
        writer.append(objectMapper.writeValueAsString(oAuth2AccessToken));
    }

    /**
     * 从request中获取客户端信息
     * <p>Authorization: Basic {base64(clientId:clientSecret)}</p>
     * <p>
     * <p>clientId: xxxxxx</p>
     * <p>clientSecret: xxxxxx</p>
     *
     * @param request 请求
     * @return 数组 [clientId,clientSecret]
     * @throws BadCredentialsException 错误的header引发的异常
     */
    private String[] extractAndDecodeClient(HttpServletRequest request) throws BadCredentialsException {
        // 1. 优先取header
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String[] headers = extractAndDecodeHeader(header);
        if (headers != null) {
            return headers;
        }
        // 2. 其次取参数
        String clientId = request.getParameter("clientId");
        String clientSecret = request.getParameter("clientSecret");
        return new String[]{clientId, clientSecret};
    }

    /**
     * 从 header 中获取客户端信息
     * <p>
     * 算法根据
     * {@link org.springframework.security.web.authentication.www.BasicAuthenticationConverter#convert(HttpServletRequest)}
     * <p>
     *
     * @param header 请求头 Authorization:
     * @return 数组 [clientId,clientSecret]
     * @throws BadCredentialsException 错误的header引发的异常
     */
    private String[] extractAndDecodeHeader(String header) throws BadCredentialsException {
        if (header == null) {
            return null;
        }
        header = header.trim();
        if (!StrUtil.startWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
            return null;
        }
        if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BASIC)) {
            throw new BadCredentialsException("Empty basic authentication token");
        }
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = java.util.Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }
        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(StringPool.COLON);
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}
