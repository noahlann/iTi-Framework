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

package org.lan.iti.cloud.iha.core.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.lan.iti.cloud.iha.sso.config.IhaSsoConfig;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class IhaConfig {

    /**
     * Enable sso, is not enabled by default
     */
    private boolean sso;

    /**
     * SSO config
     */
    private IhaSsoConfig ssoConfig;

    /**
     * After the user logs in successfully, the valid time of the token, in milliseconds, the default validity period is 7 days
     */
    private long tokenExpireTime = TimeUnit.DAYS.toMillis(7);

    /**
     * The expiration time of the jap cache, in milliseconds, the default validity period is 7 days
     */
    private long cacheExpireTime = TimeUnit.DAYS.toMillis(7);

    public IhaConfig enableSso() {
        return setSso(true);
    }

    public IhaConfig enableSso(Consumer<IhaSsoConfig> ssoConfig) {
        setSso(true);
        IhaSsoConfig japSsoConfig = new IhaSsoConfig();
        ssoConfig.accept(japSsoConfig);
        return setSsoConfig(japSsoConfig);
    }
}
