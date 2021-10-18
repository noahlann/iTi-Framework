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

package org.lan.iti.cloud.security.config.annotation.web.configuration;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.security.config.annotation.ObjectPostProcessor;
import org.lan.iti.cloud.security.config.annotation.web.WebSecurityConfigurer;
import org.lan.iti.cloud.security.config.annotation.web.builders.HttpSecurity;
import org.lan.iti.cloud.security.config.annotation.web.builders.WebSecurity;
import org.lan.iti.cloud.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于创建 WebSecurityConfigurer 的基类, 该实现允许通过覆盖方法进行定制。
 *
 * @author NorthLan
 * @date 2021/10/16
 * @url https://blog.noahlan.com
 */
@Order(100)
@Slf4j
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {
    private ApplicationContext context;

    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        @Override
        public <T> T postProcess(T object) {
            throw new IllegalStateException(ObjectPostProcessor.class.getName()
                    + " is a required bean. Ensure you have used @EnableWebSecurity and @Configuration");
        }
    };

    private HttpSecurity http;
    private boolean disableDefaults;

    /**
     * Creates an instance with the default configuration enabled.
     */
    protected WebSecurityConfigurerAdapter() {
        this(false);
    }

    /**
     * Creates an instance which allows specifying if the default configuration should be
     * enabled. Disabling the default configuration should be considered more advanced
     * usage as it requires more understanding of how the framework is implemented.
     *
     * @param disableDefaults true if the default configuration should be disabled, else
     *                        false
     */
    protected WebSecurityConfigurerAdapter(boolean disableDefaults) {
        this.disableDefaults = disableDefaults;
    }

    /**
     * Creates the {@link HttpSecurity} or returns the current instance
     *
     * @return the {@link HttpSecurity}
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected final HttpSecurity getHttp() throws Exception {
        if (this.http != null) {
            return this.http;
        }
        Map<Class<?>, Object> sharedObjects = createSharedObjects();
        this.http = new HttpSecurity(this.objectPostProcessor, sharedObjects);
        if (!this.disableDefaults) {
            applyDefaultConfiguration(this.http);
            ClassLoader classLoader = this.context.getClassLoader();
            List<AbstractHttpConfigurer> defaultHttpConfigurers = SpringFactoriesLoader
                    .loadFactories(AbstractHttpConfigurer.class, classLoader);
            for (AbstractHttpConfigurer configurer : defaultHttpConfigurers) {
                this.http.apply(configurer);
            }
        }
        configure(this.http);
        return this.http;
    }

    private void applyDefaultConfiguration(HttpSecurity http) throws Exception {
//        http.csrf();
//        http.addFilter(new WebAsyncManagerIntegrationFilter());
//        http.exceptionHandling();
//        http.headers();
//        http.sessionManagement();
        http.securityContext();
//        http.requestCache();
//        http.anonymous();
//        http.servletApi();
//        http.apply(new DefaultLoginPageConfigurer<>());
//        http.logout();
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity http = getHttp();
        // TODO FilterSecurityInterceptor
        web.addSecurityFilterChainBuilder(http);
    }

    /**
     * Override this method to configure {@link WebSecurity}. For example, if you wish to
     * ignore certain requests.
     * <p>
     * Endpoints specified in this method will be ignored by Spring Security, meaning it
     * will not protect them from CSRF, XSS, Clickjacking, and so on.
     * <p>
     * Instead, if you want to protect endpoints against common vulnerabilities, then see
     * {@link #configure(HttpSecurity)} and the {@link HttpSecurity#authorizeRequests}
     * configuration method.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
    }

    /**
     * Override this method to configure the {@link HttpSecurity}. Typically subclasses
     * should not invoke this method by calling super as it may override their
     * configuration. The default configuration is:
     *
     * <pre>
     * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
     * </pre>
     * <p>
     * Any endpoint that requires defense against common vulnerabilities can be specified
     * here, including public ones. See {@link HttpSecurity#authorizeRequests} and the
     * `permitAll()` authorization rule for more details on public endpoints.
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    protected void configure(HttpSecurity http) throws Exception {
        log.debug("Using default configure(HttpSecurity). "
                + "If subclassed this will potentially override subclass configure(HttpSecurity).");
//        http.authorizeRequests((requests) -> requests.anyRequest().authenticated());
//        http.formLogin();
//        http.httpBasic();
    }

    /**
     * Gets the ApplicationContext
     *
     * @return the context
     */
    protected final ApplicationContext getApplicationContext() {
        return this.context;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Autowired
    public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    /**
     * Creates the shared objects
     *
     * @return the shared Objects
     */
    private Map<Class<?>, Object> createSharedObjects() {
        Map<Class<?>, Object> sharedObjects = new HashMap<>();
//        sharedObjects.putAll(this.localConfigureAuthenticationBldr.getSharedObjects());
//        sharedObjects.put(UserDetailsService.class, userDetailsService());
        sharedObjects.put(ApplicationContext.class, this.context);
//        sharedObjects.put(ContentNegotiationStrategy.class, this.contentNegotiationStrategy);
//        sharedObjects.put(AuthenticationTrustResolver.class, this.trustResolver);
        return sharedObjects;
    }
}
