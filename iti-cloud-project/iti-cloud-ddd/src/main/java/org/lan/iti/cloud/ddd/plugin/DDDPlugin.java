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

package org.lan.iti.cloud.ddd.plugin;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.ddd.annotation.Extension;
import org.lan.iti.cloud.ddd.annotation.Partner;
import org.lan.iti.cloud.ddd.runtime.registry.InternalIndexer;
import org.lan.iti.cloud.ddd.runtime.registry.RegistryFactory;
import org.lan.iti.cloud.plugin.AbstractPlugin;
import org.lan.iti.cloud.plugin.PluginApplicationContext;
import org.lan.iti.cloud.plugin.util.JarUtils;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A Plugin is a dynamic loadable Jar that has a dedicated class loader.
 * <p>
 * <p>Plugin Jar，业务扩展包，是可以被动态加载的Jar = (Pattern + Extension) | (Partner + Extension)</p>
 * <p>基于 ClassLoader 实现的业务模块隔离，每个Plugin Jar可以有独立的Spring上下文.</p>
 *
 * @author NorthLan
 * @date 2021-02-23
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
@Deprecated
class DDDPlugin extends AbstractPlugin {

    protected DDDPlugin(String code,
                        String version,
                        ClassLoader jdkClassLoader,
                        ClassLoader containerClassLoader,
                        ApplicationContext containerApplicationContext) {
        super(code, version, jdkClassLoader, containerClassLoader, containerApplicationContext);
    }

    protected DDDPlugin(String code, String version) {
        super(code, version);
    }

    @Override
    public Map<Class<? extends Annotation>, List<Class<?>>> prepareClasses(
            String jarPath,
            Class<? extends Annotation> identityResolverAnnotation) throws Throwable {
        if (useSpring) {
            log.debug("Spring loading Plugin with {}, {}, {} ...", jdkClassLoader, containerClassLoader, pluginClassLoader);
            long t0 = System.nanoTime();

            pluginApplicationContext = new PluginApplicationContext(configLocations, containerApplicationContext, pluginClassLoader);
            pluginApplicationContext.refresh();

            log.info("Spring {} loaded, cost {}ms", configLocations, (System.nanoTime() - t0) / 1000_000);
        }

        // 从Plugin Jar里把 IPlugable 挑出来，以便更新注册表
        List<Class<? extends Annotation>> annotations = new ArrayList<>(2);
        annotations.add(identityResolverAnnotation);
        annotations.add(Extension.class);
        return JarUtils.loadClassWithAnnotations(jarPath, annotations, null, pluginClassLoader);
    }

    @Override
    public void preparePlugins(Class<? extends Annotation> identityResolverAnnotation,
                               Map<Class<? extends Annotation>, List<Class<?>>> plugableMap) throws IllegalAccessException, InstantiationException {
        List<Class<?>> identityResolverClasses = plugableMap.get(identityResolverAnnotation);
        if (identityResolverClasses != null && !identityResolverClasses.isEmpty()) {
            if (identityResolverAnnotation == Partner.class && identityResolverClasses.size() > 1) {
                throw new RuntimeException("One Partner jar can have at most 1 Partner instance!");
            }

            for (Class<?> irc : identityResolverClasses) {
                log.info("Preparing index {} {}", identityResolverAnnotation.getSimpleName(), irc.getCanonicalName());

                if (useSpring) {
                    // 每次加载，由于 PluginClassLoader 是不同的，irc也不同
                    Object partnerOrPattern = pluginApplicationContext.getBean(irc);
                    RegistryFactory.preparePlugins(identityResolverAnnotation, partnerOrPattern);
                } else {
                    // new instance here
                    RegistryFactory.preparePlugins(identityResolverAnnotation, irc.newInstance());
                }
            }
        }

        List<Class<?>> extensions = plugableMap.get(Extension.class);
        if (extensions != null && !extensions.isEmpty()) {
            for (Class<?> extensionClazz : extensions) {
                log.info("Preparing index Extension {}", extensionClazz.getCanonicalName());

                // 这里extensionClazz是扩展点实现的类名 e,g. org.example.bp.oms.isv.extension.DecideStepsExt
                // 而不是 IDecideStepsExt。因此，不必担心getBean异常：一个extensionClazz有多个对象
                if (useSpring) {
                    Object extension = pluginApplicationContext.getBean(extensionClazz);
                    RegistryFactory.preparePlugins(Extension.class, extension);
                } else {
                    // new instance here
                    RegistryFactory.preparePlugins(Extension.class, extensionClazz.newInstance());
                }
            }
        }
    }

    @Override
    protected void commit(Class<? extends Annotation> identityResolverAnnotation) {
        if (identityResolverAnnotation == Partner.class) {
            InternalIndexer.commitPartner();
        }
    }
}
