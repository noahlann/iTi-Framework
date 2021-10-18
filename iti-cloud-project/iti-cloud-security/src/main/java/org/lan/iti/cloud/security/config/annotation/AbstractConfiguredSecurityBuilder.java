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

package org.lan.iti.cloud.security.config.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
@Slf4j
public abstract class AbstractConfiguredSecurityBuilder<O, B extends SecurityBuilder<O>>
        extends AbstractSecurityBuilder<O> {

    private final LinkedHashMap<Class<? extends SecurityConfigurer<O, B>>, List<SecurityConfigurer<O, B>>> configurers = new LinkedHashMap<>();

    private final List<SecurityConfigurer<O, B>> configurersAddedInInitializing = new ArrayList<>();

    private final Map<Class<?>, Object> sharedObjects = new HashMap<>();

    private final boolean allowConfigurersOfSameType;

    private BuildState buildState = BuildState.UNBUILT;

    private ObjectPostProcessor<Object> objectPostProcessor;

    /***
     * 使用提供的 {@link ObjectPostProcessor} 创建一个新实例。
     * 这个后处理器必须支持 Object，因为有许多类型的对象可以被后处理。
     *
     * @param objectPostProcessor the {@link ObjectPostProcessor} to use
     */
    protected AbstractConfiguredSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        this(objectPostProcessor, false);
    }

    /***
     * 使用提供的 {@link ObjectPostProcessor} 创建一个新实例。
     * 这个后处理器必须支持 Object，因为有许多类型的对象可以被后处理。
     *
     * @param objectPostProcessor the {@link ObjectPostProcessor} to use
     * @param allowConfigurersOfSameType if true, will not override other
     * {@link SecurityConfigurer}'s when performing apply
     */
    protected AbstractConfiguredSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor,
                                                boolean allowConfigurersOfSameType) {
        Assert.notNull(objectPostProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = objectPostProcessor;
        this.allowConfigurersOfSameType = allowConfigurersOfSameType;
    }

    /**
     * 与 {@link #build()} 和 {@link #getObject()} 类似
     * <br>
     * 检查build状态以决定 {@link #build()} 是否需要首先执行
     *
     * @return the result of {@link #build()} or {@link #getObject()}. If an error occurs
     * while building, returns null.
     */
    public O getOrBuild() {
        if (!isUnbuilt()) {
            return getObject();
        }
        try {
            return build();
        } catch (Exception ex) {
            log.debug("Failed to perform build. Returning null", ex);
            return null;
        }
    }

    /**
     * Applies a {@link SecurityConfigurerAdapter} to this {@link SecurityBuilder} and
     * invokes {@link SecurityConfigurerAdapter#setBuilder(SecurityBuilder)}.
     *
     * @param configurer
     * @return the {@link SecurityConfigurerAdapter} for further customizations
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <C extends SecurityConfigurerAdapter<O, B>> C apply(C configurer) throws Exception {
        configurer.addObjectPostProcessor(this.objectPostProcessor);
        configurer.setBuilder((B) this);
        add(configurer);
        return configurer;
    }

    /**
     * Applies a {@link SecurityConfigurer} to this {@link SecurityBuilder} overriding any
     * {@link SecurityConfigurer} of the exact same class. Note that object hierarchies
     * are not considered.
     *
     * @param configurer
     * @return the {@link SecurityConfigurerAdapter} for further customizations
     * @throws Exception
     */
    public <C extends SecurityConfigurer<O, B>> C apply(C configurer) throws Exception {
        add(configurer);
        return configurer;
    }

    /**
     * Sets an object that is shared by multiple {@link SecurityConfigurer}.
     *
     * @param sharedType the Class to key the shared object by.
     * @param object     the Object to store
     */
    @SuppressWarnings("unchecked")
    public <C> void setSharedObject(Class<C> sharedType, C object) {
        this.sharedObjects.put(sharedType, object);
    }

    /**
     * Gets a shared Object. Note that object heirarchies are not considered.
     *
     * @param sharedType the type of the shared Object
     * @return the shared Object or null if it is not found
     */
    @SuppressWarnings("unchecked")
    public <C> C getSharedObject(Class<C> sharedType) {
        return (C) this.sharedObjects.get(sharedType);
    }

    /**
     * Gets the shared objects
     *
     * @return the shared Objects
     */
    public Map<Class<?>, Object> getSharedObjects() {
        return Collections.unmodifiableMap(this.sharedObjects);
    }

    /**
     * Adds {@link SecurityConfigurer} ensuring that it is allowed and invoking
     * {@link SecurityConfigurer#init(SecurityBuilder)} immediately if necessary.
     *
     * @param configurer the {@link SecurityConfigurer} to add
     */
    @SuppressWarnings("unchecked")
    private <C extends SecurityConfigurer<O, B>> void add(C configurer) {
        Assert.notNull(configurer, "configurer cannot be null");
        Class<? extends SecurityConfigurer<O, B>> clazz = (Class<? extends SecurityConfigurer<O, B>>) configurer
                .getClass();
        synchronized (this.configurers) {
            if (this.buildState.isConfigured()) {
                throw new IllegalStateException("Cannot apply " + configurer + " to already built object");
            }
            List<SecurityConfigurer<O, B>> configs = null;
            if (this.allowConfigurersOfSameType) {
                configs = this.configurers.get(clazz);
            }
            configs = (configs != null) ? configs : new ArrayList<>(1);
            configs.add(configurer);
            this.configurers.put(clazz, configs);
            if (this.buildState.isInitializing()) {
                this.configurersAddedInInitializing.add(configurer);
            }
        }
    }

    /**
     * Gets all the {@link SecurityConfigurer} instances by its class name or an empty
     * List if not found. Note that object hierarchies are not considered.
     *
     * @param clazz the {@link SecurityConfigurer} class to look for
     * @return a list of {@link SecurityConfigurer}s for further customization
     */
    @SuppressWarnings("unchecked")
    public <C extends SecurityConfigurer<O, B>> List<C> getConfigurers(Class<C> clazz) {
        List<C> configs = (List<C>) this.configurers.get(clazz);
        if (configs == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(configs);
    }

    /**
     * Removes all the {@link SecurityConfigurer} instances by its class name or an empty
     * List if not found. Note that object hierarchies are not considered.
     *
     * @param clazz the {@link SecurityConfigurer} class to look for
     * @return a list of {@link SecurityConfigurer}s for further customization
     */
    @SuppressWarnings("unchecked")
    public <C extends SecurityConfigurer<O, B>> List<C> removeConfigurers(Class<C> clazz) {
        List<C> configs = (List<C>) this.configurers.remove(clazz);
        if (configs == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(configs);
    }

    /**
     * Gets the {@link SecurityConfigurer} by its class name or <code>null</code> if not
     * found. Note that object hierarchies are not considered.
     *
     * @param clazz
     * @return the {@link SecurityConfigurer} for further customizations
     */
    @SuppressWarnings("unchecked")
    public <C extends SecurityConfigurer<O, B>> C getConfigurer(Class<C> clazz) {
        List<SecurityConfigurer<O, B>> configs = this.configurers.get(clazz);
        if (configs == null) {
            return null;
        }
        Assert.state(configs.size() == 1,
                () -> "Only one configurer expected for type " + clazz + ", but got " + configs);
        return (C) configs.get(0);
    }

    /**
     * Removes and returns the {@link SecurityConfigurer} by its class name or
     * <code>null</code> if not found. Note that object hierarchies are not considered.
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C extends SecurityConfigurer<O, B>> C removeConfigurer(Class<C> clazz) {
        List<SecurityConfigurer<O, B>> configs = this.configurers.remove(clazz);
        if (configs == null) {
            return null;
        }
        Assert.state(configs.size() == 1,
                () -> "Only one configurer expected for type " + clazz + ", but got " + configs);
        return (C) configs.get(0);
    }

    /**
     * Specifies the {@link ObjectPostProcessor} to use.
     *
     * @param objectPostProcessor the {@link ObjectPostProcessor} to use. Cannot be null
     * @return the {@link SecurityBuilder} for further customizations
     */
    @SuppressWarnings("unchecked")
    public B objectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        Assert.notNull(objectPostProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = objectPostProcessor;
        return (B) this;
    }

    /**
     * Performs post processing of an object. The default is to delegate to the
     * {@link ObjectPostProcessor}.
     *
     * @param object the Object to post process
     * @return the possibly modified Object to use
     */
    protected <P> P postProcess(P object) {
        return this.objectPostProcessor.postProcess(object);
    }

    /**
     * Executes the build using the {@link SecurityConfigurer}'s that have been applied
     * using the following steps:
     *
     * <ul>
     * <li>Invokes {@link #beforeInit()} for any subclass to hook into</li>
     * <li>Invokes {@link SecurityConfigurer#init(SecurityBuilder)} for any
     * {@link SecurityConfigurer} that was applied to this builder.</li>
     * <li>Invokes {@link #beforeConfigure()} for any subclass to hook into</li>
     * <li>Invokes {@link #performBuild()} which actually builds the Object</li>
     * </ul>
     */
    @Override
    protected final O doBuild() throws Exception {
        synchronized (this.configurers) {
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.INITIALIZING;
            beforeInit();
            init();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.CONFIGURING;
            beforeConfigure();
            configure();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.BUILDING;
            O result = performBuild();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.BUILT;
            return result;
        }
    }

    /**
     * Invoked prior to invoking each {@link SecurityConfigurer#init(SecurityBuilder)}
     * method. Subclasses may override this method to hook into the lifecycle without
     * using a {@link SecurityConfigurer}.
     */
    protected void beforeInit() throws Exception {
    }

    /**
     * Invoked prior to invoking each
     * {@link SecurityConfigurer#configure(SecurityBuilder)} method. Subclasses may
     * override this method to hook into the lifecycle without using a
     * {@link SecurityConfigurer}.
     */
    protected void beforeConfigure() throws Exception {
    }

    /**
     * Subclasses must implement this method to build the object that is being returned.
     *
     * @return the Object to be buit or null if the implementation allows it
     */
    protected abstract O performBuild() throws Exception;

    @SuppressWarnings("unchecked")
    private void init() throws Exception {
        Collection<SecurityConfigurer<O, B>> configurers = getConfigurers();
        for (SecurityConfigurer<O, B> configurer : configurers) {
            configurer.init((B) this);
        }
        for (SecurityConfigurer<O, B> configurer : this.configurersAddedInInitializing) {
            configurer.init((B) this);
        }
    }

    @SuppressWarnings("unchecked")
    private void configure() throws Exception {
        Collection<SecurityConfigurer<O, B>> configurers = getConfigurers();
        for (SecurityConfigurer<O, B> configurer : configurers) {
            configurer.configure((B) this);
        }
    }

    private Collection<SecurityConfigurer<O, B>> getConfigurers() {
        List<SecurityConfigurer<O, B>> result = new ArrayList<>();
        for (List<SecurityConfigurer<O, B>> configs : this.configurers.values()) {
            result.addAll(configs);
        }
        return result;
    }

    /**
     * 确定对象是否未构建
     *
     * @return true 未构建
     */
    private boolean isUnbuilt() {
        synchronized (this.configurers) {
            return this.buildState == BuildState.UNBUILT;
        }
    }

    /**
     * 应用构建状态
     */
    private enum BuildState {

        /**
         * 构建前
         */
        UNBUILT(0),

        /**
         * 初次构建 {@link #build()} 第一次调用
         * 直到 {@link SecurityConfigurer#init(SecurityBuilder)} 被执行
         */
        INITIALIZING(1),

        /**
         * 配置中
         * <br>
         * 所有的 {@link SecurityConfigurer#init(SecurityBuilder)} 被调用
         * 直到 所有的 {@link SecurityConfigurer#configure(SecurityBuilder)} 被调用
         */
        CONFIGURING(2),

        /**
         * 构建中
         * 从所有 {@link SecurityConfigurer#configure(SecurityBuilder)} 完成之后
         * 到 {@link AbstractConfiguredSecurityBuilder#performBuild()} 之后。
         */
        BUILDING(3),

        /**
         * 构建完
         * <br>
         * 对象完全构建之后
         */
        BUILT(4);

        private final int order;

        BuildState(int order) {
            this.order = order;
        }

        public boolean isInitializing() {
            return INITIALIZING.order == this.order;
        }

        /**
         * 确定状态是否为 CONFIGURING 或更延后的生命周期
         */
        public boolean isConfigured() {
            return this.order >= CONFIGURING.order;
        }

    }
}
