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

package org.lan.iti.common.data.orm.binding.binder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.base.BaseService;

import java.util.List;

/**
 * 关系绑定 抽象父类
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@Slf4j
public abstract class BaseBinder<T> {

    /**
     * 需要绑定到VO注解对象List
     */
    protected List<T> annoObjectList;

    /***
     * VO注解对象中的外键属性
     */
    protected String annoObjectForeignKey;

    /**
     * 被关联对象的Service实例
     */
    protected BaseService<T> referencedService;

    /***
     * DO对象中的主键属性名
     */
    protected String referencedEntityPrimaryKey;

    /**
     * 初始化 QueryWrapper
     */
    protected QueryWrapper<T> queryWrapper;

    /**
     * 引用的 EntityClass
     */
    protected Class<T> referencedEntityClass;

    /**
     * 绑定
     */
    public abstract void bind();
}
