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

package org.lan.iti.common.security.social.connect;

import java.util.Set;

/**
 * 用于{@link ConnectionFactory}实例的ServiceLocator
 * 支持按providerId和apiType查找。
 *
 * @author NorthLan
 * @date 2020-03-18
 * @url https://noahlan.com
 */
public interface ConnectionFactoryLocator {
    /**
     * 通过providerId查找ConnectionFactory
     * 返回的工厂可用于创建与提供程序的连接
     * 用于支持在一组注册的提供程序之间以动态方式创建连接
     *
     * @param providerId 用于查找ConnectionFactory的 providerId
     * @return ConnectionFactory
     */
    ConnectionFactory<?> getConnectionFactory(String providerId);

    /**
     * 通过apiType查找ConnectionFactory 例如: WechatApi.class
     *
     * @param apiType API绑定的Java类型，用于查找匹配的ConnectionFactory
     * @param <A>     API绑定类型
     * @return ConnectionFactory
     */
    <A> ConnectionFactory<A> getConnectionFactory(Class<A> apiType);

    /**
     * 返回为其注册了{@link ConnectionFactory}的providerId的集合。例如：<code>{ "wechat", "qq", "gitee" }</code>
     */
    Set<String> registeredProviderIds();
}
