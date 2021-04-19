/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.ddd.jpa;

import cn.hutool.core.util.StrUtil;
import org.lan.iti.cloud.jpa.repository.BaseEntityGraphRepository;
import org.lan.iti.common.core.IBaseTranslator;
import org.lan.iti.common.ddd.IDomainRepository;
import org.lan.iti.common.ddd.model.IEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 领域仓储基本方法实现
 *
 * @author NorthLan
 * @date 2021-04-14
 * @url https://noahlan.com
 */
public abstract class BaseJpaDomainRepositoryImpl<Entity extends IEntity, Po, Jpa extends BaseEntityGraphRepository<Po, String>>
        implements IDomainRepository<Entity> {
    @Autowired
    protected Jpa jpaRepository;

    private final IBaseTranslator<Entity, Po> translator;

    protected BaseJpaDomainRepositoryImpl(IBaseTranslator<Entity, Po> translator) {
        this.translator = translator;
    }

    @SuppressWarnings("unchecked")
    protected <T extends IBaseTranslator<Entity, Po>> T getTranslator() {
        return (T) translator;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Optional<Entity> getById(String id) {
        Po po = jpaRepository.findById(id).orElse(null);
        Entity result = po == null ? null : translator.toSource(po);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<Entity> getAllById(Iterable<String> ids) {
        List<Entity> result = new ArrayList<>();
        for (Po po : jpaRepository.findAllById(ids)) {
            result.add(translator.toSource(po));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void save(Entity entity) {
        Po po = StrUtil.isNotBlank(entity.getId()) ?
                jpaRepository.findById(entity.getId()).orElse(null) :
                null;
        if (po != null) {
            // 将entity非空值复制过来
            translator.copyPropertiesNonNullTarget(entity, po);
        } else {
            po = translator.toTarget(entity);
        }
        jpaRepository.save(po);
        translator.copyPropertiesNonNullSource(po, entity);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(Entity entity) {
        removeById(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean exists(String id) {
        return jpaRepository.existsById(id);
    }
}
