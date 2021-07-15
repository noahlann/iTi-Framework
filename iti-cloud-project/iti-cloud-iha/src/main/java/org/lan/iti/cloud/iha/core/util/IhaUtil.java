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

package org.lan.iti.cloud.iha.core.util;

import org.lan.iti.cloud.iha.core.IhaUser;
import org.lan.iti.cloud.iha.sso.IhaSsoUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public class IhaUtil {
    private static final String REDIRECT_ERROR = "IHA failed to redirect via HttpServletResponse.";

    public static String createToken(IhaUser ihaUser, HttpServletRequest request) {
        return IhaSsoUtil.createToken(ihaUser.getId(), ihaUser.getUsername(), request);
    }
}
