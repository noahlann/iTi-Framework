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

package org.lan.iti.common.security.social.mobile.connect;

import org.lan.iti.common.security.social.connect.support.NopConnectionFactory;
import org.lan.iti.common.security.social.mobile.api.Mobile;

/**
 * 手机号 连接工厂
 *
 * @author NorthLan
 * @date 2020-04-02
 * @url https://noahlan.com
 */
public class MobileConnectionFactory extends NopConnectionFactory<Mobile> {
    public MobileConnectionFactory() {
        super("mobile", new MobileServiceProvider(), new MobileAdapter());
    }
}
