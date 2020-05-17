/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.core.base;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lan.iti.common.core.interfaces.IFunction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 框架通用Service
 * <p>
 * 提供通用的DB方法
 * 对应的Mapper.xml方法要自己写入
 * </p>
 *
 * @author NorthLan
 * @date 2020/02/20
 * @url https://noahlan.com
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 调用自定义XML中指定的SQL-ID的SQL
     * 批量修改插入-默认批次1000条/次
     *
     * @param entityList 实体对象集合
     * @param getter     sqlId对应的Mapper类的方法getter(Lambda)
     * @return 是否有任何更新或插入
     */
    @Transactional(rollbackFor = Exception.class)
    default <I> Boolean saveOrUpdateBatch(Collection<T> entityList, IFunction<I, ?> getter) {
        String mappedName = getter.getImplFieldName();
        if (!StringUtils.hasText(mappedName)) {
            throw new IllegalArgumentException("getter传入错误,请检查代码");
        }
        return saveOrUpdateBatch(entityList, mappedName, 1000);
    }

    /**
     * 调用自定义XML中指定的SQL-ID的SQL
     * 批量修改插入-默认批次1000条/次
     *
     * @param entityList 实体对象集合
     * @param mappedName 插入使用的sqlId
     * @return 是否有任何更新或插入
     */
    @Transactional(rollbackFor = Exception.class)
    default Boolean saveOrUpdateBatch(Collection<T> entityList, String mappedName) {
        return saveOrUpdateBatch(entityList, mappedName, 1000);
    }

    /**
     * 调用自定义XML中指定的SQL-ID的SQL
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param mappedName 插入使用的sqlId
     * @param batchSize  批量条数
     * @return 是否有任何更新或插入
     */
    @Transactional(rollbackFor = Exception.class)
    Boolean saveOrUpdateBatch(Collection<T> entityList, String mappedName, int batchSize);

    /**
     * 获取Service的代理对象(强类型)
     * <p>
     * 默认基于CGLIB实现,需要开启 [org.springframework.context.annotation.EnableAspectJAutoProxy] exposeProxy 与 proxyTargetClass
     * 若提供[clazz],在CGLIB未开启情况下,可自动通过ApplicationContext获取Bean
     *
     * @param clazz 需要获取的类
     * @param <K>   必须是BaseService的子类
     * @return 代理类
     */
    <K extends BaseService<T>> K currentProxy(Class<K> clazz);

    /**
     * 获取基础Service的代理对象(弱类型)
     *
     * @return 基础Service的代理对象(弱类型)
     */
    BaseService<T> baseProxy();
}
