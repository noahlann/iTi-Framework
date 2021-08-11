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

package org.lan.iti.iha.server.provider;

import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.annotation.Extension;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.server.security.IhaServerRequestParam;

/**
 * Authorize the endpoint to create a callback url, and pass different callback parameters according to the request parameters
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 */
@Extension
public interface AuthorizationProvider extends IExtension<String> {

    /**
     * 生成Response（redirect）
     *
     * @param param         请求参数
     * @param responseType  回复类型
     * @param clientDetails 客户端信息
     * @param userDetails          用户信息
     * @param issuer        issuer
     * @return redirectUrl
     */
    String generateRedirect(IhaServerRequestParam param, String responseType, ClientDetails clientDetails, UserDetails userDetails, String issuer);
}
