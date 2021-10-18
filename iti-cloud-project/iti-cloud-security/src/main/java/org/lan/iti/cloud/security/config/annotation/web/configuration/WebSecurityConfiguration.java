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

import org.lan.iti.cloud.security.config.annotation.ObjectPostProcessor;
import org.lan.iti.cloud.security.config.annotation.SecurityConfigurer;
import org.lan.iti.cloud.security.config.annotation.web.builders.WebSecurity;
import org.lan.iti.iha.security.web.SecurityFilterChain;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

/**
 * @author NorthLan
 * @date 2021/10/16
 * @url https://blog.noahlan.com
 */
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfiguration implements ImportAware, BeanClassLoaderAware {
    public final static String DEFAULT_FILTER_NAME = "itiSecurityFilterChain";

    private WebSecurity webSecurity;

    private List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers;

    private List<SecurityFilterChain> securityFilterChains = Collections.emptyList();

    private List<WebSecurityCustomizer> webSecurityCustomizers = Collections.emptyList();

    private ClassLoader beanClassLoader;

    @Autowired(required = false)
    private ObjectPostProcessor<Object> objectObjectPostProcessor;

    @Bean(name = DEFAULT_FILTER_NAME)
    public Filter itiSecurityFilterChain() throws Exception {
        boolean hasConfigurers = this.webSecurityConfigurers != null && !this.webSecurityConfigurers.isEmpty();
        boolean hasFilterChain = !this.securityFilterChains.isEmpty();
        Assert.state(!(hasConfigurers && hasFilterChain),
                "Found WebSecurityConfigurerAdapter as well as SecurityFilterChain. Please select just one.");
        if (!hasConfigurers && !hasFilterChain) {
            WebSecurityConfigurerAdapter adapter = this.objectObjectPostProcessor
                    .postProcess(new WebSecurityConfigurerAdapter() {
                    });
            this.webSecurity.apply(adapter);
        }
        for (SecurityFilterChain securityFilterChain : this.securityFilterChains) {
            this.webSecurity.addSecurityFilterChainBuilder(() -> securityFilterChain);
            // TODO forEach Filters setup securityInterception
//            for (Filter filter : securityFilterChain.getFilters()) {
//            }
        }
        for (WebSecurityCustomizer customizer : this.webSecurityCustomizers) {
            customizer.customize(this.webSecurity);
        }
        return this.webSecurity.build();
    }

    @Autowired(required = false)
    public void setFilterChainProxySecurityConfigurer(ObjectPostProcessor<Object> objectPostProcessor,
                                                      @Value("#{@autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()}") List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers) throws Exception {
        this.webSecurity = objectPostProcessor.postProcess(new WebSecurity(objectPostProcessor));
        webSecurityConfigurers.sort(AnnotationAwareOrderComparator.INSTANCE);
        Integer previousOrder = null;
        Object previousConfig = null;
        for (SecurityConfigurer<Filter, WebSecurity> config : webSecurityConfigurers) {
            Integer order = AnnotationAwareOrderComparator.lookupOrder(config);
            if (previousOrder != null && previousOrder.equals(order)) {
                throw new IllegalStateException("@Order on WebSecurityConfigurers must be unique. Order of " + order
                        + " was already used on " + previousConfig + ", so it cannot be used on " + config + " too.");
            }
            previousOrder = order;
            previousConfig = config;
        }
        for (SecurityConfigurer<Filter, WebSecurity> webSecurityConfigurer : webSecurityConfigurers) {
            this.webSecurity.apply(webSecurityConfigurer);
        }
        this.webSecurityConfigurers = webSecurityConfigurers;
    }

    @Autowired(required = false)
    void setFilterChains(List<SecurityFilterChain> securityFilterChains) {
        this.securityFilterChains = securityFilterChains;
    }

    @Autowired(required = false)
    void setWebSecurityCustomizers(List<WebSecurityCustomizer> webSecurityCustomizers) {
        this.webSecurityCustomizers = webSecurityCustomizers;
    }

    @Bean
    public static AutowiredWebSecurityConfigurersIgnoreParents autowiredWebSecurityConfigurersIgnoreParents(
            ConfigurableListableBeanFactory beanFactory) {
        return new AutowiredWebSecurityConfigurersIgnoreParents(beanFactory);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {

    }

    /**
     * A custom version of the Spring provided AnnotationAwareOrderComparator that uses
     * {@link AnnotationUtils#findAnnotation(Class, Class)} to look on super class
     * instances for the {@link Order} annotation.
     */
    private static class AnnotationAwareOrderComparator extends OrderComparator {

        private static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

        @Override
        protected int getOrder(Object obj) {
            return lookupOrder(obj);
        }

        private static int lookupOrder(Object obj) {
            if (obj instanceof Ordered) {
                return ((Ordered) obj).getOrder();
            }
            if (obj != null) {
                Class<?> clazz = ((obj instanceof Class) ? (Class<?>) obj : obj.getClass());
                Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
                if (order != null) {
                    return order.value();
                }
            }
            return Ordered.LOWEST_PRECEDENCE;
        }

    }
}
