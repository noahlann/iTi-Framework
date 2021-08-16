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

package org.lan.iti.common.extension;

import org.lan.iti.common.extension.annotation.Extension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Extension注入器
 *
 * @author NorthLan
 * @date 2021-07-07
 * @url https://noahlan.com
 */
@Extension
public interface ExtensionInjector extends IExtension<Object> {

    @Override
    default boolean matches(Object params) {
        return true;
    }

    /**
     * 直接进行注入,子类选择使用参数
     *
     * @param instance 实例
     * @param fields   fields列表（包括父类）
     * @param setters  setter方法
     * @param <T>      实例类型
     * @return 注入后的实例
     */
    <T> T inject(T instance, Field[] fields, Method[] setters);
}
