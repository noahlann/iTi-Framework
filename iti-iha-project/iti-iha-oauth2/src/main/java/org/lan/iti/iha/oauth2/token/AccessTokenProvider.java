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

package org.lan.iti.iha.oauth2.token;

import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.annotation.Extension;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;

/**
 * AccessToken提供者
 *
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
@Extension
public interface AccessTokenProvider extends IExtension<String> {

    /**
     * 不同流程获取Token接口
     *
     * @param parameter    请求参数
     * @param oAuth2Config oAuth2配置
     * @return AccessToken
     * @throws AuthenticationException 获取失败异常
     */
    AccessToken getToken(OAuth2RequestParameter parameter, OAuth2Config oAuth2Config) throws AuthenticationException;
}
