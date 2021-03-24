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

package org.lan.iti.cloud.jackson.dynamicfilter.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * jackson 运行时过滤 配置信息
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Data
@ConfigurationProperties(prefix = "iti.jackson.filter")
public class JacksonDynamicFilterProperties {
    private boolean enabled = true;

    /**
     * 快速失败，开启时直接抛出异常，否则只打印错误信息
     */
    private boolean failFast = false;

    private Resolver resolver = new Resolver();

    @Data
    @NoArgsConstructor
    public static class Resolver {
        private String[] classNames = new String[0];
    }
}
