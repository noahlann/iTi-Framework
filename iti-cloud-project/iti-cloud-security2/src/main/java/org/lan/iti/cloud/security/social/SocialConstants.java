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

package org.lan.iti.cloud.security.social;

/**
 * 社交相关常量
 *
 * @author NorthLan
 * @date 2021-03-17
 * @url https://noahlan.com
 */
public interface SocialConstants {
    /**
     * 提供商ID
     * <p>内建 {@link DefaultProvider}</p>
     */
    String PROVIDE_ID_KEY = "pid";

    interface DefaultProvider {
        /**
         * 手机号+密码
         * <p>注：可能会存在需要验证码验证的情况</p>
         */
        String MOBILE_PASSWORD = "mobile";

        /**
         * 手机号+验证码
         */
        String MOBILE_CODE = "mobile_code";

        /**
         * 微信
         */
        String WECHAT = "wechat";

        /**
         * QQ
         */
        String QQ = "qq";

        /**
         * 微博
         */
        String WEIBO = "weibo";
    }
}
