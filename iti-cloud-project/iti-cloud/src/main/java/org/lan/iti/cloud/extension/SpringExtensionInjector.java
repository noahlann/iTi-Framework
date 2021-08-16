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

package org.lan.iti.cloud.extension;

import org.lan.iti.common.extension.injector.AbstractExtensionInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Spring-Extension 注入器
 * <p>
 * 提供为Extension注入其它Spring-Bean的功能
 * 此方案在未开启 {@link org.lan.iti.cloud.starter.extension.annotation.EnableSpringExtension} 时可注入spring-bean
 *
 * @author NorthLan
 * @date 2021/8/16
 * @url https://blog.noahlan.com
 */
public class SpringExtensionInjector extends AbstractExtensionInjector implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 999;
    }

    @Override
    protected boolean isInjectByField(Field field) {
        return field.isAnnotationPresent(Resource.class) || field.isAnnotationPresent(Autowired.class);
    }

    @Override
    protected String getTypeName(Field field) {
        Resource resource = field.getAnnotation(Resource.class);
        if (resource != null) {
            return resource.name();
        }
        Autowired autowired = field.getAnnotation(Autowired.class);
        if (autowired != null) {
            Qualifier qualifier = field.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                return qualifier.value();
            }
        }
        return field.getName();
    }

    @Override
    protected Collection loadListByType(Class type) {
        return applicationContext.getBeansOfType(type).values();
    }

    @Override
    protected Object loadByName(Class type, String name) {
        return applicationContext.getBean(name, type);
    }

    @Override
    protected Object loadByType(Class type) {
        return applicationContext.getBean(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
