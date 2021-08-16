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

package org.lan.iti.common.extension.injector;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.extension.ExtensionLoader;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * SPI 注入器
 * <p>
 * 可自动为Extension加载的对象注入其它Extension加载的对象实例
 *
 * @author NorthLan
 * @date 2021/8/16
 * @url https://blog.noahlan.com
 */
@Slf4j
public class SPIExtensionInjector extends AbstractExtensionInjector {

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1000;
    }

    @Override
    protected boolean isInjectByField(Field field) {
        return field.isAnnotationPresent(Resource.class);
    }

    @Override
    protected String getTypeName(Field field) {
        Resource resource = field.getAnnotation(Resource.class);
        return resource.name();
    }

    @Override
    protected List<Object> loadListByType(Class type) {
        return ExtensionLoader.getLoader(type).getList();
    }

    @Override
    protected Object loadByName(Class type, String name) {
        return ExtensionLoader.getLoader(type).getFirst(name, true);
    }

    @Override
    protected Object loadByType(Class type) {
        return ExtensionLoader.getLoader(type).getFirst();
    }
}
