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

import io.swagger.annotations.ApiOperation;
import org.lan.iti.common.core.interfaces.validator.SaveGroup;
import org.lan.iti.common.core.util.BeanUtils;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.scanner.annotation.ITIApi;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * 新增 控制器接口
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public interface SaveController<Entity extends Serializable, SaveDTO extends Serializable> extends BaseController<Entity> {

    @ITIApi
    @ApiOperation("新增")
    @PostMapping
    default ApiResult<?> save(@RequestBody @Validated(SaveGroup.class) SaveDTO dto) {
        Entity entity = BeanUtils.convert(dto, getEntityClass());
        ApiResult<?> result = processBeforeSave(entity, dto);
        if (result != null) {
            return processResult(result, getCallerMethod());
        }
        getBaseService().save(entity);
        return processResult(ApiResult.ok(entity), getCallerMethod());
    }

    @ITIApi
    @ApiOperation("批量新增")
    @PostMapping("/batch")
    default ApiResult<?> saveBatch(@RequestBody @Validated(SaveGroup.class) List<SaveDTO> dtoList) {
        List<Entity> entities = BeanUtils.convertList(dtoList, getEntityClass());
        ApiResult<?> result = processBeforeSave(entities, dtoList);
        if (result != null) {
            return processResult(result, getCallerMethod());
        }
        getBaseService().saveBatch(entities);
        return processResult(ApiResult.ok(entities), getCallerMethod());
    }

    /**
     * 新增前置处理器
     * <pre>
     *     子类可重写此方法执行前置逻辑
     * </pre>
     *
     * @param entity 经过属性赋值生成的实体
     */
    default ApiResult<?> processBeforeSave(Entity entity, SaveDTO dto) {
        return null;
    }

    /**
     * 批量新增前置处理器
     * <pre>
     *     子类可重写此方法执行前置逻辑
     * </pre>
     *
     * @param entities 经过属性赋值生成的实体列表
     */
    default ApiResult<?> processBeforeSave(List<Entity> entities, List<SaveDTO> dtoList) {
        return null;
    }
}
