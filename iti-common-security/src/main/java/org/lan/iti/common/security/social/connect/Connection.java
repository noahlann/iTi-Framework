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

import java.io.Serializable;

/**
 * 指向服务提供商用户的链接。
 * <p>
 * 允许客户端应用程序使用提供者的API访问或更新用户信息
 * 公开所有服务提供商之间通用的一组操作
 *
 * @author NorthLan
 * @date 2020-03-17
 * @url https://noahlan.com
 */
public interface Connection<A> extends Serializable {
    /**
     * Key
     *
     * @return 一个由providerId（例如"wechat"）加上providerUserId（例如"125660"）组成的组合键
     */
    ConnectionKey getKey();

    /**
     * 此连接的显示名称或标签
     * 应该适合在UI上显示，并将此连接与具有相同提供程序的其他连接区分开
     * 通常是所连接的提供者用户的全名或屏幕名称，例如"张三"或"@zs"
     * 如果此信息不是公开的或未提供，则可以为null
     * 如果用户更新其个人资料，则此属性的值可能会更改。
     *
     * @see #sync()
     */
    String getDisplayName();

    /**
     * 提供者站点上已连接用户的个人资料的公共URL。
     * 客户端应用程序可以将此值与displayName一起使用，以生成指向提供商系统上用户个人资料的链接。
     * 如果此信息不是公开的或未提供，则可以为null。
     * 如果用户更新其个人资料，则此属性的值可能会更改。
     *
     * @return the public URL for the connected user
     * @see #sync()
     */
    String getProfileUrl();

    /**
     * 链接到可视化此连接的图像
     * 应该在视觉上将这种连接与相同提供商的其他连接区分开
     * 通常，所连接的提供者用户的个人资料图片的小/缩略图版本
     * 如果此信息不是公开的或未提供，则可以为null
     * 如果用户更新其个人资料，则此属性的值可能会更改
     *
     * @see #sync()
     */
    String getImageUrl();

    /**
     * 将此连接对象与外部用户的配置文件的当前状态同步
     * 触发本地缓存的配置文件字段以在提供者的系统上发生更改时进行更新
     */
    void sync();

    /**
     * 测试此连接
     * 如果为false，则表示对{@link #getApi() api}的调用将失败
     * 用于在调用服务API之前主动测试授权凭证，例如API访问令牌
     *
     * @return 如果连接有效，则为true
     */
    boolean test();

    /**
     * 如果此连接已过期，则返回true
     * 无法使用过期的连接；对{@link #test()}的调用返回false,并且任何服务API调用都会失败
     * 如果已过期，则可以调用{@link #refresh()}来更新连接
     * 并非所有Connection实现都支持；如果不受支持，则始终返回false
     *
     * @return 如果连接已过期，则为true
     */
    boolean hasExpired();

    /**
     * 刷新此连接
     * 用于续订过期的连接
     * 如果刷新操作成功，则{@link #hasExpired()}返回false
     * 并非所有连接实现都支持；如果不支持，则此方法为无操作
     */
    void refresh();

    // TODO fetchUserProfile

    /**
     * 在提供者的系统上更新用户的状态
     * 如果服务提供商不支持状态概念，则此方法禁止操作
     *
     * @param message 状态消息
     */
    void updateStatus(String message);

    /**
     * 与服务提供商的API
     *
     * @return 提供者特定的API
     */
    A getApi();

    /**
     * 创建一个数据传输对象，该对象可用于保留此连接的状态
     * 用于支持在应用程序各层之间（例如到数据库层）之间的连接状态传输
     *
     * @return 包含有关连接的详细信息的数据传输对象
     */
    ConnectionData createData();
}
