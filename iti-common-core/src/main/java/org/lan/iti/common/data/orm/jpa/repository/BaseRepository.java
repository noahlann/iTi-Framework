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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 框架基本Repository封装,基于JpaRepository
 * <pre>
 *     提供了基本的CRUD
 *     通过BaseExecutor提供了动态查询,且可在接口访问EntityManager,基于JDK8可直接使用default方法自定义各种查询
 *     同时也满足Jpa原生@Query
 * </pre>
 * <p>
 * {@code @EnableJpaRepositories(repositoryFactoryBeanClass = ITIJpaRepositoryFactoryBean.class)}
 * {@link ITIJpaRepositoryFactoryBean}
 *
 * @author NorthLan
 * @date 2020-09-12
 * @url https://noahlan.com
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, BaseExecutor<T>, QuerydslPredicateExecutor<T> {
}
