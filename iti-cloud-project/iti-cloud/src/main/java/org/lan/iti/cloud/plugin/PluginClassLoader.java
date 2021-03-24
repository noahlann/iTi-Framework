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

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Plugin类加载器.
 * <p>
 * <pre>
 *     sun.misc.Launcher$AppClassLoader
 *                   |
 *        +--------------------+
 *        |                    |
 *  JDKClassLoader   ContainerClassLoader($AppClassLoader)
 *                             |
 *      +------------------------------------------------+
 *      |                         |                      |
 *  PluginClassLoader      PluginClassLoader      PluginClassLoader
 *      |                         |                      |
 *  PluginJar                 PluginJar               PluginJar
 * </pre>
 *
 * @author NorthLan
 * @date 2021-02-23
 * @url https://noahlan.com
 */
@Slf4j
public class PluginClassLoader extends URLClassLoader {
    // TODO 配置
    private static final String ITI_PACKAGE = "org.lan.iti";

    private final ClassLoader jdkClassLoader;
    private final ClassLoader containerClassLoader;

    public PluginClassLoader(URL[] urls, ClassLoader jdkClassLoader, ClassLoader containerClassLoader) {
        super(urls);
        for (URL url : urls) {
            addURL(url);
        }
        this.jdkClassLoader = jdkClassLoader;
        this.containerClassLoader = containerClassLoader;
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = this.findLoadedClass(className);
        if (clazz != null) {
            // 如果类已经加载过，就返回那个已经加载好的类
            return clazz;
        }

        // 如果这个类是JDK自己的，就用 JDKClassLoader 加载
        try {
            clazz = jdkClassLoader.loadClass(className);
            if (clazz != null) {
                // 说明该类是JRE的类
                log.debug("loaded {} with {}", className, jdkClassLoader);
                return clazz;
            }
        } catch (ClassNotFoundException ignored) {
        }

        // 不是JDK本身的类
        if (containerFirstClass(className)) {
            clazz = containerClassLoader.loadClass(className);
            if (clazz != null) {
                log.debug("loaded {} with {}", className, containerClassLoader);
                return clazz;
            }
        }

        // Plugin加载器自己加载
        try {
            // look for classes in the file system(jar)
            clazz = this.findClass(className);
            if (clazz != null) {
                log.info("loaded {} with {}", className, this);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }
        } catch (ClassNotFoundException ignored) {
        }

        // 如果Plugin加载器无法加载，fallback to 中台Container加载器
        try {
            clazz = containerClassLoader.loadClass(className);
            if (clazz != null) {
                log.debug("loaded {} with {}", className, containerClassLoader);
                return clazz;
            }
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    /**
     * 中台Container优先加载的类
     *
     * @param className 类名
     * @return true代表此类名符合要求
     */
    boolean containerFirstClass(String className) {
        return className != null && className.startsWith(ITI_PACKAGE);
    }
}
