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

package org.lan.iti.iha.core.strategy;

import org.lan.iti.iha.core.config.AuthenticateConfig;
import org.lan.iti.iha.core.result.IhaErrorCode;
import org.lan.iti.iha.core.result.IhaResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface IhaStrategy {
    /**
     * This function must be overridden by subclasses.  In abstract form, it always throws an exception.
     *
     * @param config   Authenticate Config
     * @param request  The request to authenticate
     * @param response The response to authenticate
     * @return IhaResponse
     */
    default IhaResponse authenticate(AuthenticateConfig config, HttpServletRequest request, HttpServletResponse response) {
        return IhaResponse.error(IhaErrorCode.ERROR.getCode(), "IhaStrategy#authenticate(AuthenticateConfig, HttpServletRequest, HttpServletResponse) must be overridden by subclass");
    }
}
