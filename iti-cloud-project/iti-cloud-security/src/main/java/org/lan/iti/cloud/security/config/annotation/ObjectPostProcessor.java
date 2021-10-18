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

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 允许初始化对象。
 * 通常，这用于调用 {@link Aware} 方法 {@link InitializingBean#afterPropertiesSet()}
 * 并确保已调用 {@link DisposableBean#destroy()}。
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public interface ObjectPostProcessor<T> {
    /**
     * 初始化对象可能会返回一个被修改的实例
     *
     * @param object the object to initialize
     * @return the initialized version of the object
     */
    <O extends T> O postProcess(O object);
}
