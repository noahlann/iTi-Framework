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

package org.lan.iti.cloud.iha.server;

import lombok.experimental.UtilityClass;
import org.lan.iti.cloud.iha.server.config.IhaServerConfig;
import org.lan.iti.cloud.iha.server.context.IhaServerContext;
import org.lan.iti.cloud.iha.server.exception.IhaServerException;
import org.lan.iti.cloud.iha.server.model.User;
import org.lan.iti.cloud.iha.server.pipeline.FilterPipeline;
import org.lan.iti.cloud.iha.server.pipeline.LogoutPipeline;
import org.lan.iti.cloud.iha.server.pipeline.SignInPipeline;
import org.lan.iti.cloud.iha.server.service.ClientDetailsService;
import org.lan.iti.cloud.iha.server.service.IdentityService;
import org.lan.iti.cloud.iha.server.service.UserDetailService;
import org.lan.iti.cloud.iha.server.service.UserStoreService;
import org.lan.iti.common.extension.ExtensionLoader;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class IhaServer implements Serializable {
    private static final long serialVersionUID = -8492693836745582215L;

    private static final String UNREGISTERED_IDS_CONTEXT = "Unregistered ids context.Please use `IhaServer.registerContext(IdsContext)` to register ids context.";
    private static IhaServerContext context;

    public static void registerContext(IhaServerContext context) {
        if (null == context) {
            throw new IhaServerException(UNREGISTERED_IDS_CONTEXT);
        }
        IhaServer.context = context;

        loadService();

        loadPipeline();
    }

    private static void loadService() {
        if (null == context.getClientDetailsService()) {
            context.setClientDetailsService(ExtensionLoader.getLoader(ClientDetailsService.class).getFirstExtension());
        }
        if (null == context.getIdentityService()) {
            context.setIdentityService(ExtensionLoader.getLoader(IdentityService.class).getFirstExtension());
        }
        if (null == context.getUserDetailService()) {
            context.setUserDetailService(ExtensionLoader.getLoader(UserDetailService.class).getFirstExtension());
        }
        if (null == context.getUserStoreService()) {
            context.setUserStoreService(ExtensionLoader.getLoader(UserStoreService.class).getFirstExtension());
        }
    }

    private static void loadPipeline() {
        if (null == context.getFilterPipeline()) {
            context.setFilterPipeline(ExtensionLoader.getLoader(FilterPipeline.class).getFirstExtension());
        }
        if (null == context.getSignInPipeline()) {
            context.setSignInPipeline(ExtensionLoader.getLoader(SignInPipeline.class).getFirstExtension());
        }
        if (null == context.getLogoutPipeline()) {
            context.setLogoutPipeline(ExtensionLoader.getLoader(LogoutPipeline.class).getFirstExtension());
        }
    }

    public static IhaServerContext getContext() {
        if (null == context) {
            throw new IhaServerException(UNREGISTERED_IDS_CONTEXT);
        }
        return context;
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return null != getUser(request);
    }

    public static void saveUser(User user, HttpServletRequest request) {
        getContext().getUserStoreService().save(user, request);
    }

    public static User getUser(HttpServletRequest request) {
        return getContext().getUserStoreService().get(request);
    }

    public static void removeUser(HttpServletRequest request) {
        getContext().getUserStoreService().remove(request);
    }

    public static IhaServerConfig getIhaServerConfig() {
        return getContext().getConfig();
    }
}
