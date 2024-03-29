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

package org.springframework.cloud.openfeign;

import lombok.Getter;
import org.lan.iti.cloud.autoconfigure.feign.FeignContractAutoConfiguration;
import org.lan.iti.cloud.autoconfigure.feign.properties.Client;
import org.lan.iti.cloud.autoconfigure.feign.properties.ITIFeignProperties;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * feign 自动配置功能
 * <p>
 * 1. 支持META-INF(spring.factories)自动导入
 * 2. 支持配置文件(*.properties/*.yml)自动导入
 * </p>
 *
 * @author NorthLan
 * @date 2020-03-04
 * @url https://noahlan.com
 */
public class ITIFeignClientsRegistrar implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware, EnvironmentAware {
    @Getter
    private ClassLoader beanClassLoader;

    @Getter
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        // register
        registerClientsByFactories(registry);
        registerClientsByProperties(registry);
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    private void registerClientsByFactories(BeanDefinitionRegistry registry) {
        List<String> feignClients = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(), getBeanClassLoader());
        // 如果 spring.factories 里为空
        if (feignClients.isEmpty()) {
            return;
        }
        for (String className : feignClients) {
            try {
                Class<?> clazz = beanClassLoader.loadClass(className);
                AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(clazz, FeignClient.class);
                if (attributes == null) {
                    continue;
                }
                // 如果已经存在该 bean，支持原生的 Feign
                if (registry.containsBeanDefinition(className)) {
                    continue;
                }
                registerClientConfiguration(registry, getClientName(attributes), attributes.get("configuration"));

                validate(attributes);
                BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);
                definition.addPropertyValue("url", getUrl(attributes));
                definition.addPropertyValue("path", getPath(attributes));
                String name = getName(attributes);
                definition.addPropertyValue("name", name);

                // 兼容最新版本的 spring-cloud-openfeign，尚未发布
                StringBuilder aliasBuilder = new StringBuilder(18);
                if (attributes.containsKey("contextId")) {
                    String contextId = getContextId(attributes);
                    aliasBuilder.append(contextId);
                    definition.addPropertyValue("contextId", contextId);
                } else {
                    aliasBuilder.append(name);
                }

                definition.addPropertyValue("type", className);
                definition.addPropertyValue("decode404", attributes.get("decode404"));
                definition.addPropertyValue("fallback", attributes.get("fallback"));
                definition.addPropertyValue("fallbackFactory", attributes.get("fallbackFactory"));
                definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

                AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

                // alias
                String alias = aliasBuilder.append("FeignClient").toString();

                // has a default, won't be null
                boolean primary = (Boolean) attributes.get("primary");

                beanDefinition.setPrimary(primary);

                String qualifier = getQualifier(attributes);
                if (StringUtils.hasText(qualifier)) {
                    alias = qualifier;
                }

                BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[]{alias});
                BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerClientsByProperties(BeanDefinitionRegistry registry) {
        ITIFeignProperties properties = Binder.get(environment)
                .bind(ITIFeignProperties.PREFIX, Bindable.of(ITIFeignProperties.class))
                .orElse(null);
        if (properties == null) {
            return;
        }
        List<Client> clients = properties.getClients();
        if (CollectionUtils.isEmpty(clients)) {
            return;
        }
        for (Client client : clients) {
            if (client.getClazz() == null) {
                continue;
            }
            String className = client.getClazz().getName();
            if (registry.containsBeanDefinition(className)) {
                continue;
            }

            registerClientConfiguration(registry, getClientName(client), client.getConfiguration());
            //
            validate(client.getFallback(), client.getFallbackFactory());
            BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);
            definition.addPropertyValue("url", getUrl(client));
            definition.addPropertyValue("path", getPath(client));
            String name = getName(client);
            definition.addPropertyValue("name", name);
            String contextId = getContextId(client);
            definition.addPropertyValue("contextId", contextId);

            definition.addPropertyValue("type", className);
            definition.addPropertyValue("decode404", client.isDecode404());
            definition.addPropertyValue("fallback", client.getFallback());
            definition.addPropertyValue("fallbackFactory", client.getFallbackFactory());
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

            AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
            String alias = contextId + "FeignClient";
            // has a default, won't be null
            beanDefinition.setPrimary(client.isPrimary());
            if (StringUtils.hasText(client.getQualifier())) {
                alias = client.getQualifier();
            }
            BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[]{alias});
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        }
    }

    /**
     * Return the class used by {@link SpringFactoriesLoader} to load configuration
     * candidates.
     *
     * @return the factory class
     */
    private Class<?> getSpringFactoriesLoaderFactoryClass() {
        return FeignContractAutoConfiguration.class;
    }

    private void validate(Map<String, Object> attributes) {
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
        // This blows up if an aliased property is overspecified
        FeignClientsRegistrar.validateFallback(annotation.getClass("fallback"));
        FeignClientsRegistrar.validateFallbackFactory(annotation.getClass("fallbackFactory"));
    }

    private void validate(Class<?> fallback, Class<?> fallbackFactory) {
        // This blows up if an aliased property is overspecified
        FeignClientsRegistrar.validateFallback(fallback);
        FeignClientsRegistrar.validateFallbackFactory(fallbackFactory);
    }

    private String getName(Map<String, Object> attributes) {
        String name = (String) attributes.get("serviceId");
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("name");
        }
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("value");
        }
        name = resolve(name);
        return FeignClientsRegistrar.getName(name);
    }

    private String getName(Client client) {
        String name = client.getName();
        return FeignClientsRegistrar.getName(name);
    }

    private String getContextId(Map<String, Object> attributes) {
        String contextId = (String) attributes.get("contextId");
        if (!StringUtils.hasText(contextId)) {
            return getName(attributes);
        }

        contextId = resolve(contextId);
        return FeignClientsRegistrar.getName(contextId);
    }

    private String getContextId(Client client) {
        String contextId = client.getContextId();
        if (!StringUtils.hasText(contextId)) {
            return getName(client);
        }
        return contextId;
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    private String getUrl(Map<String, Object> attributes) {
        String url = resolve((String) attributes.get("url"));
        return FeignClientsRegistrar.getUrl(url);
    }

    private String getUrl(Client client) {
        return FeignClientsRegistrar.getUrl(client.getUrl());
    }

    private String getPath(Map<String, Object> attributes) {
        String path = resolve((String) attributes.get("path"));
        return FeignClientsRegistrar.getPath(path);
    }

    private String getPath(Client client) {
        return FeignClientsRegistrar.getUrl(client.getPath());
    }

    @Nullable
    private String getQualifier(@Nullable Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    @Nullable
    private String getClientName(@Nullable Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String value = (String) client.get("contextId");
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("value");
        }
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("name");
        }
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("serviceId");
        }
        if (StringUtils.hasText(value)) {
            return value;
        }
        throw new IllegalStateException("Either 'name' or 'value' must be provided in @" + FeignClient.class.getSimpleName());
    }

    private String getClientName(Client client) {
        String value = client.getContextId();
        if (!StringUtils.hasText(value)) {
            value = client.getName();
        }
        if (StringUtils.hasText(value)) {
            return value;
        }
        throw new IllegalStateException("'name' must be provided in properties");
    }

    private void registerClientConfiguration(BeanDefinitionRegistry registry, Object name, Object configuration) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FeignClientSpecification.class);
        builder.addConstructorArgValue(name);
        builder.addConstructorArgValue(configuration);
        registry.registerBeanDefinition(name + "." + FeignClientSpecification.class.getSimpleName(), builder.getBeanDefinition());
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
