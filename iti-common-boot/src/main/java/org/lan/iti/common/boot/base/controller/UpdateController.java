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
import org.lan.iti.common.core.interfaces.validator.UpdateGroup;
import org.lan.iti.common.core.util.BeanUtils;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.scanner.annotation.ITIApi;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * 更新 控制器接口
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public interface UpdateController<Entity extends Serializable, UpdateDTO> extends BaseController<Entity> {

    @ApiOperation("更新")
    @PutMapping
    @ITIApi
    default ApiResult<?> updateById(@RequestBody @Validated(UpdateGroup.class) UpdateDTO dto) {
        Entity entity = BeanUtils.convert(dto, getEntityClass());
        ApiResult<?> result = processBeforeUpdate(entity, dto);
        if (result != null) {
            return processResult(result, getCallerMethod());
        }
        getBaseService().updateById(entity);
        return processResult(ApiResult.ok(entity), getCallerMethod());
    }

    @ApiOperation("批量更新")
    @PutMapping("/batch")
    @ITIApi
    default ApiResult<?> updateByIdBatch(@RequestBody @Validated(UpdateGroup.class) List<UpdateDTO> dtoList) {
        List<Entity> entities = BeanUtils.convertList(dtoList, getEntityClass());
        ApiResult<?> result = processBeforeUpdate(entities, dtoList);
        if (result != null) {
            return processResult(result, getCallerMethod());
        }
        getBaseService().updateBatchById(entities);
        return processResult(ApiResult.ok(entities), getCallerMethod());
    }

    /**
     * 更新前置处理
     *
     * @param entity    通过dto生成的Entity
     * @param updateDTO 更新DTO
     */
    default ApiResult<?> processBeforeUpdate(Entity entity, UpdateDTO updateDTO) {
        return null;
    }

    /**
     * 批量更新前置处理
     *
     * @param entity    通过dto生成的Entity
     * @param updateDTO 更新DTO
     */
    default ApiResult<?> processBeforeUpdate(List<Entity> entity, List<UpdateDTO> updateDTO) {
        return null;
    }
}
