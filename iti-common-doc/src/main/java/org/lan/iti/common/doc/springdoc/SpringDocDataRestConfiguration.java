///*
// *
// *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.common.doc.springdoc;
//
//import com.querydsl.core.types.Path;
//import com.querydsl.core.types.Predicate;
//import org.springdoc.core.SpringDocUtils;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
//
//import java.util.Optional;
//
//import static org.springdoc.core.Constants.SPRINGDOC_ENABLED;
//
///**
// * The type Spring doc data rest configuration.
// * 自定义QuerydslPredicateOperationCustomizer
// *
// * @author NorthLan
// * @date 2020-09-15
// * @url https://noahlan.com
// */
//@Configuration
//@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
//@ConditionalOnClass({org.springdoc.data.rest.SpringDocDataRestConfiguration.class, QuerydslBindingsFactory.class, Path.class})
//@AutoConfigureBefore(org.springdoc.data.rest.SpringDocDataRestConfiguration.class)
//public class SpringDocDataRestConfiguration {
//
//    /**
//     * Query dsl querydsl predicate operation customizer querydsl predicate operation customizer.
//     *
//     * @param querydslBindingsFactory the querydsl bindings factory
//     * @return the querydsl predicate operation customizer
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    @Lazy(false)
//    org.springdoc.data.rest.customisers.QuerydslPredicateOperationCustomizer queryDslQuerydslPredicateOperationCustomizer(Optional<QuerydslBindingsFactory> querydslBindingsFactory) {
//        if (querydslBindingsFactory.isPresent()) {
//            SpringDocUtils.getConfig().addRequestWrapperToIgnore(Predicate.class);
//            return new QuerydslPredicateOperationCustomizer(querydslBindingsFactory.get());
//        }
//        return null;
//    }
//}
