/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.security.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 框架默认鉴权类型枚举
 *
 * @author NorthLan
 * @date 2020-03-02
 * @url https://noahlan.com
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ITIAuthType implements IAuthType {
    USERNAME("username", "用户名"),
    MOBILE("mobile", "手机号"),
    GITEE("gitee", "码云"),
    WEIBO("weibo", "微博"),
    QQ("qq", "腾讯QQ"),
    WECHAT("wechat", "微信");

    private String type;
    private String desc;

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getDescription() {
        return this.desc;
    }
}
