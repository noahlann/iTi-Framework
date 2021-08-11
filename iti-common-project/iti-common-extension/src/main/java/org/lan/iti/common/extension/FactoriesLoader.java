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

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.ConcurrentReferenceHashMap;
import org.lan.iti.common.core.util.StringUtil;
import org.reflections.ReflectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author NorthLan
 * @date 2021-07-09
 * @url https://noahlan.com
 * @see org.springframework.core.io.support.SpringFactoriesLoader
 */
@UtilityClass
@Slf4j
public class FactoriesLoader {

    private static final String XML_FILE_EXTENSION = ".xml";

    /**
     * The location to look for factories.
     * <p>Can be present in multiple JAR files.
     */
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/iti-ext.properties";

    static final Map<ClassLoader, Map<String, Map<String, String>>> cache = new ConcurrentReferenceHashMap<>();

    public static Set<Class<?>> loadFactoryClassesSet(String factoryTypeName, ClassLoader classLoader) {
        return new HashSet<>(loadFactoryClasses(factoryTypeName, classLoader).values());
    }

    public static Map<String, Class<?>> loadFactoryClasses(String factoryTypeName, ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = FactoriesLoader.class.getClassLoader();
        }
        Map<String, String> factoryImplementationNames = loadFactoryNames(factoryTypeName, classLoaderToUse);
        Map<String, Class<?>> result = new HashMap<>(factoryImplementationNames.keySet().size());

        for (Map.Entry<String, String> entry : factoryImplementationNames.entrySet()) {
            Class<?> clazz = ReflectionUtils.forName(entry.getValue(), classLoaderToUse);
            if (clazz != null) {
                result.putIfAbsent(entry.getKey(), clazz);
            }
        }
        return result;
    }

    public static Map<String, String> loadFactoryNames(String factoryTypeName, ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = FactoriesLoader.class.getClassLoader();
        }
        return loadFactoriesMap(classLoaderToUse).getOrDefault(factoryTypeName, Collections.emptyMap());
    }

    public static Map<String, Map<String, String>> loadFactoriesMap(ClassLoader classLoader) {
        Map<String, Map<String, String>> result = cache.get(classLoader);
        if (result != null) {
            return result;
        }

        result = new HashMap<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCE_LOCATION);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                    String line;
                    String factoryTypeName = null;

                    while ((line = reader.readLine()) != null) {
                        // comment index
                        final int ci = line.indexOf('#');
                        if (ci >= 0) {
                            line = line.substring(0, ci);
                        }
                        // symbol index
                        final int si = line.indexOf('@');
                        if (si >= 0) {
                            line = line.substring(si + 1);
                            // factoryTypeName
                            factoryTypeName = line.trim();
                            continue;
                        }
                        line = line.trim();
                        if (StringUtil.isNotEmpty(factoryTypeName) && line.length() > 0) {
                            String name;
                            String clazz;
                            final int ei = line.indexOf('=');
                            final Map<String, String> map = result.computeIfAbsent(factoryTypeName, it -> new HashMap<>());
                            if (ei > 0) {
                                name = line.substring(0, ei).trim();
                                clazz = line.substring(ei + 1).trim();
                                // put simple class name: className
                                map.put(getSimpleName(clazz), clazz);
                            } else {
                                clazz = line;
                                name = getSimpleName(clazz);
                            }
                            map.put(name, clazz);
                        }
                    }
                }
            }
            cache.put(classLoader, result);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load factories from location [" +
                    FACTORIES_RESOURCE_LOCATION + "]", e);
        }
        return result;
    }

    private static String getSimpleName(String className) {
        // last comma index
        // foo.bar.xxx = 7
        final int lci = className.lastIndexOf('.');
        if (lci > 0) {
            return StringUtil.lowerFirst(className.substring(lci + 1));
        } else {
            return StringUtil.lowerFirst(className);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateFactory(String factoryImplementationName, ClassLoader classLoader) {
        try {
            Class<?> factoryImplementationClass = ReflectionUtils.forName(factoryImplementationName, classLoader);
            return (T) factoryImplementationClass.getDeclaredConstructor().newInstance();
        } catch (Throwable ex) {
            throw new IllegalArgumentException(
                    "Unable to instantiate factory class [" + factoryImplementationName + "]", ex);
        }
    }

    private static Properties loadProperties(URL url) {
        Properties props = new Properties();
        try (InputStream is = url.openStream()) {
            String filename = url.getFile();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                throw new UnsupportedOperationException("XML support disabled");
            } else {
                props.load(is);
            }
        } catch (Throwable t) {
            throw new IllegalArgumentException("Unable to load factories from location [" +
                    FACTORIES_RESOURCE_LOCATION + "]", t);
        }
        return props;
    }
}
