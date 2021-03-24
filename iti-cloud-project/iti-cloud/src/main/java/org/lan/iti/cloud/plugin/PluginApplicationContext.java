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

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.lang.NonNull;

/**
 * 每个Plugin特有的Spring application context，使用Plugin特有的class loader
 *
 * @author NorthLan
 * @date 2021-02-23
 * @url https://noahlan.com
 */
public class PluginApplicationContext extends ClassPathXmlApplicationContext {
    private final ClassLoader pluginClassLoader;

    public PluginApplicationContext(String[] configLocations, ApplicationContext parent, ClassLoader pluginClassLoader) {
        // will not refresh
        super(configLocations, false, parent);
        this.pluginClassLoader = pluginClassLoader;
    }

    @Override
    protected void initBeanDefinitionReader(@NonNull XmlBeanDefinitionReader reader) {
        super.initBeanDefinitionReader(reader);
        reader.setBeanClassLoader(pluginClassLoader);
        // so that it can find the pluginXml within the jar
        setClassLoader(pluginClassLoader);
    }
}
