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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 插件接口
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
public interface IPlugin {

    /**
     * 插件代码,在JVM内唯一
     *
     * @return 插件代码, JVM中唯一
     */
    String getCode();

    /**
     * 插件版本
     *
     * @return 插件版本
     */
    String getVersion();

    /**
     * 预加载的Class
     * {annotationClass, classes}
     *
     * @param jarPath                    jar路径
     * @param identityResolverAnnotation annotation
     * @return 该插件需加载的类
     * @throws Throwable 任意异常
     */
    Map<Class<? extends Annotation>, List<Class<?>>> prepareClasses(
            String jarPath,
            Class<? extends Annotation> identityResolverAnnotation) throws Throwable;

    /**
     * 预加载的插件
     * @param identityResolverAnnotation
     * @param plugableMap
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    void preparePlugins(
            Class<? extends Annotation> identityResolverAnnotation,
            Map<Class<? extends Annotation>, List<Class<?>>> plugableMap) throws IllegalAccessException, InstantiationException;
}
