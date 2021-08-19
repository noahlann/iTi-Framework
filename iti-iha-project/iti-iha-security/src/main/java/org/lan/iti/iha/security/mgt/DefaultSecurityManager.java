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

package org.lan.iti.iha.security.mgt;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.authentication.support.AnonymousAuthenticationToken;
import org.lan.iti.iha.security.context.SecurityContextHolder;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.exception.NotInitializedException;
import org.lan.iti.iha.security.pipeline.PipelineManager;

import javax.annotation.Nonnull;

/**
 * @author NorthLan
 * @date 2021/7/29
 * @url https://blog.noahlan.com
 */
@Slf4j
public class DefaultSecurityManager implements SecurityManager {
    private void checkState() {
        if (IhaSecurity.getContext().getAuthenticationManager() == null) {
            throw new NotInitializedException("AuthenticationManager hasn't init.");
        }
        if (IhaSecurity.getContext().getPipelineManager() == null) {
            throw new NotInitializedException("PipelineManager hasn't init.");
        }
    }

    @Nonnull
    @Override
    public Authentication authenticate(RequestParameter parameter) throws AuthenticationException {
        checkState();
        PipelineManager pipelineManager = IhaSecurity.getContext().getPipelineManager();

        pipelineManager.preAuthentication(parameter);
        Authentication authentication;
        try {
            authentication = IhaSecurity.getContext().getAuthenticationManager().authenticate(parameter);
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                pipelineManager.onAuthenticationSuccess(parameter, authentication);
            }
            return authentication;
        } catch (AuthenticationException ex) {
            pipelineManager.onAuthenticationFailure(parameter, ex);
            SecurityContextHolder.clearContext();
            throw ex;
        }
    }

    @Override
    public Authentication check(RequestParameter parameter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken
                || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication;
    }
}
