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

package org.lan.iti.iha.security.processor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lan.iti.common.core.support.IEnum;
import org.lan.iti.common.core.util.StringUtil;

/**
 * 处理器类型
 *
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ProcessorType implements IEnum<String> {
    SIMPLE("simple", "简单登录方式"),
    OAUTH2("oauth2", "OAuth2登录方式"),
    OAUTH2_REVOKE("oauth2-revoke", "OAuth2-回收token"),
    OIDC("oidc", "OIDC登录方式"),
    OIDC_REVOKE("oidc-revoke", "OIDC-回收token，复用OAuth2方法"),
    SOCIAL("social", "社交登录"),
    SAML2("saml2", "SAML2"),
    LDAP("ldap", "LDAP"),
    ;
    public static final String KEY = "$processorType";
    private final String code;
    private final String message;

    public boolean matches(String code) {
        return StringUtil.equals(this.code, code);
    }
}
