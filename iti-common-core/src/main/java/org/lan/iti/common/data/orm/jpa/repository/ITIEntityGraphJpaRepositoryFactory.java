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

package org.lan.iti.common.data.orm.jpa.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 自定义EntityGraphJpaRepository工厂,基于默认EntityGraphJpaRepository工厂添加自定义片段(fragment)
 *
 * @author NorthLan
 * @date 2020-09-16
 * @url https://noahlan.com
 */
public class ITIEntityGraphJpaRepositoryFactory extends EntityGraphJpaRepositoryFactory {
    private final EntityManager entityManager;
    private final JPAQueryFactory factory;

    private EntityPathResolver entityPathResolver;

    /**
     * Creates a new {@link EntityGraphJpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public ITIEntityGraphJpaRepositoryFactory(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager);
        this.entityManager = entityManager;
        this.factory = jpaQueryFactory;
        this.entityPathResolver = SimpleEntityPathResolver.INSTANCE;
    }

    @Override
    public void setEntityPathResolver(@NonNull EntityPathResolver entityPathResolver) {
        super.setEntityPathResolver(entityPathResolver);
        this.entityPathResolver = entityPathResolver;
    }

    @SuppressWarnings("DuplicatedCode")
    @NonNull
    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(@NonNull RepositoryMetadata metadata) {
        RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);

        if (BaseExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            if (metadata.isReactiveRepository()) {
                throw new InvalidDataAccessApiUsageException(
                        "Cannot combine Querydsl and reactive repository support in a single interface");
            }
            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

            Object baseFragment = getTargetRepositoryViaReflection(BaseJpaExecutorImpl.class, entityInformation,
                    entityManager, entityPathResolver, factory);
            fragments = fragments.append(RepositoryFragment.implemented(baseFragment));
        }
        return fragments;
    }
}
