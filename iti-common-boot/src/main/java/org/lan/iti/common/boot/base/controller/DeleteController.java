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


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.lan.iti.common.core.enums.ErrorLevelEnum;
import org.lan.iti.common.core.enums.ITIExceptionEnum;
import org.lan.iti.common.core.exception.ServiceException;
import org.lan.iti.common.core.interfaces.validator.DeleteGroup;
import org.lan.iti.common.core.util.BeanUtils;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.scanner.annotation.ITIApi;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 删除 控制器接口
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public interface DeleteController<Entity extends Serializable, DeleteDTO extends Serializable> extends BaseController<Entity> {

    @ITIApi
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", dataType = "string", paramType = "query")
    })
    @DeleteMapping("{id}")
    default ApiResult<?> deleteById(@PathVariable("id") String id) {
        return processResult(ApiResult.ok(getBaseService().removeById(id)), getCallerMethod());
    }

    @ITIApi
    @ApiOperation("批量删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "主键列表", dataType = "string", paramType = "query")
    })
    @DeleteMapping("batch")
    default ApiResult<?> deleteByIdBatch(@NotEmpty @RequestParam("ids") List<String> ids) {
        return processResult(ApiResult.ok(getBaseService().removeByIds(ids)), getCallerMethod());
    }

    @ITIApi
    @ApiOperation("条件删除")
    @DeleteMapping("by")
    default ApiResult<?> deleteBy(@Validated(DeleteGroup.class) DeleteDTO dto) {
        Entity entity = BeanUtils.convert(dto, getEntityClass());
        if (entity == null) {
            throw new ServiceException(ITIExceptionEnum.BIZ_DELETE_ERROR.getCode(), ErrorLevelEnum.PRIMARY.getValue(), "条件为空,无法按条件删除");
        }
        return processResult(ApiResult.ok(getBaseService().remove(Wrappers.update(entity))), getCallerMethod());
    }
}
