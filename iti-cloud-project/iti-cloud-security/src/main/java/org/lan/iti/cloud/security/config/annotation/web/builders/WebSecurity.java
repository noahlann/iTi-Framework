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

package org.lan.iti.cloud.security.config.annotation.web.builders;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.lan.iti.cloud.security.config.annotation.ObjectPostProcessor;
import org.lan.iti.cloud.security.config.annotation.SecurityBuilder;
import org.lan.iti.cloud.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.lan.iti.cloud.security.config.annotation.web.WebSecurityConfigurer;
import org.lan.iti.cloud.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.lan.iti.cloud.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.lan.iti.cloud.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.lan.iti.cloud.security.web.FilterChainProxy;
import org.lan.iti.cloud.security.web.matcher.MvcRequestMatcher;
import org.lan.iti.iha.security.matcher.RequestMatcher;
import org.lan.iti.iha.security.web.DefaultSecurityFilterChain;
import org.lan.iti.iha.security.web.SecurityFilterChain;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * The {@link WebSecurity} is created by {@link WebSecurityConfiguration} to create the
 * {@link FilterChainProxy} known as the Spring Security Filter Chain
 * (itiSecurityFilterChain). The itiSecurityFilterChain is the {@link Filter} that
 * the {@link org.springframework.web.filter.DelegatingFilterProxy} delegates to.
 * </p>
 *
 * <p>
 * Customizations to the {@link WebSecurity} can be made by creating a
 * {@link WebSecurityConfigurer}, overriding {@link WebSecurityConfigurerAdapter} or
 * exposing a {@link WebSecurityCustomizer} bean.
 * </p>
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
@Slf4j
public final class WebSecurity extends AbstractConfiguredSecurityBuilder<Filter, WebSecurity>
        implements SecurityBuilder<Filter>, ApplicationContextAware {

    private final List<RequestMatcher> ignoredRequests = new ArrayList<>();

    private final List<SecurityBuilder<? extends SecurityFilterChain>> securityFilterChainBuilders = new ArrayList<>();

    private IgnoredRequestConfigurer ignoredRequestRegistry;

    private Runnable postBuildAction = () -> {
    };

    /**
     * Creates a new instance
     *
     * @param objectPostProcessor the {@link ObjectPostProcessor} to use
     * @see org.lan.iti.cloud.security.config.annotation.web.configuration.WebSecurityConfiguration
     */
    public WebSecurity(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * <p>
     * Allows adding {@link RequestMatcher} instances that Spring Security should ignore.
     * Web Security provided by Spring Security (including the {@link org.lan.iti.iha.security.context.SecurityContext})
     * will not be available on {@link HttpServletRequest} that match. Typically the
     * requests that are registered should be that of only static resources. For requests
     * that are dynamic, consider mapping the request to allow all users instead.
     * </p>
     * <p>
     * Example Usage:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/ or /static/
     * 		.antMatchers(&quot;/resources/**&quot;, &quot;/static/**&quot;);
     * </pre>
     * <p>
     * Alternatively this will accomplish the same result:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/ or /static/
     * 		.antMatchers(&quot;/resources/**&quot;).antMatchers(&quot;/static/**&quot;);
     * </pre>
     * <p>
     * Multiple invocations of ignoring() are also additive, so the following is also
     * equivalent to the previous two examples:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/
     * 		.antMatchers(&quot;/resources/**&quot;);
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /static/
     * 		.antMatchers(&quot;/static/**&quot;);
     * // now both URLs that start with /resources/ and /static/ will be ignored
     * </pre>
     *
     * @return the {@link IgnoredRequestConfigurer} to use for registering request that
     * should be ignored
     */
    public IgnoredRequestConfigurer ignoring() {
        return this.ignoredRequestRegistry;
    }

    /**
     * <p>
     * Adds builders to create {@link SecurityFilterChain} instances.
     * </p>
     *
     * <p>
     * Typically this method is invoked automatically within the framework from
     * {@link WebSecurityConfigurerAdapter#init(WebSecurity)}
     * </p>
     *
     * @param securityFilterChainBuilder the builder to use to create the
     *                                   {@link SecurityFilterChain} instances
     * @return the {@link WebSecurity} for further customizations
     */
    public WebSecurity addSecurityFilterChainBuilder(SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder) {
        this.securityFilterChainBuilders.add(securityFilterChainBuilder);
        return this;
    }

    // TODO FilterSecurityInterceptor

    /**
     * Executes the Runnable immediately after the build takes place
     *
     * @param postBuildAction
     * @return the {@link WebSecurity} for further customizations
     */
    public WebSecurity postBuildAction(Runnable postBuildAction) {
        this.postBuildAction = postBuildAction;
        return this;
    }

    @Override
    protected Filter performBuild() throws Exception {
        Assert.state(!this.securityFilterChainBuilders.isEmpty(),
                () -> "At least one SecurityBuilder<? extends SecurityFilterChain> needs to be specified. "
                        + "Typically this is done by exposing a SecurityFilterChain bean "
                        + "or by adding a @Configuration that extends WebSecurityConfigurerAdapter. "
                        + "More advanced users can invoke " + WebSecurity.class.getSimpleName()
                        + ".addSecurityFilterChainBuilder directly");
        int chainSize = this.ignoredRequests.size() + this.securityFilterChainBuilders.size();
        List<SecurityFilterChain> securityFilterChains = new ArrayList<>(chainSize);
        for (RequestMatcher ignoredRequest : this.ignoredRequests) {
            securityFilterChains.add(new DefaultSecurityFilterChain(ignoredRequest));
        }
        for (SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder : this.securityFilterChainBuilders) {
            securityFilterChains.add(securityFilterChainBuilder.build());
        }
        FilterChainProxy filterChainProxy = new FilterChainProxy(securityFilterChains);
        filterChainProxy.afterPropertiesSet();

        this.postBuildAction.run();
        return filterChainProxy;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ignoredRequestRegistry = new IgnoredRequestConfigurer(applicationContext);
    }

    /**
     * An {@link IgnoredRequestConfigurer} that allows optionally configuring the
     * {@link MvcRequestMatcher#setMethod(HttpMethod)}
     */
    public final class MvcMatchersIgnoredRequestConfigurer extends IgnoredRequestConfigurer {

        private final List<MvcRequestMatcher> mvcMatchers;

        private MvcMatchersIgnoredRequestConfigurer(ApplicationContext context, List<MvcRequestMatcher> mvcMatchers) {
            super(context);
            this.mvcMatchers = mvcMatchers;
        }

        public IgnoredRequestConfigurer servletPath(String servletPath) {
            for (MvcRequestMatcher matcher : this.mvcMatchers) {
                matcher.setServletPath(servletPath);
            }
            return this;
        }

    }

    /**
     * Allows registering {@link RequestMatcher} instances that should be ignored by
     * Spring Security.
     */
    public class IgnoredRequestConfigurer extends AbstractRequestMatcherRegistry<IgnoredRequestConfigurer> {

        IgnoredRequestConfigurer(ApplicationContext context) {
            setApplicationContext(context);
        }

        @Override
        public MvcMatchersIgnoredRequestConfigurer mvcMatchers(HttpMethod method, String... mvcPatterns) {
            List<MvcRequestMatcher> mvcMatchers = createMvcMatchers(method, mvcPatterns);
            WebSecurity.this.ignoredRequests.addAll(mvcMatchers);
            return new MvcMatchersIgnoredRequestConfigurer(getApplicationContext(), mvcMatchers);
        }

        @Override
        public MvcMatchersIgnoredRequestConfigurer mvcMatchers(String... mvcPatterns) {
            return mvcMatchers(null, mvcPatterns);
        }

        @Override
        protected IgnoredRequestConfigurer chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            WebSecurity.this.ignoredRequests.addAll(requestMatchers);
            return this;
        }

        /**
         * Returns the {@link WebSecurity} to be returned for chaining.
         */
        public WebSecurity and() {
            return WebSecurity.this;
        }

    }
}
