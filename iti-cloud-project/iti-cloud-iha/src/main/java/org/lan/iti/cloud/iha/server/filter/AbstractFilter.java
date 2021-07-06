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

package org.lan.iti.cloud.iha.server.filter;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.iha.server.IhaServer;
import org.lan.iti.cloud.iha.server.config.IhaServerConfig;
import org.lan.iti.cloud.iha.server.pipeline.Pipeline;
import org.lan.iti.cloud.iha.server.util.StringUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Slf4j
public class AbstractFilter {
    protected final List<String> ignoreUrls = new ArrayList<>();

    /**
     * Whether it is a servlet request that needs to be ignored
     *
     * @param request The current HTTP request to be intercepted
     * @return boolean, the request does not need to be intercepted when true is returned
     */
    protected boolean isIgnoredServletPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (ignoreUrls.contains(servletPath)) {
            return true;
        }
        for (String ignoreUrl : ignoreUrls) {
            if (ignoreUrl.contains("**")) {
                String[] urls = ignoreUrl.split("\\*\\*");
                if (urls.length == 1) {
                    if (servletPath.startsWith(urls[0])) {
                        return true;
                    }
                }
                if (urls.length > 1) {
                    if (servletPath.startsWith(urls[0]) && servletPath.endsWith(urls[urls.length - 1])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Initialize the url of the filter to be released
     *
     * @param ignoreUrl URLs that do not need to be intercepted
     */
    protected void initIgnoreUrls(String ignoreUrl) {
        if (null != ignoreUrl) {
            String[] ignoreUrls = ignoreUrl.split(",");
            this.ignoreUrls.addAll(Arrays.asList(ignoreUrls));
        } else {
            // Fault-tolerant processing
            IhaServerConfig config = IhaServer.getIhaServerConfig();
            String authorizeUrl = config.getAuthorizeUrl();
            String authorizeAutoApproveUrl = config.getAuthorizeAutoApproveUrl();
            String loginUrl = config.getLoginUrl();
            String loginPageUrl = config.getLoginPageUrl();
            String errorUrl = config.getErrorUrl();
            String confirmPageUrl = config.getConfirmPageUrl();
            String tokenUrl = config.getTokenUrl();
            String registrationUrl = config.getRegistrationUrl();
            String checkSessionUrl = config.getCheckSessionUrl();
            String jwksUrl = config.getJwksUrl();
            String discoveryUrl = config.getDiscoveryUrl();
            String logoutUrl = config.getLoginUrl();
            String logoutRedirectUrl = config.getLogoutRedirectUrl();
            String[] urls = {authorizeUrl, authorizeAutoApproveUrl, loginUrl, loginPageUrl, errorUrl, confirmPageUrl,
                    tokenUrl, registrationUrl, jwksUrl, discoveryUrl, logoutUrl, logoutRedirectUrl, checkSessionUrl};
            for (String url : urls) {
                if (StringUtil.isEmpty(url)) {
                    continue;
                }

                this.ignoreUrls.add(url);
            }
        }
        this.ignoreUrls.add("/favicon.ico");
    }

    protected Pipeline<Object> getFilterErrorPipeline(Pipeline<Object> idsFilterErrorPipeline) {
        if (null == idsFilterErrorPipeline) {
            idsFilterErrorPipeline = new Pipeline<Object>() {
                @Override
                public void errorHandle(ServletRequest servletRequest, ServletResponse servletResponse, Throwable throwable) {
                    Pipeline.super.errorHandle(servletRequest, servletResponse, throwable);
                }
            };
        }
        return idsFilterErrorPipeline;
    }
}
