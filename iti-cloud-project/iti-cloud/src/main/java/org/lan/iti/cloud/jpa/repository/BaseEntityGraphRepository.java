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

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * 框架基本Repository封装,基于 EntityGraphJpaRepository
 * 类似{@link BaseRepository}, 具有动态选择EntityGraph的能力
 * <p>
 * {@code @EnableJpaRepositories(repositoryFactoryBeanClass = ITIEntityGraphJpaRepositoryFactoryBean.class)}
 * {@link ITIEntityGraphJpaRepositoryFactoryBean}
 *
 * @author NorthLan
 * @date 2020-09-16
 * @url https://noahlan.com
 */
@NoRepositoryBean
public interface BaseEntityGraphRepository<T, ID extends Serializable> extends
        EntityGraphJpaRepository<T, ID>, BaseExecutor<T>, EntityGraphQuerydslPredicateExecutor<T> {
}
