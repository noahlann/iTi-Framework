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

import org.lan.iti.cloud.security.config.Customizer;
import org.lan.iti.cloud.security.config.annotation.*;
import org.lan.iti.cloud.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.lan.iti.cloud.security.config.annotation.web.HttpSecurityBuilder;
import org.lan.iti.cloud.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.lan.iti.cloud.security.web.matcher.AnyRequestMatcher;
import org.lan.iti.cloud.security.web.matcher.MvcRequestMatcher;
import org.lan.iti.cloud.security.web.matcher.OrRequestMatcher;
import org.lan.iti.cloud.security.web.matcher.RegexRequestMatcher;
import org.lan.iti.iha.security.context.SecurityContext;
import org.lan.iti.iha.security.context.SecurityContextHolder;
import org.lan.iti.iha.security.matcher.AntPathRequestMatcher;
import org.lan.iti.iha.security.matcher.RequestMatcher;
import org.lan.iti.iha.security.web.DefaultSecurityFilterChain;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 允许为特定的 http 请求配置基于 Web 的安全性。
 * <br>
 * 默认情况下，它将应用于所有请求，但可以使用 {@link #requestMatcher(RequestMatcher)} 或其他类似方法进行限制。
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public final class HttpSecurity extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity>
        implements SecurityBuilder<DefaultSecurityFilterChain>, HttpSecurityBuilder<HttpSecurity> {
    private final RequestMatcherConfigurer requestMatcherConfigurer;
    private List<OrderedFilter> filters = new ArrayList<>();

    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;

    private final FilterOrderRegistration filterOrders = new FilterOrderRegistration();

    /**
     * Creates a new instance
     *
     * @param objectPostProcessor the {@link ObjectPostProcessor} that should be used
     *                            additional updates
     * @param sharedObjects       the shared Objects to initialize the {@link HttpSecurity} with
     * @see WebSecurityConfiguration
     */
    @SuppressWarnings("unchecked")
    public HttpSecurity(ObjectPostProcessor<Object> objectPostProcessor, Map<Class<?>, Object> sharedObjects) {
        super(objectPostProcessor);
        for (Map.Entry<Class<?>, Object> entry : sharedObjects.entrySet()) {
            setSharedObject((Class<Object>) entry.getKey(), entry.getValue());
        }
        ApplicationContext context = (ApplicationContext) sharedObjects.get(ApplicationContext.class);
        this.requestMatcherConfigurer = new RequestMatcherConfigurer(context);
    }

    private ApplicationContext getContext() {
        return getSharedObject(ApplicationContext.class);
    }

    // TODO ExpressionUrlAuthorizationConfigurer

    /**
     * Sets up management of the {@link SecurityContext} on the
     * {@link SecurityContextHolder} between {@link HttpServletRequest}'s. This is
     * automatically applied when using {@link WebSecurityConfigurerAdapter}.
     *
     * @return the {@link SecurityContextConfigurer} for further customizations
     * @throws Exception
     */
    public SecurityContextConfigurer<HttpSecurity> securityContext() throws Exception {
        return getOrApply(new SecurityContextConfigurer<>());
    }

    /**
     * Sets up management of the {@link SecurityContext} on the
     * {@link SecurityContextHolder} between {@link HttpServletRequest}'s. This is
     * automatically applied when using {@link WebSecurityConfigurerAdapter}.
     * <p>
     * The following customization specifies the shared {@link org.lan.iti.iha.security.context.SecurityContextRepository}
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class SecurityContextSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(HttpSecurity http) throws Exception {
     * 		http
     * 			.securityContext((securityContext) ->
     * 				securityContext
     * 					.securityContextRepository(SCR)
     * 			);
     *    }
     * }
     * </pre>
     *
     * @param securityContextCustomizer the {@link Customizer} to provide more options for
     *                                  the {@link SecurityContextConfigurer}
     * @return the {@link HttpSecurity} for further customizations
     * @throws Exception
     */
    public HttpSecurity securityContext(Customizer<SecurityContextConfigurer<HttpSecurity>> securityContextCustomizer)
            throws Exception {
        securityContextCustomizer.customize(getOrApply(new SecurityContextConfigurer<>()));
        return HttpSecurity.this;
    }

    @Override
    public <C> void setSharedObject(Class<C> sharedType, C object) {
        super.setSharedObject(sharedType, object);
    }

    @Override
    protected void beforeConfigure() throws Exception {
//        setSharedObject(AuthenticationManager.class, getAuthenticationRegistry().build());
    }

    @Override
    protected DefaultSecurityFilterChain performBuild() {
        this.filters.sort(OrderComparator.INSTANCE);
        List<Filter> sortedFilters = new ArrayList<>(this.filters.size());
        for (Filter filter : this.filters) {
            sortedFilters.add(((OrderedFilter) filter).filter);
        }
        return new DefaultSecurityFilterChain(this.requestMatcher, sortedFilters);
    }

    @Override
    public HttpSecurity addFilterAfter(Filter filter, Class<? extends Filter> afterFilter) {
        return addFilterAtOffsetOf(filter, 1, afterFilter);
    }

    @Override
    public HttpSecurity addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter) {
        return addFilterAtOffsetOf(filter, -1, beforeFilter);
    }

    private HttpSecurity addFilterAtOffsetOf(Filter filter, int offset, Class<? extends Filter> registeredFilter) {
        int order = this.filterOrders.getOrder(registeredFilter) + offset;
        this.filters.add(new OrderedFilter(filter, order));
        this.filterOrders.put(filter.getClass(), order);
        return this;
    }

    @Override
    public HttpSecurity addFilter(Filter filter) {
        Integer order = this.filterOrders.getOrder(filter.getClass());
        if (order == null) {
            throw new IllegalArgumentException("The Filter class " + filter.getClass().getName()
                    + " does not have a registered order and cannot be added without a specified order. Consider using addFilterBefore or addFilterAfter instead.");
        }
        this.filters.add(new OrderedFilter(filter, order));
        return this;
    }

    /**
     * Adds the Filter at the location of the specified Filter class. For example, if you
     * want the filter CustomFilter to be registered in the same position as
     * {@link UsernamePasswordAuthenticationFilter}, you can invoke:
     *
     * <pre>
     * addFilterAt(new CustomFilter(), UsernamePasswordAuthenticationFilter.class)
     * </pre>
     * <p>
     * Registration of multiple Filters in the same location means their ordering is not
     * deterministic. More concretely, registering multiple Filters in the same location
     * does not override existing Filters. Instead, do not register Filters you do not
     * want to use.
     *
     * @param filter   the Filter to register
     * @param atFilter the location of another {@link Filter} that is already registered
     *                 (i.e. known) with Spring Security.
     * @return the {@link HttpSecurity} for further customizations
     */
    public HttpSecurity addFilterAt(Filter filter, Class<? extends Filter> atFilter) {
        return addFilterAtOffsetOf(filter, 0, atFilter);
    }

    /**
     * Allows specifying which {@link HttpServletRequest} instances this
     * {@link HttpSecurity} will be invoked on. This method allows for easily invoking the
     * {@link HttpSecurity} for multiple different {@link RequestMatcher} instances. If
     * only a single {@link RequestMatcher} is necessary consider using
     * {@link #mvcMatcher(String)}, {@link #antMatcher(String)},
     * {@link #regexMatcher(String)}, or {@link #requestMatcher(RequestMatcher)}.
     *
     * <p>
     * Invoking {@link #requestMatchers()} will not override previous invocations of
     * {@link #mvcMatcher(String)}}, {@link #requestMatchers()},
     * {@link #antMatcher(String)}, {@link #regexMatcher(String)}, and
     * {@link #requestMatcher(RequestMatcher)}.
     * </p>
     *
     * <h3>Example Configurations</h3>
     * <p>
     * The following configuration enables the {@link HttpSecurity} for URLs that begin
     * with "/api/" or "/oauth/".
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(HttpSecurity http) throws Exception {
     * 		http
     * 			.requestMatchers()
     * 				.antMatchers(&quot;/api/**&quot;, &quot;/oauth/**&quot;)
     * 				.and()
     * 			.authorizeRequests()
     * 				.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 				.and()
     * 			.httpBasic();
     *    }
     *
     * 	&#064;Override
     * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     * 		auth
     * 			.inMemoryAuthentication()
     * 				.withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
     *    }
     * }
     * </pre>
     * <p>
     * The configuration below is the same as the previous configuration.
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(HttpSecurity http) throws Exception {
     * 		http
     * 			.requestMatchers()
     * 				.antMatchers(&quot;/api/**&quot;)
     * 				.antMatchers(&quot;/oauth/**&quot;)
     * 				.and()
     * 			.authorizeRequests()
     * 				.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 				.and()
     * 			.httpBasic();
     *    }
     *
     * 	&#064;Override
     * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     * 		auth
     * 			.inMemoryAuthentication()
     * 				.withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
     *    }
     * }
     * </pre>
     * <p>
     * The configuration below is also the same as the above configuration.
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(HttpSecurity http) throws Exception {
     * 		http
     * 			.requestMatchers()
     * 				.antMatchers(&quot;/api/**&quot;)
     * 				.and()
     * 			 .requestMatchers()
     * 				.antMatchers(&quot;/oauth/**&quot;)
     * 				.and()
     * 			.authorizeRequests()
     * 				.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 				.and()
     * 			.httpBasic();
     *    }
     *
     * 	&#064;Override
     * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     * 		auth
     * 			.inMemoryAuthentication()
     * 				.withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
     *    }
     * }
     * </pre>
     *
     * @return the {@link RequestMatcherConfigurer} for further customizations
     */
    public RequestMatcherConfigurer requestMatchers() {
        return this.requestMatcherConfigurer;
    }

    /**
     * Allows specifying which {@link HttpServletRequest} instances this
     * {@link HttpSecurity} will be invoked on. This method allows for easily invoking the
     * {@link HttpSecurity} for multiple different {@link RequestMatcher} instances. If
     * only a single {@link RequestMatcher} is necessary consider using
     * {@link #mvcMatcher(String)}, {@link #antMatcher(String)},
     * {@link #regexMatcher(String)}, or {@link #requestMatcher(RequestMatcher)}.
     *
     * <p>
     * Invoking {@link #requestMatchers()} will not override previous invocations of
     * {@link #mvcMatcher(String)}}, {@link #requestMatchers()},
     * {@link #antMatcher(String)}, {@link #regexMatcher(String)}, and
     * {@link #requestMatcher(RequestMatcher)}.
     * </p>
     *
     * <h3>Example Configurations</h3>
     * <p>
     * The following configuration enables the {@link HttpSecurity} for URLs that begin
     * with "/api/" or "/oauth/".
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(HttpSecurity http) throws Exception {
     * 		http
     * 			.requestMatchers((requestMatchers) ->
     * 				requestMatchers
     * 					.antMatchers(&quot;/api/**&quot;, &quot;/oauth/**&quot;)
     * 			)
     * 			.authorizeRequests((authorizeRequests) ->
     * 				authorizeRequests
     * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			)
     * 			.httpBasic(withDefaults());
     *    }
     * }
     * </pre>
     * <p>
     * The configuration below is the same as the previous configuration.
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(HttpSecurity http) throws Exception {
     * 		http
     * 			.requestMatchers((requestMatchers) ->
     * 				requestMatchers
     * 					.antMatchers(&quot;/api/**&quot;)
     * 					.antMatchers(&quot;/oauth/**&quot;)
     * 			)
     * 			.authorizeRequests((authorizeRequests) ->
     * 				authorizeRequests
     * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			)
     * 			.httpBasic(withDefaults());
     *    }
     * }
     * </pre>
     * <p>
     * The configuration below is also the same as the above configuration.
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(HttpSecurity http) throws Exception {
     * 		http
     * 			.requestMatchers((requestMatchers) ->
     * 				requestMatchers
     * 					.antMatchers(&quot;/api/**&quot;)
     * 			)
     * 			.requestMatchers((requestMatchers) ->
     * 			requestMatchers
     * 				.antMatchers(&quot;/oauth/**&quot;)
     * 			)
     * 			.authorizeRequests((authorizeRequests) ->
     * 				authorizeRequests
     * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			)
     * 			.httpBasic(withDefaults());
     *    }
     * }
     * </pre>
     *
     * @param requestMatcherCustomizer the {@link Customizer} to provide more options for
     *                                 the {@link RequestMatcherConfigurer}
     * @return the {@link HttpSecurity} for further customizations
     */
    public HttpSecurity requestMatchers(Customizer<RequestMatcherConfigurer> requestMatcherCustomizer) {
        requestMatcherCustomizer.customize(this.requestMatcherConfigurer);
        return HttpSecurity.this;
    }

    /**
     * Allows configuring the {@link HttpSecurity} to only be invoked when matching the
     * provided {@link RequestMatcher}. If more advanced configuration is necessary,
     * consider using {@link #requestMatchers()}.
     *
     * <p>
     * Invoking {@link #requestMatcher(RequestMatcher)} will override previous invocations
     * of {@link #requestMatchers()}, {@link #mvcMatcher(String)},
     * {@link #antMatcher(String)}, {@link #regexMatcher(String)}, and
     * {@link #requestMatcher(RequestMatcher)}.
     * </p>
     *
     * @param requestMatcher the {@link RequestMatcher} to use (i.e. new
     *                       AntPathRequestMatcher("/admin/**","GET") )
     * @return the {@link HttpSecurity} for further customizations
     * @see #requestMatchers()
     * @see #antMatcher(String)
     * @see #regexMatcher(String)
     */
    public HttpSecurity requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return this;
    }

    /**
     * Allows configuring the {@link HttpSecurity} to only be invoked when matching the
     * provided ant pattern. If more advanced configuration is necessary, consider using
     * {@link #requestMatchers()} or {@link #requestMatcher(RequestMatcher)}.
     *
     * <p>
     * Invoking {@link #antMatcher(String)} will override previous invocations of
     * {@link #mvcMatcher(String)}}, {@link #requestMatchers()},
     * {@link #antMatcher(String)}, {@link #regexMatcher(String)}, and
     * {@link #requestMatcher(RequestMatcher)}.
     * </p>
     *
     * @param antPattern the Ant Pattern to match on (i.e. "/admin/**")
     * @return the {@link HttpSecurity} for further customizations
     * @see AntPathRequestMatcher
     */
    public HttpSecurity antMatcher(String antPattern) {
        return requestMatcher(new AntPathRequestMatcher(antPattern));
    }

    /**
     * Allows configuring the {@link HttpSecurity} to only be invoked when matching the
     * provided Spring MVC pattern. If more advanced configuration is necessary, consider
     * using {@link #requestMatchers()} or {@link #requestMatcher(RequestMatcher)}.
     *
     * <p>
     * Invoking {@link #mvcMatcher(String)} will override previous invocations of
     * {@link #mvcMatcher(String)}}, {@link #requestMatchers()},
     * {@link #antMatcher(String)}, {@link #regexMatcher(String)}, and
     * {@link #requestMatcher(RequestMatcher)}.
     * </p>
     *
     * @param mvcPattern the Spring MVC Pattern to match on (i.e. "/admin/**")
     * @return the {@link HttpSecurity} for further customizations
     * @see MvcRequestMatcher
     */
    public HttpSecurity mvcMatcher(String mvcPattern) {
        HandlerMappingIntrospector introspector = new HandlerMappingIntrospector(getContext());
        return requestMatcher(new MvcRequestMatcher(introspector, mvcPattern));
    }

    /**
     * Allows configuring the {@link HttpSecurity} to only be invoked when matching the
     * provided regex pattern. If more advanced configuration is necessary, consider using
     * {@link #requestMatchers()} or {@link #requestMatcher(RequestMatcher)}.
     *
     * <p>
     * Invoking {@link #regexMatcher(String)} will override previous invocations of
     * {@link #mvcMatcher(String)}}, {@link #requestMatchers()},
     * {@link #antMatcher(String)}, {@link #regexMatcher(String)}, and
     * {@link #requestMatcher(RequestMatcher)}.
     * </p>
     *
     * @param pattern the Regular Expression to match on (i.e. "/admin/.+")
     * @return the {@link HttpSecurity} for further customizations
     * @see RegexRequestMatcher
     */
    public HttpSecurity regexMatcher(String pattern) {
        return requestMatcher(new RegexRequestMatcher(pattern, null));
    }

    /**
     * If the {@link SecurityConfigurer} has already been specified get the original,
     * otherwise apply the new {@link SecurityConfigurerAdapter}.
     *
     * @param configurer the {@link SecurityConfigurer} to apply if one is not found for
     *                   this {@link SecurityConfigurer} class.
     * @return the current {@link SecurityConfigurer} for the configurer passed in
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private <C extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> C getOrApply(C configurer)
            throws Exception {
        C existingConfig = (C) getConfigurer(configurer.getClass());
        if (existingConfig != null) {
            return existingConfig;
        }
        return apply(configurer);
    }

    /**
     * An extension to {@link RequestMatcherConfigurer} that allows optionally configuring
     * the servlet path.
     */
    public final class MvcMatchersRequestMatcherConfigurer extends RequestMatcherConfigurer {

        /**
         * Creates a new instance
         *
         * @param context  the {@link ApplicationContext} to use
         * @param matchers the {@link MvcRequestMatcher} instances to set the servlet path
         *                 on if {@link #servletPath(String)} is set.
         */
        private MvcMatchersRequestMatcherConfigurer(ApplicationContext context, List<MvcRequestMatcher> matchers) {
            super(context);
            this.matchers = new ArrayList<>(matchers);
        }

        public RequestMatcherConfigurer servletPath(String servletPath) {
            for (RequestMatcher matcher : this.matchers) {
                ((MvcRequestMatcher) matcher).setServletPath(servletPath);
            }
            return this;
        }
    }

    /**
     * Allows mapping HTTP requests that this {@link HttpSecurity} will be used for
     */
    public class RequestMatcherConfigurer extends AbstractRequestMatcherRegistry<RequestMatcherConfigurer> {

        protected List<RequestMatcher> matchers = new ArrayList<>();

        RequestMatcherConfigurer(ApplicationContext context) {
            setApplicationContext(context);
        }

        @Override
        public MvcMatchersRequestMatcherConfigurer mvcMatchers(HttpMethod method, String... mvcPatterns) {
            List<MvcRequestMatcher> mvcMatchers = createMvcMatchers(method, mvcPatterns);
            setMatchers(mvcMatchers);
            return new MvcMatchersRequestMatcherConfigurer(getContext(), mvcMatchers);
        }

        @Override
        public MvcMatchersRequestMatcherConfigurer mvcMatchers(String... patterns) {
            return mvcMatchers(null, patterns);
        }

        @Override
        protected RequestMatcherConfigurer chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            setMatchers(requestMatchers);
            return this;
        }

        private void setMatchers(List<? extends RequestMatcher> requestMatchers) {
            this.matchers.addAll(requestMatchers);
            requestMatcher(new OrRequestMatcher(this.matchers));
        }

        /**
         * Return the {@link HttpSecurity} for further customizations
         *
         * @return the {@link HttpSecurity} for further customizations
         */
        public HttpSecurity and() {
            return HttpSecurity.this;
        }
    }

    /**
     * A Filter that implements Ordered to be sorted. After sorting occurs, the original
     * filter is what is used by FilterChainProxy
     */
    private static final class OrderedFilter implements Ordered, Filter {

        private final Filter filter;

        private final int order;

        private OrderedFilter(Filter filter, int order) {
            this.filter = filter;
            this.order = order;
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                throws IOException, ServletException {
            this.filter.doFilter(servletRequest, servletResponse, filterChain);
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        @Override
        public String toString() {
            return "OrderedFilter{" + "filter=" + this.filter + ", order=" + this.order + '}';
        }
    }
}
