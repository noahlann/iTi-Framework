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

package org.lan.iti.cloud.autoconfigure.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * QueryDSL 自动装配
 *
 * @author NorthLan
 * @date 2021/8/9
 * @url https://blog.noahlan.com
 */
@Configuration
public class QueryDSLAutoConfiguration {

    @ConditionalOnClass({JPAQueryFactory.class, EntityManager.class})
    public static class JpaQueryFactoryConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }

    @ConditionalOnBean(DataSource.class)
    @ConditionalOnClass({SQLQueryFactory.class, DataSource.class})
    public static class SQLQueryFactoryConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public SQLQueryFactory sqlQueryFactory(DataSource dataSource) {
            return new SQLQueryFactory(
                    new com.querydsl.sql.Configuration(SQLTemplatesProvider.getSQLTemplates(dataSource)),
                    dataSource);
        }
    }
}
