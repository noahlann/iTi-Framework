///*
// *
// *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
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
//package org.lan.iti.cloud.autoconfigure.security;
//
//import lombok.extern.slf4j.Slf4j;
//import org.lan.iti.cloud.security.component.ITIResourceServerConfigurerAdapter;
//import org.lan.iti.common.core.constants.SecurityConstants;
//import org.springframework.beans.MutablePropertyValues;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.GenericBeanDefinition;
//import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
//import org.springframework.core.type.AnnotationMetadata;
//import org.springframework.lang.NonNull;
//
//import java.util.Map;
//
///**
// * 资源注册
// *
// * @author NorthLan
// * @date 2020-02-24
// * @url https://noahlan.com
// */
//@Slf4j
//public class SecurityBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
//
//    @Override
//    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
//                                        BeanDefinitionRegistry registry) {
//        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableITIResourceServer.class.getName());
//        boolean isLocal = annotationAttributes != null && (Boolean) annotationAttributes.getOrDefault("isLocal", false);
//
//        if (registry.isBeanNameInUse(SecurityConstants.RESOURCE_SERVER_CONFIGURER)) {
//            log.warn("本地存在资源服务器配置，覆盖默认配置:" + SecurityConstants.RESOURCE_SERVER_CONFIGURER);
//        } else {
//            registerResourceServerConfigurer(isLocal, registry);
//        }
//
//        if (registry.isBeanNameInUse(SecurityConstants.RESOURCE_SERVER_TOKEN_SERVICES)) {
//            log.warn("本地存在TokenServices配置，覆盖默认配置:" + SecurityConstants.RESOURCE_SERVER_TOKEN_SERVICES);
//        } else {
//            registerTokenServicesConfigurer(isLocal, registry);
//        }
//    }
//
//    public void registerTokenServicesConfigurer(boolean isLocal, BeanDefinitionRegistry registry) {
//        if (isLocal) {
//            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
//            beanDefinition.setBeanClass(SecurityTokenServicesConfiguration.class);
//
//            registry.registerBeanDefinition(SecurityConstants.RESOURCE_SERVER_TOKEN_SERVICES, beanDefinition);
//        }
//    }
//
//    private void registerResourceServerConfigurer(boolean isLocal,
//                                                  BeanDefinitionRegistry registry) {
//        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
//
//        MutablePropertyValues values = new MutablePropertyValues();
//        values.addPropertyValue("local", isLocal);
//
//        beanDefinition.setPropertyValues(values);
//        beanDefinition.setBeanClass(ITIResourceServerConfigurerAdapter.class);
//
//        registry.registerBeanDefinition(SecurityConstants.RESOURCE_SERVER_CONFIGURER, beanDefinition);
//    }
//}
