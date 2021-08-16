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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.ClassLoaderUtil;
import org.lan.iti.common.core.util.ReflectUtil;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.common.extension.adapter.ExtensionAdapter;
import org.lan.iti.common.extension.adapter.ExtensionAdapterFactory;
import org.lan.iti.common.extension.support.NamedClassCache;
import org.lan.iti.common.extension.util.Holder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Load extensions
 * <ul>
 *     <li>auto inject dependency extensions</li>
 *     <li>auto wrap extensions in wrapper</li>
 *     <li>default extensions is an adaptive instance</li>
 * </ul>
 *
 * @author NorthLan
 * @date 2021-07-07
 * @url https://noahlan.com
 * @see <a href="http://java.sun.com/j2se/1.5.0/docs/guide/jar/jar.html#Service%20Provider">Service Provider in Java 5</a>
 * @see org.lan.iti.common.extension.annotation.Extension
 */
@Slf4j
public class ExtensionLoader<T extends IExtension<P>, P> {
    /**
     * 全局缓存 {Class<T> : ExtensionLoader<T>}
     */
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?, ?>> EXTENSION_LOADERS = new ConcurrentHashMap<>(64);

    /**
     * 全局缓存 {Class<T> : {name: Instance}}
     */
    private static final ConcurrentMap<Class<?>, NamedClassCache<Object>> EXTENSION_INSTANCES = new ConcurrentHashMap<>(64);

    /**
     * 全局缓存 {Class<T> : {name: Class<? extends T>}}
     */
    private static final ConcurrentMap<Class<?>, NamedClassCache<Class<?>>> EXTENSION_CLASSES = new ConcurrentHashMap<>(255);

    /**
     * 使用别名时的参数key前缀
     * <p>
     * 作用于 cachedParamsInstances {key}
     */
    private static final String PREFIX_NAMED_HOLDER = "$NAME_";

    /**
     * 局部缓存 {P, Holder<Object>}
     */
    private final ConcurrentMap<Object, Holder<List<T>>> cachedParamsInstances = new ConcurrentHashMap<>();

//    /**
//     * 局部缓存 String(name) : Holder<Instance>
//     */
//    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

//    private final Map<String, Object> cachedActivates = Collections.synchronizedMap(new LinkedHashMap<>());
//    private final Map<String, Set<String>> cachedActivateGroups = Collections.synchronizedMap(new LinkedHashMap<>());
//    private final Map<String, String[]> cachedActivateValues = Collections.synchronizedMap(new LinkedHashMap<>());

//    private final Holder<Object> cachedAdaptiveInstance = new Holder<>();
//    private volatile Class<?> cachedAdaptiveClass = null;
//    private String cachedDefaultName;
//    private volatile Throwable createAdaptiveInstanceError;

//    private Set<Class<?>> cachedWrapperClasses;

    /**
     * 缓存加载异常，从而可选择时机抛出
     */
    private ConcurrentHashMap<String, IllegalStateException> exceptions = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final ExtensionAdapterFactory extensionAdapterFactory;
    private final List<ExtensionInjector> extensionInjectors;

    /**
     * Record all unacceptable exceptions when using SPI
     */
    private Set<String> unacceptableExceptions = new ConcurrentHashSet<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
        if (type == ExtensionAdapter.class) {
            this.extensionAdapterFactory = new ExtensionAdapterFactory();
            this.extensionInjectors = null;
        } else {
            this.extensionAdapterFactory = null;
            if (type == ExtensionInjector.class) {
                this.extensionInjectors = null;
            } else {
                this.extensionInjectors = ExtensionLoader.getLoader(ExtensionInjector.class).getList();
            }
        }
    }

    public static ExtensionAdapterFactory getAdapterFactory() {
        return (ExtensionAdapterFactory) getLoader(ExtensionAdapter.class).getFirst("adapterFactory");
    }

    /**
     * Get {@code ExtensionLoader} instance
     *
     * @param type interface class with SPI annotation
     * @param <T>  interface class with SPI annotation
     * @return ExtensionLoader instance
     */
    @SuppressWarnings("unchecked")
    public static <T extends IExtension<P>, P> ExtensionLoader<T, P> getLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }
        return (ExtensionLoader<T, P>) EXTENSION_LOADERS.computeIfAbsent(type, ExtensionLoader::new);
    }


    public T getFirst(P params) {
        return getFirst(params, false, true);
    }

    public T getFirst(P params, boolean byName) {
        return getFirst(params, byName, true);
    }

    public T getFirst(P params, boolean byName, boolean wrap) {
        List<T> result = getList(params, byName, wrap);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public T getFirst() {
        List<T> list = getList();
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public List<T> getList() {
        return createExtension(null, false, false);
    }

    public List<T> getList(P params) {
        return getList(params, false, true);
    }

    public List<T> getList(P params, boolean byName) {
        return getList(params, byName, true);
    }

    /**
     * Find the extensions with the given param.
     *
     * @param params params
     * @return The instance
     */
    public List<T> getList(P params, boolean byName, boolean wrap) {
        Assert.notNull(params, "Parameter cannot be null");

        Object objParams = byName ? PREFIX_NAMED_HOLDER + params : params;

        final Holder<List<T>> holder = getOrCreateHolder(objParams);
        List<T> instanceList = holder.get();
        if (instanceList == null) {
            synchronized (holder) {
                instanceList = holder.get();
                if (instanceList == null) {
                    List<T> list = createExtension(objParams, byName, wrap);
                    if (!list.isEmpty()) {
                        instanceList = list;
                        holder.set(instanceList);
                    }
                }
            }
        }
        return instanceList;
    }

    private Holder<List<T>> getOrCreateHolder(Object params) {
        return cachedParamsInstances.computeIfAbsent(params, p -> new Holder<>());
    }

    @SuppressWarnings("unchecked")
    private List<T> createExtension(Object params, boolean byName, boolean wrap) {
        // TODO exceptions
//        if (clazz == null || unacceptableExceptions.contains(name)) {
//            throw findException(name);
//        }
        try {
            List<T> result = new ArrayList<>();
            NamedClassCache<Object> instanceCache = getExtensionInstances(this.type);
            if (byName) {
                String name = params.toString().replace(PREFIX_NAMED_HOLDER, StringUtil.EMPTY);
                Object instance = instanceCache.getByName(name);
                if (instance == null) {
                    // load spi
                    NamedClassCache<Class<?>> classes = getExtensionClasses(this.type, true, false);
                    Class<?> clazz = classes.getByName(name);
                    if (clazz == null) {
                        // SPI force load
                        classes = getExtensionClasses(this.type, true, true);
                        clazz = classes.getByName(name);
                        if (clazz == null) {
                            throw new IllegalStateException("instance for name " + name + " not found!");
                        }
                    }
                    // load from cache first
                    instance = createAndCacheInstance(name, clazz, instanceCache);
                }
                if (instance != null) {
                    // inject
                    injectExtension(instance);
                    result.add((T) instance);
                }
            }
            if (result.isEmpty()) {
                createAndCacheAllInstance(instanceCache);
                if (isExtensionAdapter()) {
                    // 此处不进行注入操作
                    this.extensionAdapterFactory.setAdapters(instanceCache.getAll());
                    result.add((T) this.extensionAdapterFactory);
                } else {
                    for (Object o : instanceCache.getAll()) {
                        // 无论是否match上,均进行注入操作
                        Object obj = injectExtension(o);
                        // 参数为null,返回所有此类型的实现
                        T t = (T) obj;
                        if (params == null) {
                            result.add(t);
                        } else if (t.matches((P) params)) {
                            result.add(t);
                        }
                    }
                }
            }
            // TODO wrap
            result.sort(Comparator.comparingInt(IExtension::getOrder));
            return result;
        } catch (Throwable e) {
            throw new IllegalStateException("Extension instance class: " +
                    this.type + ") couldn't be instantiated: " + e.getMessage(), e);
        }
    }

    private boolean isExtensionAdapter() {
        return type == ExtensionAdapter.class && extensionAdapterFactory != null;
    }

    private void createAndCacheAllInstance(NamedClassCache<Object> instanceCache) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // create all instances of this.type
        NamedClassCache<Class<?>> classes = getExtensionClasses(this.type, true, false);
        for (Map.Entry<String, Class<?>> entry : classes.getMap().entrySet()) {
            if (!instanceCache.containsByName(entry.getKey())) {
                createAndCacheInstance(entry.getKey(), entry.getValue(), instanceCache);
            }
        }
    }

    private Object createAndCacheInstance(String name, Class<?> clazz, NamedClassCache<Object> instanceCache) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (clazz == null) {
            return null;
        }
        Object instance = instanceCache.getByValueClass(clazz);
        if (instance != null) {
            return instance;
        }
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (isExtensionAdapter()) {
                Function<Class<?>, NamedClassCache<Class<?>>> getExtensionClassesFunction =
                        it -> getExtensionClasses(it, false, false);
                Function<Class<?>, NamedClassCache<Object>> getExtensionInstancesFunction =
                        this::getExtensionInstances;
                instance = constructor.newInstance(getExtensionClassesFunction, getExtensionInstancesFunction);
            } else if (constructor.getParameterCount() == 0) {
                instance = constructor.newInstance();
            }
            if (instance != null) {
                break;
            }
        }
        if (instance != null) {
            instanceCache.cache(name, instance);
        }
        return instance;
    }

    private NamedClassCache<Object> getExtensionInstances(Class<?> clazz) {
        return EXTENSION_INSTANCES.computeIfAbsent(clazz, it -> new NamedClassCache<>());
    }

    private NamedClassCache<Class<?>> getExtensionClasses(Class<?> clazz, boolean loadSpi, boolean force) {
        NamedClassCache<Class<?>> cache = EXTENSION_CLASSES.computeIfAbsent(clazz, it -> new NamedClassCache<>());
        if (force) {
            loadExtensionClasses(cache);
        } else if (cache.isEmpty() && loadSpi) {
            // there is no classes of type
            // spi load
            if (log.isDebugEnabled()) {
                log.debug("spi loading... {}", clazz.getName());
            }
            loadExtensionClasses(cache);
        }
        return cache;
    }

    /**
     * synchronized in getExtensionClasses
     */
    private void loadExtensionClasses(NamedClassCache<Class<?>> cache) {
        Map<String, Class<?>> classMap = FactoriesLoader.loadFactoryClasses(this.type.getName(), findClassLoader(false));
        cache.cache(classMap);
    }

    private static ClassLoader findClassLoader(boolean useExtensionClassLoaderFirst) {
        return ClassLoaderUtil.getClassLoader(ExtensionLoader.class, useExtensionClassLoaderFirst);
    }

    /**
     * 自动注入SPI-Loaded对象，同时开放ExtensionFactory做适配，可支持Spring等环境的自动注入
     * <ul>
     *   <li>Inject方式</li>
     *   <li>1. 构造方法 (spring环境)（不支持，实现上有些不好的地方）</li>
     *   <li>2. setterXXX (所有环境)</li>
     *   <li>3. @Resource</li>
     *   <li>4. @Autowired | 注解了@Autowired 的方法参数 (spring环境)</li>
     * </ul>
     *
     * @param instance 实例
     * @return 注入后的实例
     */
    private Object injectExtension(Object instance) {
        if (extensionInjectors == null) {
            return instance;
        }
        try {
            Class<?> instanceClass = instance.getClass();
            Field[] fields = ReflectUtil.getFields(instanceClass);

            Method[] setters = Arrays.stream(ReflectUtil.getMethods(instanceClass))
                    .filter(ReflectUtil::isSetter)
                    .filter(method -> {
                        Class<?> parameterClass = method.getParameterTypes()[0];
                        if (parameterClass == null) {
                            return false;
                        }
                        return !ClassUtil.isBasicType(parameterClass);
                    })
                    .toArray(Method[]::new);
            for (ExtensionInjector injector : this.extensionInjectors) {
                instance = injector.inject(instance, fields, setters);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return instance;
    }

//    /**
//     * cache Activate class which is annotated with <code>Activate</code>
//     * <p>
//     * for compatibility, also cache class with old alibaba Activate annotation
//     */
//    private void cacheActivateClass(Class<?> clazz, String name) {
//        Activate activate = clazz.getAnnotation(Activate.class);
//        if (activate != null) {
//            cachedActivates.put(name, activate);
//        }
//    }
//
//    /**
//     * cache Adaptive class which is annotated with <code>Adaptive</code>
//     */
//    private void cacheAdaptiveClass(Class<?> clazz, boolean overridden) {
//        if (cachedAdaptiveClass == null || overridden) {
//            cachedAdaptiveClass = clazz;
//        } else if (!cachedAdaptiveClass.equals(clazz)) {
//            throw new IllegalStateException("More than 1 adaptive class found: "
//                    + cachedAdaptiveClass.getName()
//                    + ", " + clazz.getName());
//        }
//    }

//    /**
//     * cache wrapper class
//     * <p>
//     * like: ProtocolFilterWrapper, ProtocolListenerWrapper
//     */
//    private void cacheWrapperClass(Class<?> clazz) {
//        if (cachedWrapperClasses == null) {
//            cachedWrapperClasses = new ConcurrentHashSet<>();
//        }
//        cachedWrapperClasses.add(clazz);
//    }
//
//    private String findAnnotationName(Class<?> clazz) {
//        String name = clazz.getSimpleName();
//        if (name.endsWith(this.type.getSimpleName())) {
//            name = name.substring(0, name.length() - this.type.getSimpleName().length());
//        }
//        return name.toLowerCase();
//    }

//    /**
//     * test if clazz is a wrapper class
//     * <p>
//     * which has Constructor with given class type as its only argument
//     */
//    private boolean isWrapperClass(Class<?> clazz) {
//        try {
//            clazz.getConstructor(this.type);
//            return true;
//        } catch (NoSuchMethodException e) {
//            return false;
//        }
//    }

//    @SuppressWarnings("unchecked")
//    private T createAdaptiveExtension() {
//        try {
//            return injectExtension((T) getAdaptiveExtensionClass().newInstance());
//        } catch (Exception e) {
//            throw new IllegalStateException("Can't create adaptive extension " + this.type + ", cause: " + e.getMessage(), e);
//        }
//    }
//
//    private Class<?> getAdaptiveExtensionClass() {
//        getExtensionClasses();
//        if (cachedAdaptiveClass != null) {
//            return cachedAdaptiveClass;
//        }
//        return cachedAdaptiveClass = createAdaptiveExtensionClass();
//    }
//
//    private Class<?> createAdaptiveExtensionClass() {
//        String code = new AdaptiveClassCodeGenerator(this.type, cachedDefaultName).generate();
//        ClassLoader classLoader = findClassLoader();
//        org.apache.dubbo.common.compiler.Compiler compiler = ExtensionLoader.getExtensionLoader(org.apache.dubbo.common.compiler.Compiler.class).getAdaptiveExtension();
//        return compiler.compile(code, classLoader);
//    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.type.getName() + "]";
    }

}
