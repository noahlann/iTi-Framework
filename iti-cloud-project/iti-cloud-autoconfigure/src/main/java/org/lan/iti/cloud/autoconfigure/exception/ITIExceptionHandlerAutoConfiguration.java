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

package org.lan.iti.cloud.autoconfigure.exception;

import org.axonframework.messaging.annotation.MessageHandlerInvocationException;
import org.lan.iti.cloud.axon.handler.AxonExceptionHandler;
import org.lan.iti.cloud.handler.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

/**
 * 异常处理器自动装配
 * <p>
 * 不作用在OAuth Server上
 *
 * @author NorthLan
 * @date 2020-07-30
 * @url https://noahlan.com
 */
@Configuration
public class ITIExceptionHandlerAutoConfiguration {

    /**
     * 全局异常处理器 自动配置
     */
    @Configuration
//    @ConditionalOnMissingClass("com.alibaba.csp.sentinel.Tracer")
    public static class GlobalExceptionHandlerAutoConfiguration {

        @Bean
        public GlobalExceptionHandler globalExceptionHandler() {
            return new GlobalExceptionHandler();
        }
    }

    /**
     * 参数异常处理器 自动配置
     */
    @Configuration
    @ConditionalOnClass({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public static class ArgumentsExceptionHandlerAutoConfiguration {

        @Bean
        public ArgumentsExceptionHandler argumentsExceptionHandler() {
            return new ArgumentsExceptionHandler();
        }
    }

    /**
     * SQL异常处理器 自动配置
     */
    @Configuration
    @ConditionalOnClass({SQLException.class, PersistenceException.class})
    public static class SqlExceptionHandlerAutoConfiguration {

        @Bean
        public SqlExceptionHandler sqlExceptionHandler() {
            return new SqlExceptionHandler();
        }
    }

    /**
     * SpringJdbc异常处理器 自动配置
     */
    @Configuration
    @ConditionalOnClass({BadSqlGrammarException.class})
    public static class SpringJdbcSqlExceptionHandlerAutoConfiguration {

        @Bean
        public SpringJdbcExceptionHandler springJdbcExceptionHandler() {
            return new SpringJdbcExceptionHandler();
        }
    }

    /**
     * 业务异常处理器自动配置
     */
    @Configuration
    public static class BizExceptionHandlerAutoConfiguration {

        @Bean
        public BizExceptionHandler bizExceptionHandler() {
            return new BizExceptionHandler();
        }
    }


    /**
     * Axon异常处理器 自动配置
     */
    @Configuration
    @ConditionalOnClass({MessageHandlerInvocationException.class})
    public static class AxonExceptionHandlerAutoConfiguration {

        @Bean
        public AxonExceptionHandler axonExceptionHandler() {
            return new AxonExceptionHandler();
        }
    }


//    /**
//     * 全局异常处理器自动配置
//     * <p>
//     * Sentinel存在的环境
//     */
//    @Configuration
//    @ConditionalOnClass({
//            MethodArgumentNotValidException.class,
//            ConstraintViolationException.class,
//            Tracer.class,
//            SentinelGlobalExceptionHandler.class})
//    public static class SentinelGlobalExceptionHandlerAutoConfiguration {
//
//        @Bean
//        public SentinelGlobalExceptionHandler sentinelGlobalExceptionHandler() {
//            return new SentinelGlobalExceptionHandler();
//        }
//    }


}
