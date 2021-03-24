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

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.EntityPathResolver;

import javax.persistence.EntityManager;

/**
 * 基于 JPA+QueryDSL 扩展基础片段实现,使用了 {@link JPAQueryFactory}
 *
 * @author NorthLan
 * @date 2020-09-12
 * @url https://noahlan.com
 * @see org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor
 */
public class BaseJpaExecutorImpl<T> implements BaseExecutor<T> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityPath<T> path;
    private final Querydsl querydsl;
    private final JPAQueryFactory factory;
    private final EntityManager entityManager;

    public BaseJpaExecutorImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager,
                               EntityPathResolver resolver, JPAQueryFactory factory) {
        this.entityInformation = entityInformation;
        this.path = resolver.createPath(entityInformation.getJavaType());
        this.entityManager = entityManager;
        this.querydsl = new Querydsl(entityManager, new PathBuilder<T>(path.getType(), path.getMetadata()));
        this.factory = factory;
    }

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    public JpaEntityInformation<T, ?> getEntityInformation() {
        return this.entityInformation;
    }

    @Override
    public EntityPath<T> getEntityPath() {
        return this.path;
    }

    @Override
    public JPAQueryFactory getJPAQueryFactory() {
        return this.factory;
    }

    @Override
    public Querydsl getQueryDSL() {
        return this.querydsl;
    }

    @Override
    public JPADeleteClause delete() {
        return factory.delete(path);
    }

    @Override
    public JPAUpdateClause update() {
        return factory.update(path);
    }

    @Override
    public JPAQuery<?> find() {
        return new JPAQuery<>(entityManager).from(path);
    }

    @Override
    public <A> JPAQuery<A> find(Expression<A> expr) {
        return find().select(expr);
    }

    @Override
    public JPAQuery<Tuple> find(Expression<?>... exprs) {
        return find().select(exprs);
    }

    @Override
    public <A> JPAQuery<A> findDistinct(Expression<A> expr) {
        return find().select(expr).distinct();
    }

    @Override
    public JPAQuery<Tuple> findDistinct(Expression<?>... exprs) {
        return find().select(exprs).distinct();
    }
}
