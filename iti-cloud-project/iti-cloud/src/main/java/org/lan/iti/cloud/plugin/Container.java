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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.plugin.util.FileUtils;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 业务容器，用于动态加载个性化业务包：Plugin Jar.
 * 单例模式，常驻内存
 * <p>
 * <p>{@code Container}常驻内存，{@code PluginJar}动态加载：动静分离</p>
 * <p>
 * <pre>
 *    +- 1 containerClassLoader
 *    |- 1 jdkClassLoader
 *    |- 1 containerApplicationContext
 *    |
 *    |                     +- pluginApplicationContext
 *    |                     |
 * Container ----> Plugin --+- pluginClassLoader
 *             N                    | loadClass
 *                        +---------------------+
 *                        |                     |
 *                  [Partner | Pattern]      Extension
 * </pre>
 *
 * @author NorthLan
 * @date 2021-02-23
 * @url https://noahlan.com
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Container {
    private static final String JAR_SUFFIX = ".jar";
    private static final Container INSTANCE = new Container();

    private static final ClassLoader JDK_CLASS_LOADER = initJDKClassLoader();
    private static final ClassLoader CONTAINER_CLASS_LOADER = Container.class.getClassLoader();

    private static final Map<String, IPlugin> ACTIVE_PLUGINS = new HashMap<>();

    private static ApplicationContext CONTAINER_APPLICATION_CONTEXT;

    /**
     * 获取业务容器单例.
     */
    @NotNull
    public static Container getInstance() {
        return INSTANCE;
    }

    /**
     * 获取当前所有活跃的{@code Plugin}.
     *
     * @return key: Plugin code
     */
    @NotNull
    public Map<String, IPlugin> getActivePlugins() {
        return ACTIVE_PLUGINS;
    }

    /**
     * 设置Container的Spring上下文
     *
     * @param containerApplicationContext Container的Spring上下文
     */
    public void setContainerApplicationContext(ApplicationContext containerApplicationContext) {
        CONTAINER_APPLICATION_CONTEXT = containerApplicationContext;
    }

    /**
     * 加载业务前台jar包.
     * <p>
     * <p>如果使用本动态加载，就不要maven里静态引入业务前台jar包依赖了.</p>
     *
     * @param plugin          所使用的的插件描述
     * @param annotationClass 用于识别的注解类名
     * @param jarUrl          Plugin jar URL
     * @param useSpring       jar包是否需要spring机制
     * @throws Throwable 异常
     */
    public synchronized void loadPlugin(@NotNull AbstractPlugin plugin, Class<? extends Annotation> annotationClass, @NotNull URL jarUrl, boolean useSpring) throws Throwable {
        File localJar = createLocalFile(jarUrl);
        localJar.deleteOnExit();
        log.info("loadPartnerPlugin {} -> {}", jarUrl, localJar.getCanonicalPath());
        FileUtils.copyInputStreamToFile(jarUrl.openStream(), localJar);
        loadPlugin(plugin, annotationClass, localJar.getAbsolutePath(), useSpring);
    }

    /**
     * 加载业务前台jar包.
     * <p>
     * <p>如果使用本动态加载，就不要maven里静态引入业务前台jar包依赖了.</p>
     *
     * @param plugin          所使用的的插件描述
     * @param annotationClass 用于识别的注解类名
     * @param jarPath         jar path
     * @param useSpring       jar包是否需要spring机制
     * @throws Throwable 异常
     */
    public synchronized void loadPlugin(@NotNull AbstractPlugin plugin, Class<? extends Annotation> annotationClass, @NotNull String jarPath, boolean useSpring) throws Throwable {
        if (!jarPath.endsWith(JAR_SUFFIX)) {
            throw new IllegalArgumentException("Invalid jarPath: " + jarPath);
        }

        long t0 = System.nanoTime();
        log.warn("Loading partner:{} useSpring:{}", jarPath, useSpring);
        try {
            plugin.initContext(JDK_CLASS_LOADER, CONTAINER_CLASS_LOADER, CONTAINER_APPLICATION_CONTEXT);
            plugin.load(jarPath, useSpring, annotationClass, new ContainerContext(CONTAINER_APPLICATION_CONTEXT));

            AbstractPlugin pluginToDestroy = (AbstractPlugin) ACTIVE_PLUGINS.get(plugin.getCode());
            if (pluginToDestroy != null) {
                log.warn("to destroy partner:{} ver:{}", plugin.getCode(), plugin.getVersion());
                pluginToDestroy.onDestroy();
            }

            // old plugin will be GC'ed eventually
            ACTIVE_PLUGINS.put(plugin.getCode(), plugin);

            log.warn("Loaded partner:{}, cost {}ms", jarPath, (System.nanoTime() - t0) / 1000_000);
        } catch (Throwable ex) {
            log.error("fails to load partner:{}, cost {}ms", jarPath, (System.nanoTime() - t0) / 1000_000, ex);

            throw ex;
        }
    }

    File createLocalFile(@NotNull URL jarUrl) throws IOException {
        String prefix = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String suffix = jarUrl.getPath().substring(jarUrl.getPath().lastIndexOf("/") + 1);
        File file = File.createTempFile(prefix, "." + suffix);
        file.deleteOnExit();
        return file;
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    private static ClassLoader initJDKClassLoader() {
        ClassLoader parent = ClassLoader.getSystemClassLoader();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }

        List<URL> jdkUrls = new ArrayList<>(100);
        try {
            // javaHome: C:/Program Files/java/jdk1.8.0_181/
            String javaHome = System.getProperty("java.home").replace(File.separator + "jre", "");
            // search path of URLs for loading classes and resources
            URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
            for (URL url : urls) {
                if (url.getPath().startsWith(javaHome)) {
                    // 只找JDK本身的
                    jdkUrls.add(url);
                }
            }
        } catch (Throwable shouldNeverHappen) {
            log.error("JDKClassLoader", shouldNeverHappen);
        }

        return new URLClassLoader(jdkUrls.toArray(new URL[0]), parent);
    }
}
