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

package org.lan.iti.cloud.plugin;

/**
 * 插件监听器
 * <p>
 * <p>监听启动、关闭、预加载</p>
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
public interface IPluginListener {

    /**
     * 插件包prepare完成时触发.
     * <p>
     * <p>此时，插件包里的类刚刚加载和实例化，还没有被调用</p>
     *
     * @param ctx 容器上下文
     * @throws Exception exception
     */
    void onPrepared(IContainerContext ctx) throws Exception;

    /**
     * 插件包切换完成时触发.
     * <p>
     * <p>此时，相应的请求会发送该插件包里的类</p>
     *
     * @param ctx 容器上下文
     * @throws Exception exception
     */
    void onCommitted(IContainerContext ctx) throws Exception;
}
