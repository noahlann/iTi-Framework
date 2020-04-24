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

package org.lan.iti.common.data.orm.binding;

import cn.hutool.core.collection.CollUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.base.BaseService;
import org.lan.iti.common.core.util.BeanUtils;
import org.lan.iti.common.data.orm.binding.annotation.BindEntity;
import org.lan.iti.common.data.orm.binding.annotation.BindEntityList;
import org.lan.iti.common.data.orm.binding.annotation.BindField;
import org.lan.iti.common.data.orm.binding.binder.BaseBinder;
import org.lan.iti.common.data.orm.binding.parser.AnnotationGroup;
import org.lan.iti.common.data.orm.binding.parser.FieldAnnotation;
import org.lan.iti.common.data.orm.binding.parser.ParserCache;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 数据绑定器
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@Slf4j
@UtilityClass
public class ITIDataBinder {

    /**
     * 自动转换和绑定单个VO中的注解关联（禁止循环调用，多个对象请调用 {@link #convertAndBind(List, Class)})）
     *
     * @param entity  实体
     * @param voClass 需要转换的VO class
     * @param <E>     实体类型
     * @param <VO>    VO类型
     * @return VO
     */
    public <E, VO> VO convertAndBind(E entity, Class<VO> voClass) {
        VO vo = BeanUtils.convert(entity, voClass);
        bind(vo);
        return vo;
    }

    /**
     * 自动转换和绑定多个VO中的注解关联
     *
     * @param entityList 需要转换的Entity list
     * @param voClass    需要转换的VO class
     * @param <E>        实体类型
     * @param <VO>       VO类型
     * @return VO
     */
    public <E, VO> List<VO> convertAndBind(List<E> entityList, Class<VO> voClass) {
        List<VO> voList = BeanUtils.convertList(entityList, voClass);
        bind(voList);
        return voList;
    }

    /**
     * 自动绑定单个VO的关联对象（禁止循环调用，多个对象请调用{@link #bind(List)} ）
     *
     * @param vo   需要注解绑定的对象
     * @param <VO> VO
     */
    public <VO> void bind(VO vo) {
        List<VO> voList = Collections.singletonList(vo);
        bind(voList);
    }

    /**
     * 自动绑定多个VO集合的关联对象
     *
     * @param voList 需要注解绑定的对象集合
     * @param <VO>   VO
     */
    public <VO> void bind(List<VO> voList) {
        if (CollUtil.isEmpty(voList)) {
            return;
        }
        Class<?> voClass = voList.get(0).getClass();
        AnnotationGroup group = ParserCache.getAnnotationGroup(voClass);
        if (group.isNotEmpty()) {
            // 根据注解绑定相关数据
            Optional.ofNullable(group.getFieldAnnotations()).ifPresent(it -> doBindingField(voList, it));
            Optional.ofNullable(group.getEntityAnnotations()).ifPresent(it -> {
                for (FieldAnnotation annotation : it) {
                    doBindingEntity(voList, annotation);
                }
            });
            Optional.ofNullable(group.getEntityListAnnotations()).ifPresent(it -> {
                for (FieldAnnotation annotation : it) {
                    doBindingEntityList(voList, annotation);
                }
            });
        }
    }

    private <VO> void doBindingField(List<VO> voList, List<FieldAnnotation> annotations) {
        // 多字段合并查询,减少SQL
        Map<String, List<FieldAnnotation>> classToListMap = new HashMap<>();
        for (FieldAnnotation annotation : annotations) {
            BindField bindField = (BindField) annotation.getAnnotation();
            String key = bindField.entity().getName() + ":" + bindField.condition();
            List<FieldAnnotation> list = classToListMap.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(annotation);
        }
        // TODO 解析条件并且执行绑定
    }

    private <VO> void doBindingEntity(List<VO> voList, FieldAnnotation annotation) {
        BindEntity bindEntity = (BindEntity) annotation.getAnnotation();
        // binder

        // TODO 解析条件并且执行绑定
    }

    /***
     * 绑定Entity
     * @param voList
     * @param fieldAnnotation
     * @param <VO>
     */
    private static <VO> void doBindingEntityList(List<VO> voList, FieldAnnotation fieldAnnotation) {
        BindEntityList bindAnnotation = (BindEntityList) fieldAnnotation.getAnnotation();
        // 构建binder
//        EntityListBinder binder = buildEntityListBinder(bindAnnotation, voList);
//        if (binder != null) {
//            binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
//            // 解析条件并且执行绑定
//            parseConditionsAndBinding(binder, bindAnnotation.condition());
//        }
    }

    /***
     * 解析条件并且执行绑定
     * @param condition
     * @param binder
     */
    private static void parseConditionsAndBinding(BaseBinder<?> binder, String condition){
        try{
//            ConditionManager.parseConditions(condition, binder);
            binder.bind();
        }
        catch (Exception e){
            log.error("解析注解条件与绑定执行异常", e);
        }
    }


    private BaseService getBaseService(Annotation annotation) {
        Class<?> entityClass = null;
        if (annotation instanceof BindField) {

        } else if (annotation instanceof BindEntity) {

        } else if (annotation instanceof BindEntityList) {

        } else {
            log.warn("非预期的注解: {}", annotation.getClass().getSimpleName());
            return null;
        }
        // todo get by entity
        return null;
    }

}
