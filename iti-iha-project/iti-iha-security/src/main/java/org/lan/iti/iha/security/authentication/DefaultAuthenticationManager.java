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

package org.lan.iti.iha.security.authentication;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lan.iti.iha.security.exception.AuthenticationException;
import org.lan.iti.iha.security.exception.ProviderNotFoundException;
import org.lan.iti.iha.security.exception.UnsupportedAuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.AuthenticationProcessor;
import org.lan.iti.iha.security.processor.ProcessorManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * default process manager
 *
 * @author NorthLan
 * @date 2021/7/29
 * @url https://blog.noahlan.com
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAuthenticationManager implements AuthenticationManager {
    @Getter
    private final Map<String, ProcessorManager> managerMap = new ConcurrentHashMap<>();

    @Setter
    private boolean eraseCredentialsAfterAuthentication = true;

    @Override
    public AuthenticationManager addProcessor(String type, AuthenticationProcessor processor) {
        return addProcessors(type, processor);
    }

    @Override
    public AuthenticationManager addProcessors(String type, AuthenticationProcessor... processors) {
        Assert.notEmpty(type, "type cannot null");
        ProcessorManager manager = getProcessorManager(type);
        for (AuthenticationProcessor processor : processors) {
            manager.addProcessor(processor);
        }
        return this;
    }

    private ProcessorManager getProcessorManager(String type) {
        return managerMap.computeIfAbsent(type, t -> new ProcessorManager(type, true));
    }

    @NotNull
    @Override
    public Authentication authenticate(RequestParameter parameter) throws AuthenticationException {
        AuthenticationException lastException = null;
        Authentication result = null;

        ProcessorManager processorManager = getProcessorManager(parameter.getProcessorType());
        if (processorManager == null) {
            lastException = UnsupportedAuthenticationException.getDefaultInstance();
        } else {
            try {
                result = processorManager.process(parameter);
            } catch (AuthenticationException ex) {
                lastException = ex;
            }
        }
        if (result != null) {
            if (this.eraseCredentialsAfterAuthentication && (result instanceof CredentialsContainer)) {
                // Authentication is complete. Remove credentials and other secret data
                // from authentication
                ((CredentialsContainer) result).eraseCredentials();
            }
            return result;
        }
        if (lastException == null) {
            throw new ProviderNotFoundException("No AuthenticationProvider found for: " + parameter);
        }
        throw lastException;
    }
}
