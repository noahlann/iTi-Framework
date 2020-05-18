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
import org.lan.iti.common.core.interfaces.validator.QueryGroup;
import org.lan.iti.common.core.util.BeanUtils;
import org.lan.iti.common.model.page.PageParameter;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.model.util.PageUtils;
import org.lan.iti.common.scanner.annotation.ITIApi;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

/**
 * 查询 控制器接口
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public interface QueryController<Entity extends Serializable, QueryDTO extends Serializable> extends BaseController<Entity> {

    @ITIApi
    @ApiOperation("通过ID查询")
    @GetMapping("{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", dataType = "string", paramType = "query")
    })
    default ApiResult<?> getById(@PathVariable String id) {
        return processResult(ApiResult.ok(getBaseService().getById(id)),
                getCallerMethod());
    }

    @ITIApi
    @ApiOperation("列表查询")
    @GetMapping("list")
    default ApiResult<?> getList(@Validated(QueryGroup.class) QueryDTO dto, @RequestParam(value = "keyword", required = false) String keyword) {
        Entity entity = BeanUtils.convert(dto, getEntityClass());
        ApiResult<?> result = processBeforeQueryListOrPage(entity, dto, keyword, null);
        if (result != null) {
            return processResult(result, getCallerMethod());
        }
        return processResult(ApiResult.ok(getBaseService().list(Wrappers.query(entity))),
                getCallerMethod());
    }

    @ITIApi
    @ApiOperation("分页查询")
    @GetMapping("page")
    default ApiResult<?> getPage(PageParameter parameter, @Validated(QueryGroup.class) QueryDTO dto, @RequestParam(value = "keyword", required = false) String keyword) {
        Entity entity = BeanUtils.convert(dto, getEntityClass());
        ApiResult<?> result = processBeforeQueryListOrPage(entity, dto, keyword, parameter);
        if (result != null) {
            return processResult(result, getCallerMethod());
        }
        return processResult(ApiResult.ok(getBaseService().page(PageUtils.build(parameter), Wrappers.query(entity))),
                getCallerMethod());
    }

    default ApiResult<?> processBeforeQueryListOrPage(Entity entity, QueryDTO dto, String keyword, PageParameter page) {
        return null;
    }
}
