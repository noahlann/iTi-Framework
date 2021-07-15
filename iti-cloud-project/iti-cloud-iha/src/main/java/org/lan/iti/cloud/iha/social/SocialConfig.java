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

package org.lan.iti.cloud.iha.social;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import me.zhyd.oauth.config.AuthConfig;
import org.lan.iti.cloud.iha.core.config.AuthenticateConfig;

/**
 * Configuration file of third-party authorization login module
 *
 * @author NorthLan
 * @date 2021-07-15
 * @url https://noahlan.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Builder
public class SocialConfig extends AuthenticateConfig {

    /**
     * The name of a third-party platform regardless of case. For example: gitee、github、google
     */
    private String platform;

    /**
     * An opaque value used by the client to maintain state between the request and callback.  The authorization
     * server includes this value when redirecting the user-agent back to the client
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.1.1" target="_blank">https://tools.ietf.org/html/rfc6749#section-4.1.1</a>
     */
    private String state;

    /**
     * JustAuth Config
     */
    private AuthConfig justAuthConfig;

    /**
     * Package of {@code AuthRequest} implementation classes
     */
    private String[] scanPackages;

    /**
     * Exclude classes that do not need to be registered, such as: {@code AuthDefaultRequest}、
     * {@code AbstractAuthWeChatEnterpriseRequest}、{@code AuthRequest}
     */
    private String[] exclusionClassNames;
}
