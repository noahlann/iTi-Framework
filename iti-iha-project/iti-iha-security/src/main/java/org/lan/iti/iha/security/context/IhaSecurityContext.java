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

package org.lan.iti.iha.security.context;

import lombok.Data;
import lombok.experimental.Accessors;
import org.lan.iti.iha.security.cache.Cache;
import org.lan.iti.iha.security.cache.LocalCache;
import org.lan.iti.iha.security.authentication.AuthenticationChecker;
import org.lan.iti.iha.security.authentication.AuthenticationManager;
import org.lan.iti.iha.security.authentication.DefaultAuthenticationChecker;
import org.lan.iti.iha.security.authentication.DefaultAuthenticationManager;
import org.lan.iti.iha.security.clientdetails.ClientDetailsService;
import org.lan.iti.iha.security.config.SecurityConfig;
import org.lan.iti.iha.security.jwt.JwtService;
import org.lan.iti.iha.security.mgt.DefaultSecurityManager;
import org.lan.iti.iha.security.mgt.SecurityManager;
import org.lan.iti.iha.security.pipeline.PipelineManager;
import org.lan.iti.iha.security.userdetails.UserDetailsService;

/**
 * Security Context
 *
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
@Data
@Accessors(chain = true)
public class IhaSecurityContext {
    // security manager
    private SecurityManager securityManager = new DefaultSecurityManager();
    private AuthenticationManager authenticationManager = new DefaultAuthenticationManager();
    private PipelineManager pipelineManager = new PipelineManager();
    private SecurityConfig config = new SecurityConfig();

    // support service
    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private AuthenticationChecker authenticationChecker = new DefaultAuthenticationChecker(); // for user
    private ClientDetailsService clientDetailsService;

    // caching
    private Cache cache = new LocalCache();
}
