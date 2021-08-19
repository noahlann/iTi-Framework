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

package org.lan.iti.iha.server.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class IhaServerConfig {

    /**
     * 开启动态Issuer
     */
    private boolean enableDynamicIssuer;

    /**
     * Identity provider
     */
    private String issuer;

    /**
     * The {@code context-path} property of the application. Enable if and only if {@link #enableDynamicIssuer} is true
     */
    private String contextPath;

    /**
     * Url配置
     */
    private UrlProperties urlProperties = new UrlProperties();
}
