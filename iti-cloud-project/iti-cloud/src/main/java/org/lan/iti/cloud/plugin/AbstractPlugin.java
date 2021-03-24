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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.plugin.util.JarUtils;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * A Plugin is a dynamic loadable Jar that has a dedicated class loader.
 * <p>
 * <p>Plugin Jar，业务扩展包，是可以被动态加载的Jar</p>
 * <p>基于 ClassLoader 实现的业务模块隔离，每个Plugin Jar可以有独立的Spring上下文.</p>
 *
 * @author NorthLan
 * @date 2021-02-23
 * @url https://noahlan.com
 */
@Slf4j
public abstract class AbstractPlugin implements IPlugin {
    @Getter
    protected final String code;

    /**
     * 回滚用
     */
    @Getter
    protected final String version;

    /**
     * 内存共享的 classLoader
     */
    protected ClassLoader jdkClassLoader;
    protected ClassLoader containerClassLoader;

    /**
     * pluginApplicationContext 的 父级 ApplicationContext
     */
    protected ApplicationContext containerApplicationContext;
    /**
     * Spring 配置文件路径
     */
    protected String[] configLocations;

    /**
     * each Plugin will have a specific plugin class loader
     */
    protected ClassLoader pluginClassLoader;

    /**
     * 是否使用Spring的类加载
     */
    protected boolean useSpring;

    /**
     * each Plugin will have a specific Spring IoC with the same parent: the Container
     */
    protected PluginApplicationContext pluginApplicationContext;

    protected AbstractPlugin(String code,
                             String version,
                             ClassLoader jdkClassLoader,
                             ClassLoader containerClassLoader,
                             ApplicationContext containerApplicationContext) {
        this.code = code;
        this.version = version;
        this.jdkClassLoader = jdkClassLoader;
        this.containerClassLoader = containerClassLoader;
        this.containerApplicationContext = containerApplicationContext;

        // 由于pluginApplicationContext.parent is containerApplicationContext
        // 如都使用相同的plugin.xml，会造成Pattern Jar的plugin.xml冲了Partner Jar里的plugin.xml：假如Pattern Jar是静态加载的
        // 为此，让每个业务扩展包的配置文件不同，保证不冲突
        this.configLocations = new String[]{"/plugin-" + code + ".xml"};
    }

    protected AbstractPlugin(String code, String version) {
        this(code, version, null, null, null);
    }

    public void initContext(ClassLoader jdkClassLoader,
                            ClassLoader containerClassLoader,
                            ApplicationContext containerApplicationContext){
        this.jdkClassLoader = jdkClassLoader;
        this.containerClassLoader = containerClassLoader;
        this.containerApplicationContext = containerApplicationContext;
    }

    public void load(String jarPath, boolean useSpring, Class<? extends Annotation> identityResolverAnnotation, IContainerContext ctx) throws Throwable {
        this.useSpring = useSpring;

        // each Plugin Jar has a specific PluginClassLoader
        pluginClassLoader = new PluginClassLoader(new URL[]{new File(jarPath).toURI().toURL()}, jdkClassLoader, containerClassLoader);

        // Spring load classes in jar
        // Spring load all relevant classes in the jar using the new PluginClassLoader
        Map<Class<? extends Annotation>, List<Class<?>>> plugableMap = prepareClasses(jarPath, identityResolverAnnotation);
        log.info("Classes prepared, plugableMap {}", plugableMap);

        // IPluginListener 不通过Spring加载，而是手工加载、创建实例
        // 如果一个jar里有多个 IPluginListener 实现，只会返回第一个实例
        IPluginListener pluginListener = JarUtils.loadBeanWithType(pluginClassLoader, jarPath, IPluginListener.class);
        if (pluginListener != null) {
            pluginListener.onPrepared(ctx);
        }

        // 现在，新jar里的类已经被新的ClassLoader加载到内存了，也实例化了，但旧jar里的类仍然在工作
        preparePlugins(identityResolverAnnotation, plugableMap);
        log.info("Plugins index prepared for {}", identityResolverAnnotation.getSimpleName());

        // 内存里插件相关索引已准备好，现在切换
        internalCommit(identityResolverAnnotation);
        log.info("Committed: {}", identityResolverAnnotation.getSimpleName());

        if (pluginListener != null) {
            pluginListener.onCommitted(ctx);
        }
    }

    public void onDestroy() {
        // 把该Plugin下的所有类的所有引用处理干净，这样才能GC介入
        if (useSpring) {
            pluginApplicationContext.close();
        }
    }

    @Override
    public String toString() {
        return "Plugin:" + code + ":" + version;
    }

    private void internalCommit(Class<? extends Annotation> identityResolverAnnotation) {
        commit(identityResolverAnnotation);
    }

    protected abstract void commit(Class<? extends Annotation> identityResolverAnnotation);
}
