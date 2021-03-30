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

package org.lan.iti.cloud.enforce;

import com.tngtech.archunit.lang.ArchRule;
import org.lan.iti.common.ddd.IDomainRepository;
import org.lan.iti.common.ddd.model.IDomain;
import org.lan.iti.common.ddd.model.IDomainService;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

/**
 * 架构守护神规则库，based on ArchUnit.
 * <p>iTi 框架的架构守护神，为架构演进保驾护航，拒绝架构腐化.</p>
 * <p>同时，也为本框架提供了静态检查机制(配合单元测试使用)，杜绝线上出现不合规范的使用.</p>
 *
 * @author NorthLan
 * @date 2021-02-07
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class ITIArchitectureEnforcer {
    public static final List<ArchRule> REQUIRED_RULES = new LinkedList<>();

    private ITIArchitectureEnforcer() {
    }

    // region 基本规约
    /**
     * Enum规约
     * 枚举类名应以Enum结尾
     */
    public static final ArchRule ENUM_RULE = classes()
            .that().areEnums()
            .should().haveSimpleNameEndingWith("Enum")
            .as("枚举类名应以Enum结尾");
    /**
     * logger 使用规约
     * 必须以 private static final 定义
     */
    public static final ArchRule LOGGER_RULE = fields()
            .that().haveRawType(Logger.class)
            .should().bePrivate()
            .andShould().beStatic()
            .andShould().beFinal()
            .because("by convention");
    // endregion

    // region 领域相关规约
    /**
     * DDD分层架构
     * 应用层: application/app 允许 用户接口层/基础设施层 引用
     * 领域层: domain 允许 应用层 引用
     * 基础设施层: infrastructure/infra 允许 应用层/用户接口层 引用
     * 用户接口层: interfaces 允许 应用层 引用
     * 共享层: spec 允许任何层 引用
     */
    public static final ArchRule OPTIONAL_DDD_LAYER_RULE = layeredArchitecture()
            .optionalLayer("Application").definedBy("..application..", "..app..")
            .optionalLayer("Domain").definedBy("..domain..")
            .optionalLayer("Infrastructure").definedBy("..infrastructure..", "..infra..")
            .optionalLayer("Interfaces").definedBy("..interfaces..")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Interfaces")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Application", "Interfaces")
            .whereLayer("Interfaces").mayNotBeAccessedByAnyLayer()
            .as("必须严格属于DDD分层架构规范,注意各层之间的引用");

    /**
     * 领域服务规约
     * 对于 domain.service 下的类
     * 1. 命名必须以 DomainService 结尾
     * 2. 必须实现接口 IDomainService
     * 3. 必须是接口
     */
    public static final ArchRule DOMAIN_SERVICE_RULE = classes()
            .that().resideInAPackage("..domain.service..")
            .should().haveSimpleNameEndingWith("DomainService")
            .andShould().beAssignableTo(IDomainService.class)
            .as("领域服务规约：\n" +
                    "1. 命名必须以 DomainService 结尾; \n" +
                    "2. 必须实现接口 IDomainService");

    /**
     * 领域仓储接口 规约
     * 对于所有 domain.facade.repository 的类
     * 1. 必须是接口
     * 2. 必须实现或继承 IDomainRepository 接口
     * 3. 不能以 @Repository 进行注解
     * 4. 必须以 xxxRepository 命名
     */
    public static final ArchRule DOMAIN_REPOSITORY_RULE = classes()
            .that().resideInAPackage("..domain.facade.repository..")
            .should().beInterfaces()
            .andShould().beAssignableTo(IDomainRepository.class)
            .andShould().notBeAnnotatedWith(Repository.class)
            .andShould().haveSimpleNameEndingWith("Repository")
            .as("领域仓储接口规约\n" +
                    "1. 必须是接口\n" +
                    "2. 必须实现或继承 IAggregateRepository 接口\n" +
                    "3. 不能以 @Repository 进行注解\n" +
                    "4. 必须以 xxxRepository 命名");

    /**
     * 聚合根规约
     * 对于所有实现了 IAggregateRoot 的类，属于聚合根
     * 1. 只能具有私有构造方法，即：不允许外部new
     */
    public static final ArchRule AGGREGATE_ROOT_RULE = classes()
            .that().implement(IDomain.class)
            .should().haveOnlyPrivateConstructors()
            .as("聚合根不允许外部new");

    /**
     * 聚合根规约
     * 对于所有实现了 IAggregateRoot 的类，属于聚合根
     * 2. 只能具有 package/private/protected 级别的 setter (包括 getXXX 与 isXXX)
     */
    public static final ArchRule AGGREGATE_ROOT_SETTER_RULE = members()
            .that().areDeclaredInClassesThat().implement(IDomain.class)
            .and().haveNameStartingWith("set").or().haveNameStartingWith("is")
            .should().bePrivate()
            .orShould().bePackagePrivate()
            .orShould().beProtected()
            .as("聚合根setter方法不允许暴露至外部");


    // endregion

    static {
        // 不允许使用System.out/System.in/System.err，printStackTrace
        REQUIRED_RULES.add(NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS);

        // 不允许使用java.util.logging
        REQUIRED_RULES.add(NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING);

        /* 基本规约 */
        // 枚举
        REQUIRED_RULES.add(ENUM_RULE);
        // logger
        REQUIRED_RULES.add(LOGGER_RULE);

        /* 领域规约 */
        // 聚合根
        REQUIRED_RULES.add(AGGREGATE_ROOT_RULE);
        REQUIRED_RULES.add(AGGREGATE_ROOT_SETTER_RULE);

        // 领域仓储
        REQUIRED_RULES.add(DOMAIN_REPOSITORY_RULE);

        // 领域服务
        REQUIRED_RULES.add(DOMAIN_SERVICE_RULE);
    }
}
