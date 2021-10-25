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

package org.lan.iti.cloud.security.config.annotation.web.configurers;

import org.lan.iti.cloud.security.access.ConfigAttribute;
import org.lan.iti.cloud.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.lan.iti.iha.security.matcher.RequestMatcher;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 用于注册 {@link RequestMatcher} 的基类。
 * 例如，它可能允许指定哪个 {@link RequestMatcher} 需要一定级别的授权。
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public abstract class AbstractConfigAttributeRequestMatcherRegistry<C> extends AbstractRequestMatcherRegistry<C> {
    private final List<UrlMapping> urlMappings = new ArrayList<>();
    private List<RequestMatcher> unmappedMatchers;

    /**
     * Gets the {@link UrlMapping} added by subclasses in
     * {@link #chainRequestMatchers(java.util.List)}. May be empty.
     *
     * @return the {@link UrlMapping} added by subclasses in
     * {@link #chainRequestMatchers(java.util.List)}
     */
    final List<UrlMapping> getUrlMappings() {
        return this.urlMappings;
    }

    /**
     * Adds a {@link UrlMapping} added by subclasses in
     * {@link #chainRequestMatchers(java.util.List)} and resets the unmapped
     * {@link RequestMatcher}'s.
     *
     * @param urlMapping {@link UrlMapping} the mapping to add
     */
    final void addMapping(UrlMapping urlMapping) {
        this.unmappedMatchers = null;
        this.urlMappings.add(urlMapping);
    }

    /**
     * Marks the {@link RequestMatcher}'s as unmapped and then calls
     * {@link #chainRequestMatchersInternal(List)}.
     *
     * @param requestMatchers the {@link RequestMatcher} instances that were created
     * @return the chained Object for the subclass which allows association of something
     * else to the {@link RequestMatcher}
     */
    @Override
    protected final C chainRequestMatchers(List<RequestMatcher> requestMatchers) {
        this.unmappedMatchers = requestMatchers;
        return chainRequestMatchersInternal(requestMatchers);
    }

    /**
     * Subclasses should implement this method for returning the object that is chained to
     * the creation of the {@link RequestMatcher} instances.
     *
     * @param requestMatchers the {@link RequestMatcher} instances that were created
     * @return the chained Object for the subclass which allows association of something
     * else to the {@link RequestMatcher}
     */
    protected abstract C chainRequestMatchersInternal(List<RequestMatcher> requestMatchers);

    /**
     * Adds a {@link UrlMapping} added by subclasses in
     * {@link #chainRequestMatchers(java.util.List)} at a particular index.
     *
     * @param index      the index to add a {@link UrlMapping}
     * @param urlMapping {@link UrlMapping} the mapping to add
     */
    final void addMapping(int index, UrlMapping urlMapping) {
        this.urlMappings.add(index, urlMapping);
    }

    /**
     * Creates the mapping of {@link RequestMatcher} to {@link Collection} of
     * {@link ConfigAttribute} instances
     *
     * @return the mapping of {@link RequestMatcher} to {@link Collection} of
     * {@link ConfigAttribute} instances. Cannot be null.
     */
    final LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> createRequestMap() {
        Assert.state(this.unmappedMatchers == null, () -> "An incomplete mapping was found for " + this.unmappedMatchers
                + ". Try completing it with something like requestUrls().<something>.hasRole('USER')");
        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();
        for (UrlMapping mapping : getUrlMappings()) {
            RequestMatcher matcher = mapping.getRequestMatcher();
            Collection<ConfigAttribute> configAttrs = mapping.getConfigAttrs();
            requestMap.put(matcher, configAttrs);
        }
        return requestMap;
    }

    /**
     * A mapping of {@link RequestMatcher} to {@link Collection} of
     * {@link ConfigAttribute} instances
     */
    static final class UrlMapping {

        private final RequestMatcher requestMatcher;

        private final Collection<ConfigAttribute> configAttrs;

        UrlMapping(RequestMatcher requestMatcher, Collection<ConfigAttribute> configAttrs) {
            this.requestMatcher = requestMatcher;
            this.configAttrs = configAttrs;
        }

        RequestMatcher getRequestMatcher() {
            return this.requestMatcher;
        }

        Collection<ConfigAttribute> getConfigAttrs() {
            return this.configAttrs;
        }

    }
}