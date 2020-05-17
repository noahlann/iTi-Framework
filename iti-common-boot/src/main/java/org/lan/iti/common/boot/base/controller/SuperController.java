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
 * 超级控制器抽象类,继承该类将会使控制器具有如下方法(接口)
 * <pre>
 * get:
 *     get          GET /{id}                   通过ID查询
 *     getList      GET /list?                  查询列表 GET /list?
 *     getPage      GET /page?                  分页查询
 * save:
 *     save         POST /                      保存
 *     saveList     POST /list                  批量保存
 * update:
 *     update       PUT /                       修改
 * delete:
 *     delete       DELETE /{id}                删除
 *     deleteList   DELETE /list                批量删除
 * poi:
 *     preview      GET /export/preview         导出预览
 *     exportExcel  POST /export/excel          导出Excel
 *     importExcel  POST /export/excel          导入Excel
 * </pre>
 * <p>
 * 同时将拥有
 * <pre>
 *     自动注入与Entity相对应的Service
 * </pre>
 *
 * @param <Service>   服务
 * @param <Entity>    实体
 * @param <QueryDTO>  查询条件数据传输对象
 * @param <UpdateDTO> 更新条件数据传输对象
 * @param <SaveDTO>   新增条件数据传输对象
 * @param <DeleteDTO> 删除条件数据传输对象
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public abstract class SuperController<Service extends BaseService<Entity>, Entity extends Serializable,
        QueryDTO extends Serializable,
        UpdateDTO extends Serializable,
        SaveDTO extends Serializable,
        DeleteDTO extends Serializable>
        implements
        QueryController<Entity, QueryDTO>,
        UpdateController<Entity, UpdateDTO>,
        SaveController<Entity, SaveDTO>,
        DeleteController<Entity, DeleteDTO> {

    protected Class<Entity> entityClass = null;

    @Autowired
    protected Service baseService;

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
