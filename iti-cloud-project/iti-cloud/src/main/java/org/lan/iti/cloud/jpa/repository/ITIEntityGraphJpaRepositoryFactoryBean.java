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

package org.lan.iti.cloud.jpa.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 自定义Repository工厂Bean
 *
 * @author NorthLan
 * @date 2020-09-16
 * @url https://noahlan.com
 */
public class ITIEntityGraphJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends EntityGraphJpaRepositoryFactoryBean<T, S, ID> {
    private JPAQueryFactory jpaQueryFactory;
    private SQLQueryFactory sqlQueryFactory;

    /**
     * Creates a new {@link EntityGraphJpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public ITIEntityGraphJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Autowired
    public void setJPAQueryFactory(JPAQueryFactory factory) {
        this.jpaQueryFactory = factory;
    }

    @Autowired
    public void setSqlQueryFactory(SQLQueryFactory factory) {
        this.sqlQueryFactory = factory;
    }

    @NonNull
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(@NonNull EntityManager entityManager) {
        return new ITIEntityGraphJpaRepositoryFactory(entityManager, this.jpaQueryFactory, this.sqlQueryFactory);
    }
}
