/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.doc.springfox;

import lombok.AllArgsConstructor;
import org.lan.iti.common.doc.properties.SpringfoxProperties;
import org.lan.iti.common.doc.springfox.plugins.QuerydslPredicatePlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * swagger 自动装配
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties(SpringfoxProperties.class)
@AllArgsConstructor
public class SpringfoxAutoConfiguration {
    private final SpringfoxProperties properties;

    /**
     * 默认的排除路径，排除Spring Boot默认的错误处理路径和端点
     */
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");
    private static final String BASE_PATH = "/**";

    @Bean
    @ConditionalOnBean(QuerydslBindingsFactory.class)
    public OperationBuilderPlugin querydslPredicatePlugin(QuerydslBindingsFactory factory) {
        return new QuerydslPredicatePlugin(factory);
    }

    /**
     * 注入优先级调整
     */
    @Bean
    public SwaggerHandlerMappingProcessor swaggerHandlerMappingProcessor() {
        return new SwaggerHandlerMappingProcessor();
    }

    @Bean
    public Docket api() {
        // base-path处理
        if (properties.getBasePath().isEmpty()) {
            properties.getBasePath().add(BASE_PATH);
        }
        // exclude-path处理
        if (properties.getExcludePath().isEmpty()) {
            properties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
        }

        Predicate<String> paths = PathSelectors.any();
        for (String path : properties.getBasePath()) {
            paths = paths.and(PathSelectors.ant(path));
        }
        for (String path : properties.getExcludePath()) {
            paths = paths.and(PathSelectors.ant(path).negate());
        }

        return new Docket(DocumentationType.SWAGGER_2)
                .host(properties.getHost())
                .apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()))
                .paths(paths)
                .build()
                .securitySchemes(Arrays.asList(securitySchema(), apiKey()))
                .securityContexts(Collections.singletonList(securityContext()))
                .pathMapping("/");
    }

    /**
     * 配置默认的全局鉴权策略的开关，通过正则表达式进行匹配；默认匹配所有URL
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(properties.getAuthorization().getAuthRegex()))
                .build();
    }

    /**
     * 默认的全局鉴权策略
     */
    private List<SecurityReference> defaultAuth() {
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        properties.getAuthorization().getAuthorizationScopeList().forEach(authorizationScope -> authorizationScopeList.add(new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[authorizationScopeList.size()];
        return Collections.singletonList(SecurityReference.builder()
                .reference(properties.getAuthorization().getName())
                .scopes(authorizationScopeList.toArray(authorizationScopes))
                .build());
    }

    /**
     * ApiKey
     * 避免knife4j不支持OAuth的schema
     */
    private ApiKey apiKey() {
        return new ApiKey(properties.getAuthorization().getName() + "_ApiKey", HttpHeaders.AUTHORIZATION, "header");
    }

    /**
     * 基于oAuth2的schema
     */
    private OAuth securitySchema() {
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        properties.getAuthorization().getAuthorizationScopeList().forEach(authorizationScope -> authorizationScopeList.add(new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
        ArrayList<GrantType> grantTypes = new ArrayList<>();
        properties.getAuthorization().getTokenUrlList().forEach(tokenUrl -> grantTypes.add(new ResourceOwnerPasswordCredentialsGrant(tokenUrl)));
        return new OAuth(properties.getAuthorization().getName(), authorizationScopeList, grantTypes);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .license(properties.getLicense())
                .licenseUrl(properties.getLicenseUrl())
                .termsOfServiceUrl(properties.getTermsOfServiceUrl())
                .contact(new Contact(properties.getContact().getName(), properties.getContact().getUrl(), properties.getContact().getEmail()))
                .version(properties.getVersion())
                .build();
    }
}
