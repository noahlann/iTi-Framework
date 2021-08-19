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

import lombok.experimental.UtilityClass;
import org.lan.iti.iha.server.config.IhaServerConfig;
import org.lan.iti.iha.server.config.UrlProperties;
import org.lan.iti.iha.server.context.IhaServerContext;

import java.io.Serializable;

/**
 * 授权服务器
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class IhaServer implements Serializable {
    private static final long serialVersionUID = -8492693836745582215L;

    private static final String UNREGISTERED_CONTEXT = "Unregistered iha context.Please use `IhaServer.registerContext(IhaServerContext)` to register iha context.";
    private static IhaServerContext context;

    public static void init(IhaServerContext context) {
        if (null == context) {
            throw new SecurityException(UNREGISTERED_CONTEXT);
        }
        IhaServer.context = context;
    }

    public static IhaServerContext getContext() {
        if (null == context) {
            throw new SecurityException(UNREGISTERED_CONTEXT);
        }
        return context;
    }

    public static IhaServerConfig getIhaServerConfig() {
        return getContext().getConfig();
    }

    public static UrlProperties getUrlProperties() {
        return getIhaServerConfig().getUrlProperties();
    }
}
