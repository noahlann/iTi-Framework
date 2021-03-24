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

package org.lan.iti.cloud.security.component;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.lan.iti.common.core.util.StringPool;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;


/**
 * 改造 {@link BearerTokenExtractor} 对公开权限的请求不进行token解析
 *
 * @author NorthLan
 * @date 2020-07-30
 * @url https://noahlan.com
 */
@RequiredArgsConstructor
public class ITIBearerTokenExtractor extends BearerTokenExtractor {
    private static final int URL_MAX_LENGTH = 2;
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final PermitAllUrlResolver permitAllUrlResolver;

    @Override
    public Authentication extract(HttpServletRequest request) {
        // 判断请求方法是否匹配
        boolean result = permitAllUrlResolver.getIgnoreUrls().stream().anyMatch(url -> {
            String[] strings = StrUtil.split(url, StringPool.PIPE);
            // 1. 判断路径匹配
            boolean match = pathMatcher.match(strings[0], request.getRequestURI());
            // 2. 判断方法匹配
            if (strings.length >= URL_MAX_LENGTH) {
                String[] methods = StrUtil.split(strings[1], StringPool.COMMA);
                return ArrayUtil.contains(methods, request.getMethod()) && match;
            }
            return match;
        });
        return result ? null : super.extract(request);
    }
}
