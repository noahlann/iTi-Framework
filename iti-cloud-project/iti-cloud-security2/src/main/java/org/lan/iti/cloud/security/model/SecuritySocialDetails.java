/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.security.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 安全-社交信息数据模型
 *
 * @author NorthLan
 * @date 2020-05-26
 * @url https://noahlan.com
 */
@Data
public class SecuritySocialDetails implements Serializable {
    private static final long serialVersionUID = -8066818207108645806L;

    /**
     * 类型
     */
    private String type;

    /**
     * app_id
     */
    private String appId;

    /**
     * app_secret
     */
    private String appSecret;

    /**
     * 回调地址
     */
    private String redirectUrl;

    /**
     * 认证地址
     */
    private String authorizationUrl;

    /**
     * 获取用户信息地址
     */
    private String userInfoUrl;
}
