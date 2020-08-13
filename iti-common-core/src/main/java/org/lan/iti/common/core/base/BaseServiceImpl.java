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

package org.lan.iti.common.core.base;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lan.iti.common.core.util.AopUtils;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * 通用ServiceImpl
 *
 * @author NorthLan
 * @date 2020-03-20
 * @url https://noahlan.com
 * @deprecated 下一版本将做重大更新 {@link BaseService}
 */
@Deprecated
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
    @Override
    public Boolean saveOrUpdateBatch(Collection<T> entityList, String mappedName, int batchSize) {
        Assert.notNull(entityList, "entityList must not be empty");
        Class<?> clazz = currentModelClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(tableInfo.getSqlStatement(mappedName), entity));
    }

    @Override
    public <K extends BaseService<T>> K currentProxy(Class<K> clazz) {
        return AopUtils.currentServiceProxy(clazz);
    }

    @Override
    public BaseService<T> baseProxy() {
        return AopUtils.currentServiceProxy(null);
    }
}
