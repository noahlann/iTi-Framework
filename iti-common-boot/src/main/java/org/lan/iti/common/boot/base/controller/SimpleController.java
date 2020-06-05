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

package org.lan.iti.common.boot.base.controller;

import org.lan.iti.common.core.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * 为子类注入相关 Service
 *
 * @author NorthLan
 * @date 2020-05-18
 * @url https://noahlan.com
 * @since 1.3.1
 */
public abstract class SimpleController<Service extends BaseService<Entity>, Entity extends Serializable>
        implements BaseController<Entity> {

    protected Class<Entity> entityClass = null;

    @Autowired
    protected Service baseService;

    @SuppressWarnings("unchecked")
    @Override
    public Class<Entity> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class<Entity>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        }
        return entityClass;
    }

    @Override
    public BaseService<Entity> getBaseService() {
        return baseService;
    }
}
