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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import java.util.Map;

/**
 * 查询 控制器接口
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public interface QueryController<Entity extends Serializable, QueryDTO> extends BaseController<Entity> {
    /**
     * 关键字 _key
     */
    String KEY_KEYWORD = "keyword";

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

    /**
     * 列表查询
     * <pre>
     *     子类可通过 重写 {@link #wrapperList(Object, Map, Serializable)} 方法来自定义查询条件包装器
     *     若此方法返回 {@code null},则执行原有查询逻辑：dto封装为entity(AND)进行查询
     * </pre>
     *
     * @param dto   查询条件 数据传输对象
     * @param extra 额外条件
     */
    @ITIApi
    @ApiOperation("列表查询")
    @GetMapping("list")
    default ApiResult<?> getList(@Validated(QueryGroup.class) QueryDTO dto, @RequestParam Map<String, String> extra) {
        Entity entity = BeanUtils.convert(dto, getEntityClass());

        // 子类重写接管方法逻辑(非null返回值)
        ApiResult<?> result = processList(dto, extra, entity);
        if (result != null) {
            return processResult(result, getCallerMethod());
        }

        // 子类重写接管条件生成逻辑
        Wrapper<Entity> condition = wrapperList(dto, extra, entity);
        if (condition != null) {
            return processResult(ApiResult.ok(getBaseService().list(condition)), getCallerMethod());
        }
        return processResult(ApiResult.ok(getBaseService().list(Wrappers.query(entity))), getCallerMethod());
    }

    /**
     * 分页查询
     * <pre>
     *     {@link #getList(Object, Map)}
     * </pre>
     *
     * @param dto   查询条件 数据传输对象
     * @param extra 额外条件
     */
    @ITIApi
    @ApiOperation("分页查询")
    @GetMapping("page")
    default ApiResult<?> getPage(@Validated(QueryGroup.class) QueryDTO dto, PageParameter pageParameter, @RequestParam Map<String, String> extra) {
        Entity entity = BeanUtils.convert(dto, getEntityClass());
        IPage<Entity> page = PageUtils.build(pageParameter);

        // 子类重写接管方法逻辑(非null返回值)
        ApiResult<?> result = processPage(page, dto, extra, entity);
        if (result != null) {
            return processResult(result, getCallerMethod());
        }

        // 子类重写接管条件生成逻辑
        Wrapper<Entity> condition = wrapperPage(page, dto, extra, entity);
        if (condition != null) {
            return processResult(ApiResult.ok(getBaseService().page(PageUtils.build(pageParameter), condition)), getCallerMethod());
        }
        return processResult(ApiResult.ok(getBaseService().page(PageUtils.build(pageParameter), Wrappers.query(entity))), getCallerMethod());
    }

    default ApiResult<?> processList(QueryDTO dto, Map<String, String> extra, Entity entity) {
        return null;
    }

    default ApiResult<?> processPage(IPage<Entity> page, QueryDTO dto, Map<String, String> extra, Entity entity) {
        return null;
    }

    /**
     * 构造查询条件
     *
     * @param dto    查询条件 数据传输对象
     * @param extra  额外条件
     * @param entity dto转换的entity对象
     * @return QueryWrapper，若为null则使用entity(AND)作为条件
     */
    default Wrapper<Entity> wrapperList(QueryDTO dto, Map<String, String> extra, Entity entity) {
        return null;
    }

    /**
     * 构造查询条件
     *
     * @param page   分页对象
     * @param dto    查询条件 数据传输对象
     * @param extra  额外条件
     * @param entity dto转换的entity对象
     * @return QueryWrapper，若为null则使用entity(AND)作为条件
     */
    default Wrapper<Entity> wrapperPage(IPage<Entity> page, QueryDTO dto, Map<String, String> extra, Entity entity) {
        return null;
    }
}
