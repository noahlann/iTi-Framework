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
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.ClassLoaderUtil;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.common.extension.adapter.ExtensionAdapter;
import org.lan.iti.common.extension.adapter.ExtensionAdapterFactory;
import org.lan.iti.common.extension.support.NamedClassCache;
import org.lan.iti.common.extension.util.Holder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * Record all unacceptable exceptions when using SPI
     */
    private Set<String> unacceptableExceptions = new ConcurrentHashSet<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
        this.extensionAdapterFactory = isExtensionAdapter() ? new ExtensionAdapterFactory() : null;
//        this.objectFactory = (type == ExtensionFactory.class ? null : ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension());
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
        NamedClassCache<Object> instanceCache = createAllInstance();
        // TODO wrap sort inject
        return instanceCache.getAll().stream().map(it -> (T) it).collect(Collectors.toList());
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
                    instance = createInstance(clazz, instanceCache);
                    // cache it
                    instanceCache.cache(name, instance);
                    // add
                }
                result.add((T) instance);
            } else {
                createAllInstance(instanceCache);
            }
            if (result.isEmpty()) {
                if (this.extensionAdapterFactory != null && isExtensionAdapter()) {
                    this.extensionAdapterFactory.setAdapters(instanceCache.getAll());
                    result.add((T) this.extensionAdapterFactory);
                } else {
                    for (Object o : instanceCache.getAll()) {
                        T t = (T) o;
                        if (t.matches((P) params)) {
                            result.add(t);
                        }
                    }
                }
            }
            // TODO inject
            // TODO wrap
            result.sort(Comparator.comparingInt(IExtension::getOrder));
            return result;
        } catch (Throwable e) {
            throw new IllegalStateException("Extension instance class: " +
                    this.type + ") couldn't be instantiated: " + e.getMessage(), e);
        }
    }

    private boolean isExtensionAdapter() {
        return type == ExtensionAdapter.class;
    }

    private NamedClassCache<Object> createAllInstance() {
        NamedClassCache<Object> instanceCache = getExtensionInstances(this.type);
        try {
            return createAllInstance(instanceCache);
        } catch (Throwable e) {
            throw new IllegalStateException("Extension instance class: " +
                    this.type + ") couldn't be instantiated: " + e.getMessage(), e);
        }
    }

    private NamedClassCache<Object> createAllInstance(NamedClassCache<Object> instanceCache) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // create all instances of this.type
        NamedClassCache<Class<?>> classes = getExtensionClasses(this.type, true, false);
        for (Map.Entry<String, Class<?>> entry : classes.getMap().entrySet()) {
            if (instanceCache.getByName(entry.getKey()) == null) {
                instanceCache.cache(entry.getKey(), createInstance(entry.getValue(), instanceCache));
            }
        }
        return instanceCache;
    }

    private Object createInstance(Class<?> clazz, NamedClassCache<Object> instanceCache) throws InvocationTargetException, InstantiationException, IllegalAccessException {
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
                return constructor.newInstance(getExtensionClassesFunction, getExtensionInstancesFunction);
            } else {
                return constructor.newInstance();
            }
        }
        return null;
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

//    /**
//     * Get the extension by specified name if found, or {@link #getDefaultExtension() returns the default one}
//     *
//     * @param name the name of extension
//     * @return non-null
//     */
//    public T getOrDefaultExtension(String name) {
//        return containsExtension(name) ? getExtension(name) : getDefaultExtension();
//    }
//
//    /**
//     * Return default extension, return <code>null</code> if it's not configured.
//     */
//    public T getDefaultExtension() {
//        getExtensionClasses();
//        if (StringUtils.isBlank(cachedDefaultName) || "true".equals(cachedDefaultName)) {
//            return null;
//        }
//        return getExtension(cachedDefaultName);
//    }
//
//    public boolean hasExtension(String name) {
//        if (StringUtils.isEmpty(name)) {
//            throw new IllegalArgumentException("Extension name == null");
//        }
//        Class<?> c = this.getExtensionClass(name);
//        return c != null;
//    }
//
//    public Set<String> getSupportedExtensions() {
//        Map<String, Class<?>> clazzes = getExtensionClasses();
//        return Collections.unmodifiableSet(new TreeSet<>(clazzes.keySet()));
//    }
//
//    public Set<T> getSupportedExtensionInstances() {
//        List<T> instances = new LinkedList<>();
//        Set<String> supportedExtensions = getSupportedExtensions();
//        if (CollUtil.isNotEmpty(supportedExtensions)) {
//            for (String name : supportedExtensions) {
//                instances.add(getExtension(name));
//            }
//        }
//        // sort the Prioritized instances
//        sort(instances, Prioritized.COMPARATOR);
//        return new LinkedHashSet<>(instances);
//    }

//    /**
//     * Return default extension name, return <code>null</code> if not configured.
//     */
//    public String getDefaultExtensionName() {
//        getExtensionClasses();
//        return cachedDefaultName;
//    }

//    /**
//     * Register new extension via API
//     *
//     * @param name  extension name
//     * @param clazz extension class
//     * @throws IllegalStateException when extension with the same name has already been registered.
//     */
//    public void addExtension(String name, Class<?> clazz) {
//        getExtensionClasses(); // load classes
//
//        if (!this.type.isAssignableFrom(clazz)) {
//            throw new IllegalStateException("Input type " +
//                    clazz + " doesn't implement the Extension " + this.type);
//        }
//        if (clazz.isInterface()) {
//            throw new IllegalStateException("Input type " +
//                    clazz + " can't be interface!");
//        }
//
//        if (!clazz.isAnnotationPresent(Adaptive.class)) {
//            if (StringUtils.isBlank(name)) {
//                throw new IllegalStateException("Extension name is blank (Extension " + this.type + ")!");
//            }
//            if (cachedClasses.get().containsKey(name)) {
//                throw new IllegalStateException("Extension name " +
//                        name + " already exists (Extension " + this.type + ")!");
//            }
//
//            cachedNames.put(clazz, name);
//            cachedClasses.get().put(name, clazz);
//        } else {
//            if (cachedAdaptiveClass != null) {
//                throw new IllegalStateException("Adaptive Extension already exists (Extension " + this.type + ")!");
//            }
//
//            cachedAdaptiveClass = clazz;
//        }
//    }

//    @SuppressWarnings("unchecked")
//    public T getAdaptiveExtension() {
//        Object instance = cachedAdaptiveInstance.get();
//        if (instance == null) {
//            if (createAdaptiveInstanceError != null) {
//                throw new IllegalStateException("Failed to create adaptive instance: " +
//                        createAdaptiveInstanceError.toString(),
//                        createAdaptiveInstanceError);
//            }
//
//            synchronized (cachedAdaptiveInstance) {
//                instance = cachedAdaptiveInstance.get();
//                if (instance == null) {
//                    try {
//                        instance = createAdaptiveExtension();
//                        cachedAdaptiveInstance.set(instance);
//                    } catch (Throwable t) {
//                        createAdaptiveInstanceError = t;
//                        throw new IllegalStateException("Failed to create adaptive instance: " + t.toString(), t);
//                    }
//                }
//            }
//        }
//        return (T) instance;
//    }

    private IllegalStateException findException(String name) {
        StringBuilder buf = new StringBuilder("No such extension " + this.type.getName() + " by name " + name);

        int i = 1;
        for (Map.Entry<String, IllegalStateException> entry : exceptions.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(name.toLowerCase())) {
                if (i == 1) {
                    buf.append(", possible causes: ");
                }
                buf.append("\r\n(");
                buf.append(i++);
                buf.append(") ");
                buf.append(entry.getKey());
                buf.append(":\r\n");
                buf.append(StringUtil.toString(entry.getValue()));
            }
        }

        if (i == 1) {
            buf.append(", no related exception was found, please check whether related SPI module is missing.");
        }
        return new IllegalStateException(buf.toString());
    }

//    @SuppressWarnings("unchecked")
//    private T createExtension(String name, boolean wrap) {
//        Class<?> clazz = getExtensionClasses().get(name);
//        if (clazz == null || unacceptableExceptions.contains(name)) {
//            throw findException(name);
//        }
//        try {
//            T instance = (T) EXTENSION_INSTANCES.get(clazz);
//            if (instance == null) {
//                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.getDeclaredConstructor().newInstance());
//                instance = (T) EXTENSION_INSTANCES.get(clazz);
//            }
//            injectExtension(instance);
//
//            if (wrap) {
//                List<Class<?>> wrapperClassesList = new ArrayList<>();
//                if (cachedWrapperClasses != null) {
//                    wrapperClassesList.addAll(cachedWrapperClasses);
//                    wrapperClassesList.sort(WrapperComparator.COMPARATOR);
//                    Collections.reverse(wrapperClassesList);
//                }
//
//                if (CollUtil.isNotEmpty(wrapperClassesList)) {
//                    for (Class<?> wrapperClass : wrapperClassesList) {
//                        Wrapper wrapper = wrapperClass.getAnnotation(Wrapper.class);
//                        if (wrapper == null
//                                || (ArrayUtil.contains(wrapper.matches(), name) && !ArrayUtil.contains(wrapper.mismatches(), name))) {
//                            instance = injectExtension((T) wrapperClass.getConstructor(this.type).newInstance(instance));
//                        }
//                    }
//                }
//            }
//            return instance;
//        } catch (Throwable t) {
//            throw new IllegalStateException("Extension instance (name: " + name + ", class: " +
//                    this.type + ") couldn't be instantiated: " + t.getMessage(), t);
//        }
//    }

//    private boolean containsExtension(String name) {
//        return getExtensionClasses().containsKey(name);
//    }

//    private T injectExtension(T instance) {
//        if (objectFactory == null) {
//            return instance;
//        }
//        try {
//            for (Method method : instance.getClass().getMethods()) {
//                if (!isSetter(method)) {
//                    continue;
//                }
//                Class<?> pt = method.getParameterTypes()[0];
//                if (ClassUtil.isBasicType(pt)) {
//                    continue;
//                }
//
//                try {
//                    String property = getSetterProperty(method);
//                    Object object = objectFactory.getExtension(pt, property);
//                    if (object != null) {
//                        method.invoke(instance, object);
//                    }
//                } catch (Exception e) {
//                    log.error("Failed to inject via method " + method.getName()
//                            + " of interface " + this.type.getName() + ": " + e.getMessage(), e);
//                }
//
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return instance;
//    }


//    /**
//     * extract and cache default extension name if exists
//     */
//    private void cacheDefaultExtensionName() {
//        final SPI defaultAnnotation = this.type.getAnnotation(SPI.class);
//        if (defaultAnnotation == null) {
//            return;
//        }
//
//        String value = defaultAnnotation.value();
//        if ((value = value.trim()).length() > 0) {
//            String[] names = NAME_SEPARATOR.split(value);
//            if (names.length > 1) {
//                throw new IllegalStateException("More than 1 default extension name on extension " + type.getName()
//                        + ": " + Arrays.toString(names));
//            }
//            if (names.length == 1) {
//                cachedDefaultName = names[0];
//            }
//        }
//    }

//    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir, String type) {
//        loadDirectory(extensionClasses, dir, type, false, false);
//    }

//    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir, String type,
//                               boolean extensionLoaderClassLoaderFirst, boolean overridden, String... excludedPackages) {
//        String fileName = dir + type;
//        try {
//            Enumeration<URL> urls = null;
//            ClassLoader classLoader = findClassLoader();
//
//            // try to load from ExtensionLoader's ClassLoader first
//            if (extensionLoaderClassLoaderFirst) {
//                ClassLoader extensionLoaderClassLoader = ExtensionLoader.class.getClassLoader();
//                if (ClassLoader.getSystemClassLoader() != extensionLoaderClassLoader) {
//                    urls = extensionLoaderClassLoader.getResources(fileName);
//                }
//            }
//
//            if (urls == null || !urls.hasMoreElements()) {
//                if (classLoader != null) {
//                    urls = classLoader.getResources(fileName);
//                } else {
//                    urls = ClassLoader.getSystemResources(fileName);
//                }
//            }
//
//            if (urls != null) {
//                while (urls.hasMoreElements()) {
//                    URL resourceURL = urls.nextElement();
//                    loadResource(extensionClasses, classLoader, resourceURL, overridden, excludedPackages);
//                }
//            }
//        } catch (Throwable t) {
//            log.error("Exception occurred when loading extension class (interface: " +
//                    type + ", description file: " + fileName + ").", t);
//        }
//    }

//    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader,
//                              URL resourceURL, boolean overridden, String... excludedPackages) {
//        try {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
//                String line;
//                String clazz;
//                while ((line = reader.readLine()) != null) {
//                    final int ci = line.indexOf('#');
//                    if (ci >= 0) {
//                        line = line.substring(0, ci);
//                    }
//                    line = line.trim();
//                    if (line.length() > 0) {
//                        try {
//                            String name = null;
//                            int i = line.indexOf('=');
//                            if (i > 0) {
//                                name = line.substring(0, i).trim();
//                                clazz = line.substring(i + 1).trim();
//                            } else {
//                                clazz = line;
//                            }
//                            if (StringUtils.isNotEmpty(clazz) && !isExcluded(clazz, excludedPackages)) {
//                                loadClass(extensionClasses, resourceURL, Class.forName(clazz, true, classLoader), name, overridden);
//                            }
//                        } catch (Throwable t) {
//                            IllegalStateException e = new IllegalStateException("Failed to load extension class (interface: " + this.type + ", class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);
//                            exceptions.put(line, e);
//                        }
//                    }
//                }
//            }
//        } catch (Throwable t) {
//            log.error("Exception occurred when loading extension class (interface: " +
//                    this.type + ", class file: " + resourceURL + ") in " + resourceURL, t);
//        }
//    }

//    private boolean isExcluded(String className, String... excludedPackages) {
//        if (excludedPackages != null) {
//            for (String excludePackage : excludedPackages) {
//                if (className.startsWith(excludePackage + ".")) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    private void loadClass(Map<String, Class<?>> extensionClasses, URL resourceURL, Class<?> clazz, String name,
//                           boolean overridden) throws NoSuchMethodException {
//        if (!this.type.isAssignableFrom(clazz)) {
//            throw new IllegalStateException("Error occurred when loading extension class (interface: " +
//                    this.type + ", class line: " + clazz.getName() + "), class "
//                    + clazz.getName() + " is not subtype of interface.");
//        }
//        if (clazz.isAnnotationPresent(Adaptive.class)) {
//            cacheAdaptiveClass(clazz, overridden);
//        } else if (isWrapperClass(clazz)) {
//            cacheWrapperClass(clazz);
//        } else {
//            clazz.getConstructor();
//            if (StringUtils.isEmpty(name)) {
//                name = findAnnotationName(clazz);
//                if (name.length() == 0) {
//                    throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config " + resourceURL);
//                }
//            }
//
//            String[] names = NAME_SEPARATOR.split(name);
//            if (ArrayUtil.isNotEmpty(names)) {
//                cacheActivateClass(clazz, names[0]);
//                for (String n : names) {
//                    cacheName(clazz, n);
//                    saveInExtensionClass(extensionClasses, clazz, n, overridden);
//                }
//            }
//        }
//    }

//    /**
//     * cache name
//     */
//    private void cacheName(Class<?> clazz, String name) {
//        if (!cachedNames.containsKey(clazz)) {
//            cachedNames.put(clazz, name);
//        }
//    }

//    /**
//     * put clazz in extensionClasses
//     */
//    private void saveInExtensionClass(Map<String, Class<?>> extensionClasses, Class<?> clazz, String name, boolean overridden) {
//        Class<?> c = extensionClasses.get(name);
//        if (c == null || overridden) {
//            extensionClasses.put(name, clazz);
//        } else if (c != clazz) {
//            // duplicate implementation is unacceptable
//            unacceptableExceptions.add(name);
//            String duplicateMsg = "Duplicate extension " + this.type.getName() + " name " + name + " on " + c.getName() + " and " + clazz.getName();
//            log.error(duplicateMsg);
//            throw new IllegalStateException(duplicateMsg);
//        }
//    }

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
