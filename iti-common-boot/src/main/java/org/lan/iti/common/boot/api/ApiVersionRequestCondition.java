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

package org.lan.iti.common.boot.api;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * API版本控制URL匹配
 *
 * @author NorthLan
 * @date 2020-09-10
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {
    // 路径中版本的前缀， 这里用 /v[1-9]/的形式
    private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("/v(\\d+).*/");

    // api版本
    private final int apiVersion;

    @NonNull
    @Override
    public ApiVersionRequestCondition combine(@NonNull ApiVersionRequestCondition other) {
        // 最后定于原则,方法上的定义优于类上的定义
        return new ApiVersionRequestCondition(other.apiVersion);
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(@NonNull HttpServletRequest request) {
        Matcher matcher = VERSION_PREFIX_PATTERN.matcher(request.getRequestURI());
        if (matcher.find()) {
            // 0: v 1: [0-9]
            int version = Integer.parseInt(matcher.group(1));
            if (version >= this.apiVersion) {
                return this;
            }
        }
        return null;
    }

    @Override
    public int compareTo(@NonNull ApiVersionRequestCondition other, @NonNull HttpServletRequest request) {
        // 优先匹配最新版本号
        return other.apiVersion - this.apiVersion;
    }
}
