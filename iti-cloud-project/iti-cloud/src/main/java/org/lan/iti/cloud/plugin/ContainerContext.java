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

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;

/**
 * 默认的容器上下文实现.
 * <p>
 * <p>有些{@code Plugin}不希望Spring加载，但需要中台的资源(例如：RPC/Redis/MQ)，中台输出受限的Spring容器供使用</p>
 *
 * @author NorthLan
 * @date 2021-02-23
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class ContainerContext implements IContainerContext {
    private final ApplicationContext containerApplicationContext;

    @Override
    public <T> T getBean(@NotNull Class<T> requiredType) throws RuntimeException {
        return containerApplicationContext.getBean(requiredType);
    }

    @Override
    public <T> T getBean(@NotNull String name, @NotNull Class<T> requiredType) throws RuntimeException {
        return containerApplicationContext.getBean(name, requiredType);
    }
}
