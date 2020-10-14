///*
// *
// *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.common.doc.springdoc;
//
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import io.swagger.v3.oas.models.servers.Server;
//import lombok.AllArgsConstructor;
//import org.lan.iti.common.core.util.PropertiesUtils;
//import org.lan.iti.common.doc.properties.SpringdocProperties;
//import org.springdoc.core.GroupedOpenApi;
//import org.springdoc.core.SpringDocConfigProperties;
//import org.springdoc.core.customizers.OpenApiCustomiser;
//import org.springdoc.core.customizers.OperationCustomizer;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Springdoc 自动装配
// *
// * @author NorthLan
// * @date 2020-09-14
// * @url https://noahlan.com
// */
//@Configuration
//@AllArgsConstructor
//@EnableConfigurationProperties(SpringdocProperties.class)
//@ConditionalOnClass({OpenAPI.class, SpringDocConfigProperties.class})
//public class SpringDocAutoConfiguration {
//    private final SpringdocProperties properties;
//    private final Optional<List<OperationCustomizer>> operationCustomizers;
//    private final Optional<List<OpenApiCustomiser>> openApiCustomisers;
//
//    @Bean
//    public GroupedOpenApi defaultGroup() {
//        SpringDocConfigProperties.GroupConfig elt = properties.getDefaultGroup();
//        GroupedOpenApi.Builder builder = GroupedOpenApi.builder();
//        if (!CollectionUtils.isEmpty(elt.getPackagesToScan())) {
//            builder.packagesToScan(elt.getPackagesToScan().toArray(new String[0]));
//        }
//        if (!CollectionUtils.isEmpty(elt.getPathsToMatch())) {
//            builder.pathsToMatch(elt.getPathsToMatch().toArray(new String[0]));
//        }
//        if (!CollectionUtils.isEmpty(elt.getPackagesToExclude())) {
//            builder.packagesToExclude(elt.getPackagesToExclude().toArray(new String[0]));
//        }
//        if (!CollectionUtils.isEmpty(elt.getPathsToExclude())) {
//            builder.pathsToExclude(elt.getPathsToExclude().toArray(new String[0]));
//        }
//        operationCustomizers.ifPresent(list -> list.forEach(builder::addOperationCustomizer));
//        openApiCustomisers.ifPresent(list -> list.forEach(builder::addOpenApiCustomiser));
//        return builder.group(elt.getGroup()).build();
//    }
//
//    @Bean
//    public OpenAPI api() {
//        return new OpenAPI()
//                .components(new Components()
//                        .addSecuritySchemes("oauth2", oAuth2Scheme()))
//                .addServersItem(new Server()
//                        .url("/" + PropertiesUtils.getOrDefault("spring.application.name", "unknown")))
//                .info(info());
//    }
//
//    private SecurityScheme oAuth2Scheme() {
//        return new SecurityScheme()
//                .type(SecurityScheme.Type.OAUTH2)
//                .description("OAuth Protocol 2.0")
//                .flows(properties.getOAuthFlows());
//    }
//
//    private Info info() {
//        return new Info()
//                .title(properties.getTitle())
//                .description(properties.getDescription())
//                .version(properties.getVersion())
//                .termsOfService(properties.getTermsOfService())
//                .contact(properties.getContact())
//                .license(properties.getLicense())
//                .extensions(properties.getExtensions());
//    }
//}
