/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.cloud.security.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.security.feign.RemoteClientDetailsService;
import org.lan.iti.cloud.security.model.OauthClientDetails;
import org.lan.iti.common.core.constants.CacheConstants;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 扩展ClientDetailsService
 * <p>
 * 1. 支持缓存
 * 2. 支持通过远程接口获取客户端信息，使Auth服务与数据库解耦
 *
 * @author NorthLan
 * @date 2020-02-27
 * @url https://noahlan.com
 */
@Slf4j
@RequiredArgsConstructor
public class ITIClientDetailsServiceImpl implements ClientDetailsService {
    private final RemoteClientDetailsService clientDetailsService;

    @Override
    @Cacheable(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#p0", unless = "#result == null")
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        OauthClientDetails clientDetails = clientDetailsService.getClientDetailsById(clientId).getData();

        if (clientDetails == null) {
            return null;
        }
        // 适配为spring内置的oauth2类型
        return clientDetailsWrapper(clientDetails);
    }

    /**
     * 客户端类型转化
     * <p>
     * 参考{@link org.springframework.security.oauth2.provider.client.JdbcClientDetailsService}
     *
     * @param origin OAuthClientDetails 统一模型
     * @return spring内置的oauth2对象
     */
    private ClientDetails clientDetailsWrapper(OauthClientDetails origin) {
        BaseClientDetails target = new BaseClientDetails();
        // 必选项
        target.setClientId(origin.getClientId());
        target.setClientSecret(String.format("{noop}%s", origin.getClientSecret()));

        if (ArrayUtil.isNotEmpty(origin.getAuthorizedGrantTypes())) {
            target.setAuthorizedGrantTypes(CollUtil.newArrayList(origin.getAuthorizedGrantTypes()));
        }

        if (StrUtil.isNotBlank(origin.getAuthorities())) {
            target.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(origin.getAuthorities()));
        }

        if (StrUtil.isNotBlank(origin.getResourceIds())) {
            target.setResourceIds(StringUtils.commaDelimitedListToSet(origin.getResourceIds()));
        }

        if (StrUtil.isNotBlank(origin.getWebServerRedirectUri())) {
            target.setRegisteredRedirectUri(StringUtils.commaDelimitedListToSet(origin.getWebServerRedirectUri()));
        }

        if (StrUtil.isNotBlank(origin.getScope())) {
            target.setScope(StringUtils.commaDelimitedListToSet(origin.getScope()));
        }

        if (StrUtil.isNotBlank(origin.getAutoapprove())) {
            target.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(origin.getAutoapprove()));
        }

        if (origin.getAccessTokenValidity() != null) {
            target.setAccessTokenValiditySeconds(origin.getAccessTokenValidity());
        }

        if (origin.getRefreshTokenValidity() != null) {
            target.setRefreshTokenValiditySeconds(origin.getRefreshTokenValidity());
        }

        String json = origin.getAdditionalInformation();
        if (StrUtil.isNotBlank(json)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> additionalInformation = JSONUtil.toBean(json, Map.class);
                target.setAdditionalInformation(additionalInformation);
            } catch (Exception e) {
                log.warn("Could not decode JSON for additional information: " + json, e);
            }
        }
        return target;
    }
}
