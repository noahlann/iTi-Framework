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

package org.lan.iti.cloud.security.config.annotation;

/**
 * 允许对 {@link SecurityBuilder} 进行配置
 * <p>
 * 所有的 {@link SecurityConfigurer} 首先调用其初始化方法 {@link #init(SecurityBuilder)}
 * 而后调用具体的配置方法 {@link #configure(SecurityBuilder)}
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public interface SecurityConfigurer<O, B extends SecurityBuilder<O>> {

    /**
     * 初始化 {@link SecurityBuilder}
     * <p>
     * 这里只应创建和修改共享状态，而不是用于构建对象的 {@link SecurityBuilder} 上的属性。
     * 这确保 {@link #configure(SecurityBuilder)} 方法在构建时使用正确的共享对象。
     * <br>
     * configurer 应在此处调用
     *
     * @param builder builder
     * @throws Exception
     */
    void init(B builder) throws Exception;

    /**
     * 配置 {@link SecurityBuilder}
     * <p>
     * 配置 builder 的属性
     *
     * @param builder builder
     * @throws Exception
     */
    void configure(B builder) throws Exception;

}