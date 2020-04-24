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

package org.lan.iti.common.security.social.security.provider;

import org.springframework.beans.factory.InitializingBean;


/**
 * 抽象类
 * <p>抽象公共方法</p>
 *
 * @author NorthLan
 * @date 2020-04-01
 * @url https://noahlan.com
 */
public abstract class AbstractSocialAuthenticationService<S> implements SocialAuthenticationService<S>, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
