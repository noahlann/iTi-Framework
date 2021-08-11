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

package org.lan.iti.iha.server;

import cn.hutool.core.collection.CollUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.pkce.CodeChallengeMethod;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.clientdetails.ClientDetailsService;
import org.lan.iti.iha.security.context.IhaSecurityContext;
import org.lan.iti.iha.security.context.SecurityContextHolder;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessorType;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.config.IhaServerConfig;
import org.lan.iti.iha.server.context.IhaServerContext;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.provider.ScopeProvider;
import org.lan.iti.iha.server.service.IdentityService;
import org.lan.iti.iha.server.service.IhaUserDetailService;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 */
public class TestLogin {
    private IhaServerContext context;
    private ClientDetails clientDetails;
    private UserDetails userDetails;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        context = new IhaServerContext();

        ClientDetailsService clientDetailsService = Mockito.mock(ClientDetailsService.class);
        clientDetails = ClientDetails.builder()
                .id("test")
                .clientId("test")
                .clientName("test")
                .clientSecret("test")
                .accessTokenTimeToLive(TimeUnit.MINUTES.toSeconds(30))
                .codeTimeToLive(TimeUnit.MINUTES.toSeconds(30))
                .idTokenTimeToLive(TimeUnit.MINUTES.toSeconds(30))
                .refreshTokenTimeToLive(TimeUnit.MINUTES.toSeconds(30))
                .codeChallengeMethod(CodeChallengeMethod.S256.name())
                .grantTypes(GrantType.AUTHORIZATION_CODE.getType())
                .redirectUris("http://127.0.0.1")
                .requireProofKey(false)
                .responseTypes(ResponseType.CODE.getType())
                .scopes(CollUtil.join(ScopeProvider.getScopeCodes(), " "))
                .reuseRefreshToken(true)
                .siteDomain("domain")
                .available(true)
                .autoApprove(true)
                .build();
        Mockito.when(clientDetailsService.getByAppId(clientDetails.getId())).thenReturn(clientDetails);

        userDetails = new UserDetails()
                .setUsername("admin")
                .setCredentials("123456")
                .setId("123");
        IhaUserDetailService userDetailService = Mockito.mock(IhaUserDetailService.class);
        Mockito.when(userDetailService.loadByType(userDetails.getUsername(), "username", null))
                .thenReturn(userDetails);

        IdentityService identityService = Mockito.mock(IdentityService.class);

        context.setIdentityService(identityService);
        context.setConfig(new IhaServerConfig()
                .setEnableDynamicIssuer(true)
                .setExternalConfirmPageUrl(true)
                .setExternalLoginPageUrl(true));

        IhaSecurityContext context = new IhaSecurityContext();
        context.setUserDetailsService(userDetailService);
        context.setClientDetailsService(clientDetailsService);

        context.getAuthenticationManager().addProcessor(ProcessorType.SIMPLE, null);
        IhaSecurity.init(context);
    }

    @Test
    public void testLogin() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Map<String, String[]> map = new TreeMap<>();
//        map.put("client_id", new String[]{clientDetails.getClientId()});
//        map.put("client_secret", new String[]{clientDetails.getClientSecret()});
        map.put("principal", new String[]{userDetails.getUsername()});
        map.put("credentials", new String[]{userDetails.getCredentials().toString()});
        map.put("type", new String[]{"username"});
        //
        Mockito.when(request.getParameterMap()).thenReturn(map);

        RequestParameter parameter = new RequestParameter(map);
        parameter.setProcessorType(ProcessorType.SIMPLE);

        Authentication authentication = IhaSecurity.getContext().getSecurityManager().authenticate(new RequestParameter(request));
        System.out.println(authentication);
        System.out.println(authentication.isAuthenticated());

//        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        System.out.println(IhaSecurity.getContext().getSecurityManager().check(parameter));
    }
}
