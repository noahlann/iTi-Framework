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

package org.lan.iti.common.boot.config;

import lombok.AllArgsConstructor;
import org.lan.iti.common.core.properties.ErrorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

/**
 * 异常处理器自动装配
 *
 * @author NorthLan
 * @date 2020-07-30
 * @url https://noahlan.com
 */
@Configuration
@AllArgsConstructor
public class ITIExceptionHandlerAutoConfiguration {
    private final ErrorProperties properties;

    @Bean
    @ConditionalOnClass({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler(properties);
    }

    @Bean
    @ConditionalOnClass({SQLException.class, BadSqlGrammarException.class})
    public SqlExceptionHandler sqlExceptionHandler() {
        return new SqlExceptionHandler(properties);
    }
}
