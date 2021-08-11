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

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import javax.persistence.EntityManager;

/**
 * 基于 JPA+QueryDSL 扩展基础片段 one of fragments | {@link QuerydslPredicateExecutor}
 *
 * @author NorthLan
 * @date 2020-09-12
 * @url https://noahlan.com
 */
public interface BaseExecutor<T> {
    /**
     * 提供 Repository 返回 EntityManager 的功能
     *
     * @return Spring环境中的EntityManager
     */
    EntityManager getEntityManager();

    /**
     * 提供 Repository 返回 JpaEntityInformation 的功能
     *
     * @return 该Repository定义的Entity信息
     */
    JpaEntityInformation<T, ?> getEntityInformation();

    /**
     * 提供 Repository 返回 EntityPath 的功能
     *
     * @return 该Entity的QueryDSL-Path信息
     * @see EntityPath
     */
    EntityPath<T> getEntityPath();

    /**
     * 提供 Repository 返回 JPAQueryFactory 的功能
     *
     * @return JPA环境中的 QueryFactory
     */
    JPAQueryFactory getJPAQueryFactory();

    /**
     * 提供 Repository 返回 SQLQueryFactory 的功能
     *
     * @return 普通环境中的 QueryFactory
     */
    SQLQueryFactory getSQLQueryFactory();

    /**
     * 提供 Repository 直接返回 QueryDSL 的功能
     *
     * @return 该Entity的QueryDSL
     */
    Querydsl getQueryDSL();
}
