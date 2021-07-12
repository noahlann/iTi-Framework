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
//package org.lan.iti.common.core.spi.factory;
//
//import org.lan.iti.common.core.spi.ExtensionFactory;
//import org.lan.iti.common.core.spi.ExtensionLoader;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author NorthLan
// * @date 2021-07-08
// * @url https://noahlan.com
// */
//// TODO Adaptive
//public class AdaptiveExtensionFactory implements ExtensionFactory {
//    private final List<ExtensionFactory> factories;
//
//    public AdaptiveExtensionFactory() {
//        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
//        List<ExtensionFactory> list = new ArrayList<>();
//        for (String name : loader.getSupportedExtensions()) {
//            list.add(loader.getExtension(name));
//        }
//        factories = Collections.unmodifiableList(list);
//    }
//
//
//    @Override
//    public <T> T getExtension(Class<T> type, String name) {
//        for (ExtensionFactory factory : factories) {
//            T extension = factory.getExtension(type, name);
//            if (extension != null) {
//                return extension;
//            }
//        }
//        return null;
//    }
//}