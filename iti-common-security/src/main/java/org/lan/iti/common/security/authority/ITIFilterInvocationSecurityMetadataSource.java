/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.security.authority;

import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.scanner.model.ResourceDefinition;
import org.lan.iti.common.scanner.util.PathUtils;
import org.lan.iti.common.security.authority.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 扩展 SecurityMetadataSource
 * <p>
 * 获取匹配的资源
 * </p>
 *
 * @author NorthLan
 * @date 2020-05-09
 * @url https://noahlan.com
 */
@NoArgsConstructor
@Slf4j
public class ITIFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Setter
    private FilterInvocationSecurityMetadataSource superMetadataSource;

    @Autowired
    private AuthorityService authorityService;

    @Value("${spring.application.name:_SELF_}")
    private String applicationName;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private UrlPathHelper pathHelper = new UrlPathHelper();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (object instanceof FilterInvocation) {
            FilterInvocation invocation = (FilterInvocation) object;
            HttpServletRequest request = invocation.getRequest();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String urlPath = pathHelper.getLookupPathForRequest(request);

            List<ResourceDefinition> allResources = authorityService.getAllResources(applicationName);
            List<ResourceDefinition> matchingPatterns = new ArrayList<>();
            for (ResourceDefinition item : allResources) {
                String registeredPattern = item.getUrl();
                if (StrUtil.isBlank(registeredPattern)) {
                    continue;
                }
                if (StrUtil.containsAnyIgnoreCase(item.getHttpMethod(), method)
                        && antPathMatcher.match(registeredPattern, urlPath)) {
                    matchingPatterns.add(item);
                }
            }
            ResourceDefinition bestMatch = null;
            Comparator<ResourceDefinition> patternComparator = PathUtils.getPatternComparator(urlPath);
            if (!matchingPatterns.isEmpty()) {
                matchingPatterns.sort(patternComparator);
                if (log.isTraceEnabled() && matchingPatterns.size() > 1) {
                    log.trace("Matching patterns " + matchingPatterns);
                }
                bestMatch = matchingPatterns.get(0);
            }
            if (bestMatch != null) {
                return Collections.singletonList(convertTo(bestMatch));
            }
        }
        if (superMetadataSource == null) {
            return Collections.emptySet();
        }
        return superMetadataSource.getAttributes(object);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return convertToList(authorityService.getAllResources(applicationName));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    /**
     * 将标准资源转换为ConfigAttribute
     *
     * @param resourceDefinitions 标准资源(scanner)列表
     * @return configAttribute
     */
    private Collection<ConfigAttribute> convertToList(List<ResourceDefinition> resourceDefinitions) {
        return resourceDefinitions.stream().map(this::convertTo).collect(Collectors.toList());
    }

    /**
     * 将标准资源转换为ConfigAttribute
     *
     * @param resourceDefinition 标准资源(scanner)
     * @return configAttribute
     */
    private ConfigAttribute convertTo(ResourceDefinition resourceDefinition) {
        return new SecurityConfig(SecurityConstants.PREFIX_AUZ + resourceDefinition.getCode());
    }
}
