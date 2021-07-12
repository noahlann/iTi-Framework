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
//package org.lan.iti.cloud.spi;
//
//import cn.hutool.core.collection.ConcurrentHashSet;
//import com.alibaba.spring.util.BeanFactoryUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.lang.NonNull;
//
//import java.util.Set;
//
///**
// * @author NorthLan
// * @date 2021-07-08
// * @url https://noahlan.com
// */
//@Slf4j
//public class SpringExtensionFactory implements ExtensionFactory, ApplicationContextAware {
//    private static final Set<ApplicationContext> CONTEXTS = new ConcurrentHashSet<>();
//
//    public static void addApplicationContext(ApplicationContext context) {
//        CONTEXTS.add(context);
//        if (context instanceof ConfigurableApplicationContext) {
//            ((ConfigurableApplicationContext) context).registerShutdownHook();
//        }
//    }
//
//    public static void removeApplicationContext(ApplicationContext context) {
//        CONTEXTS.remove(context);
//    }
//
//    public static Set<ApplicationContext> getContexts() {
//        return CONTEXTS;
//    }
//
//    // currently for test purpose
//    public static void clearContexts() {
//        CONTEXTS.clear();
//    }
//
//    @Override
//    public <T> T getExtension(Class<T> type, String name) {
//        //SPI should be get from SpiExtensionFactory
//        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
//            return null;
//        }
//
//        for (ApplicationContext context : CONTEXTS) {
//            T bean = BeanFactoryUtils.getOptionalBean(context, name, type);
//            if (bean != null) {
//                return bean;
//            }
//        }
//
//        log.warn("No spring extension (bean) named:" + name + ", try to find an extension (bean) of type " + type.getName());
//
//        return null;
//    }
//
//    @Override
//    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
//        SpringExtensionFactory.addApplicationContext(applicationContext);
//    }
//}
