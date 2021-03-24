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

import javax.validation.constraints.NotNull;

/**
 * 插件容器上下文
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
public interface IContainerContext {

    /**
     * Return the bean instance that uniquely matches the given object type, if any.
     *
     * @param requiredType type the bean must match; can be an interface or superclass
     * @param <T>          type
     * @return an instance of the single bean matching the required type
     * @throws RuntimeException exception
     */
    <T> T getBean(@NotNull Class<T> requiredType) throws RuntimeException;

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     *
     * @param name         the name of the bean to retrieve
     * @param requiredType type the bean must match; can be an interface or superclass
     * @param <T>          type
     * @return an instance of the bean
     * @throws RuntimeException exception
     */
    <T> T getBean(@NotNull String name, @NotNull Class<T> requiredType) throws RuntimeException;
}
