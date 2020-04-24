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

package org.lan.iti.common.security.component;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.security.exception.ITIAuth2Exception;
import org.lan.iti.common.security.service.UserDetailsBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 扩展 用户登录信息转化器
 * 避免两次读取userDetails,优化性能
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Slf4j
@AllArgsConstructor
public class ITIUserAuthenticationConverter implements UserAuthenticationConverter {
    private static final String N_A = "N/A";

    /**
     * 用户构建器
     */
    private final UserDetailsBuilder userDetailsBuilder;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(USERNAME, userAuthentication.getName());
        if (userAuthentication.getAuthorities() != null && !userAuthentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(userAuthentication.getAuthorities()));
        }
        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            Map<String, ?> userMap = MapUtil.get(map, SecurityConstants.DETAILS_USER_DETAILS, Map.class);
            Collection<? extends GrantedAuthority> authorities = getAuthorities(userMap);
            validateTenantId(userMap);
            return new UsernamePasswordAuthenticationToken(userDetailsBuilder.from(userMap, authorities), N_A, authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }

    /**
     * 验证租户信息
     */
    private void validateTenantId(Map<String, ?> map) {
        String headerValue = getCurrentTenantId();
        Integer userValue = MapUtil.getInt(map, SecurityConstants.DETAILS_TENANT_ID);
        if (StrUtil.isNotBlank(headerValue) && !userValue.toString().equals(headerValue)) {
            log.warn("请求头中的租户ID({})和用户的租户ID({})不一致", headerValue, userValue);
            // TODO 不要提示租户ID不对，可能被穷举
            throw new ITIAuth2Exception(SpringSecurityMessageSource.getAccessor().getMessage("AbstractUserDetailsAuthenticationProvider.badTenantId", "Bad tenant ID"));
        }
    }

    // TODO 需构建到util中去
    private Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * 获取请求链中的租户ID
     */
    // TODO 需构建到util中去
    private String getCurrentTenantId() {
        return getCurrentHttpRequest().map(httpServletRequest -> httpServletRequest.getHeader(ITIConstants.TENANT_ID_HEADER_NAME)).orElse(null);
    }
}
