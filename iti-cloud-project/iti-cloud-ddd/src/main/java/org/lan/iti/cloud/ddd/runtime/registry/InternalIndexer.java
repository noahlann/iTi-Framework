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

package org.lan.iti.cloud.ddd.runtime.registry;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.ddd.runtime.AbstractDomainAbility;
import org.lan.iti.common.core.exception.BootstrapException;
import org.lan.iti.common.ddd.ext.IDomainExtension;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册表索引 领域相关内部使用
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
@SuppressWarnings("rawtypes")
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public class InternalIndexer {
    /**
     * 空步骤
     */
    private static final List<StepMeta> EMPTY_STEPS = new ArrayList<>();

    /**
     * 业务规约 元数据存储
     */
    static final List<SpecificationMeta> SPECIFICATION_METAS = new ArrayList<>();

    /**
     * 领域 元数据存储
     * {code, meta}
     */
    static final Map<String, DomainMeta> DOMAIN_META_MAP = new HashMap<>();

    /**
     * 业务能力 元数据存储
     * {class of AbstractDomainAbility, meta}
     */
    static final Map<Class<? extends AbstractDomainAbility>, DomainAbilityMeta> DOMAIN_ABILITY_META_MAP = new HashMap<>();

    /**
     * 业务步骤 元数据存储
     * {activityCode, {stepCode, meta}}
     */
    static final Map<String, Map<String, StepMeta>> DOMAIN_STEP_META_MAP = new HashMap<>();

    /**
     * 扩展点 Pattern 元数据存储
     */
    static final Map<String, PatternMeta> PATTERN_META_MAP = new HashMap<>();

    /**
     * 扩展点 Pattern 元数据存储
     * <pre>
     *     已排序
     * </pre>
     */
    static final Map<Class<? extends IDomainExtension>, List<PatternMeta>> SORTED_PATTERN_MAP = new HashMap<>();

    /**
     * 扩展点 Partner 元数据存储
     */
    static final Map<String, PartnerMeta> PARTNER_META_MAP = new ConcurrentHashMap<>();

    /**
     * 已准备好的扩展点 Partner 元数据
     */
    static PartnerMeta partnerMetaPrepared = null;

    /**
     * 扩展点 Policy 元数据存储
     */
    static final Map<Class<? extends IDomainExtension>, PolicyMeta> POLICY_META_MAP = new HashMap<>();

    //region Finder

    /**
     * 根据业务能力类找到一个业务能力实例
     *
     * @param clazz 业务能力类
     * @param <T>   具体业务能力类型泛型
     * @return 业务能力实例, 可null
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractDomainAbility> T findDomainAbility(@NotNull Class<? extends T> clazz) {
        DomainAbilityMeta domainAbilityMeta = DOMAIN_ABILITY_META_MAP.get(clazz);
        if (domainAbilityMeta == null) {
            // 研发忘记使用注解DomainAbility了，线上bug
            // 但如果没有关闭架构守护神ArchitectureEnforcer，则不可能出现该bug
            log.error("{} forgot to apply @DomainAbility, ArchitectureEnforcer turned off? indexed:{}",
                    clazz.getCanonicalName(), DOMAIN_ABILITY_META_MAP.keySet());
            return null;
        }
        return (T) domainAbilityMeta.getDomainAbilityBean();
    }

    /**
     * 给定一个领域能力，找到它定义的扩展点接口, internal usage only.
     *
     * @param clazz 业务能力类
     */
    public static Class<? extends IDomainExtension> getDomainAbilityExtDeclaration(@NotNull Class<? extends AbstractDomainAbility> clazz) {
        DomainAbilityMeta domainAbilityMeta = DOMAIN_ABILITY_META_MAP.get(clazz);
        if (domainAbilityMeta == null) {
            // 研发忘记使用注解DomainAbility了，线上bug
            // 但如果没有关闭架构守护神ArchitectureEnforcer，则不可能出现该bug
            log.error("{} not apply @DomainAbility, ArchitectureEnforcer turned off?", clazz.getCanonicalName());
            return null;
        }
        return domainAbilityMeta.getExtClass();
    }

    /**
     * 获取某一个扩展点的所有实现实例.
     *
     * @param extClazz  extension interface
     * @param params    参数,可以是聚合
     * @param firstStop 是否找到一个就返回
     * @return 有效的扩展点列表, empty List if not found
     */
    @NotNull
    public static List<ExtensionMeta> findEffectiveExtensions(
            @NotNull Class<? extends IDomainExtension> extClazz,
            @NotNull Object params, boolean firstStop) {
        List<ExtensionMeta> effectiveExtensions = new LinkedList<>();

        // O(1) extension locating by Policy
        PolicyMeta policyMeta = POLICY_META_MAP.get(extClazz);
        if (policyMeta != null) {
            // bingo! this extension is located by policy
            ExtensionMeta extensionByPolicy = policyMeta.getExtension(params);
            if (extensionByPolicy == null) {
                // found no extension for this model
                return effectiveExtensions;
            }

            effectiveExtensions.add(extensionByPolicy);
            return effectiveExtensions;
        }

        // Pattern优先：细粒度的扩展点
        // 否则，Pattern下的扩展点可能会被粗粒度的Partner下扩展点给盖住，无法执行
        // Partner=KA，在Partner下它实现了一个扩展点，这时候签约了一个新KA客户(xx)的逻辑稍有不同，因此实现了一个Pattern下的扩展点
        // 如果Partner优先，那么xx的扩展点实现永远不会被执行：它被Partner下的扩展点盖住了 -:(
        // 同时，Pattern是有优先级的，典型场景：一个Seller有一个Pattern，该Seller下有2个特殊的Dept，各自有自己的Pattern
        List<PatternMeta> sortedPatternMetas = SORTED_PATTERN_MAP.get(extClazz);
        if (sortedPatternMetas != null && !sortedPatternMetas.isEmpty()) {
            // 该扩展点在一些Pattern上有实现，那么通过Pattern把扩展点实例找到
            log.debug("{} found patterns:{}", extClazz.getCanonicalName(), sortedPatternMetas);

            for (PatternMeta patternMeta : sortedPatternMetas) {
                if (!patternMeta.match(params)) {
                    continue;
                }

                // 找该Pattern下实现了该扩展点接口的实例
                ExtensionMeta extensionMeta = patternMeta.getExtension(extClazz);
                if (extensionMeta != null) {
                    effectiveExtensions.add(extensionMeta);
                }

                if (firstStop && !effectiveExtensions.isEmpty()) {
                    return effectiveExtensions;
                }
            }
        }

        // 之后再找Partner
        for (PartnerMeta partnerMeta : PARTNER_META_MAP.values()) {
            if (!partnerMeta.match(params)) {
                continue;
            }

            ExtensionMeta extensionMeta = partnerMeta.getExtension(extClazz);
            if (extensionMeta != null) {
                effectiveExtensions.add(extensionMeta);
                break; // 垂直业务是互斥的，不可叠加的
            }
        }

        return effectiveExtensions;
    }

    /**
     * 根据指定条件获取匹配的领域活动步骤列表.
     *
     * @param activityCode 领域活动码，即该步骤属于哪一个领域活动
     * @param stepCodeList 活动步骤的编号{@code code}列表
     * @return 匹配的活动步骤列表, will never be null
     */
    @NotNull
    public static List<StepMeta> findDomainSteps(@NotNull String activityCode, @NotNull List<String> stepCodeList) {
        Map<String, StepMeta> childMap = DOMAIN_STEP_META_MAP.get(activityCode);
        if (CollUtil.isEmpty(childMap)) {
            log.error("found NiL activity: {}", activityCode);
            return EMPTY_STEPS;
        }

        List<StepMeta> result = new LinkedList<>();
        for (String s : stepCodeList) {
            StepMeta stepMeta = childMap.get(s);
            if (stepMeta != null) {
                result.add(stepMeta);
            }
        }
        if (result.size() != stepCodeList.size()) {
            log.warn("expected:{}, got:{} domain steps", stepCodeList.size(), result.size());
        }

        return result;
    }
    //endregion

    //region Indexer

    /**
     * 缓存 Step 元数据 索引
     */
    static void index(StepMeta stepMeta) {
        Map<String, StepMeta> childMap = DOMAIN_STEP_META_MAP.computeIfAbsent(stepMeta.getActivity(),
                it -> new HashMap<>(2));

        if (childMap.containsKey(stepMeta.getCode())) {
            throw BootstrapException.ofMessage("duplicated step code: ", stepMeta.getCode());
        }

        childMap.put(stepMeta.getCode(), stepMeta);
        log.debug("indexed {} ", stepMeta);
    }

    /**
     * 缓存 Domain 元数据 索引
     */
    static void index(DomainMeta domainMeta) {
        if (DOMAIN_META_MAP.containsKey(domainMeta.getCode())) {
            throw BootstrapException.ofMessage("duplicated domain code: ", domainMeta.getCode());
        }

        DOMAIN_META_MAP.put(domainMeta.getCode(), domainMeta);
        log.debug("Indexed {}", domainMeta);
    }

    /**
     * 缓存 业务规约 元数据 索引
     *
     * @param specificationMeta specification 元数据
     */
    static void index(SpecificationMeta specificationMeta) {
        SPECIFICATION_METAS.add(specificationMeta);
        log.debug("indexed {}", specificationMeta);
    }

    /**
     * 缓存 DomainAbility 元数据 索引
     */
    static void index(DomainAbilityMeta domainAbilityMeta) {
        if (!DOMAIN_META_MAP.containsKey(domainAbilityMeta.getDomain())) {
            throw BootstrapException.ofMessage("DomainAbility domain not found: ", domainAbilityMeta.getDomain());
        }

        if (DOMAIN_ABILITY_META_MAP.containsKey(domainAbilityMeta.getDomainAbilityClass())) {
            throw BootstrapException.ofMessage("duplicated domain ability: ", domainAbilityMeta.getDomainAbilityBean().toString());
        }

        DOMAIN_ABILITY_META_MAP.put(domainAbilityMeta.getDomainAbilityClass(), domainAbilityMeta);
        log.debug("indexed {}", domainAbilityMeta);
    }

    /**
     * 缓存 DomainService 元数据 索引
     */
    static void index(DomainServiceMeta domainServiceMeta) {
        if (!DOMAIN_META_MAP.containsKey(domainServiceMeta.getDomain())) {
            throw BootstrapException.ofMessage("DomainService domain not found: ", domainServiceMeta.getDomain());
        }
        // TODO 缓存 领域服务的 索引
        log.debug("indexed {}", domainServiceMeta);
    }

    static void index(ExtensionMeta extensionMeta) {
        if (POLICY_META_MAP.containsKey(extensionMeta.getExtClazz())) {
            // this extension clazz will use policy
            PolicyMeta policyMeta = POLICY_META_MAP.get(extensionMeta.getExtClazz());
            policyMeta.registerExtensionMeta(extensionMeta);

            log.debug("indexed {} on {}", extensionMeta, policyMeta);
            return;
        }

        if (PATTERN_META_MAP.containsKey(extensionMeta.getCode())) {
            // 基于Pattern的扩展点，因为在pattern里找到了对应的code：extension.code = pattern.code
            PatternMeta patternMeta = PATTERN_META_MAP.get(extensionMeta.getCode());
            patternMeta.registerExtensionMeta(extensionMeta);

            log.debug("indexed {} on {}", extensionMeta, patternMeta);
            return;
        }

        if (PARTNER_META_MAP.containsKey(extensionMeta.getCode())) {
            // 基于垂直业务的扩展点
            PartnerMeta partnerMeta = PARTNER_META_MAP.get(extensionMeta.getCode());
            partnerMeta.registerExtensionMeta(extensionMeta);

            log.debug("indexed {} on {}", extensionMeta, partnerMeta);
            return;
        }

        if (!IDomainExtension.DEFAULT_CODE.equals(extensionMeta.getCode())) {
            // 扩展点编码，不属于Pattern也不属于Partner，也不是默认的扩展点实现
            throw BootstrapException.ofMessage("invalid extension code: ", extensionMeta.getCode());
        }
    }

    static void index(PatternMeta patternMeta) {
        if (PATTERN_META_MAP.containsKey(patternMeta.getCode())) {
            throw BootstrapException.ofMessage("duplicated pattern code: ", patternMeta.getCode());
        }

        // pattern.code不能与partner.code冲突
        if (PARTNER_META_MAP.containsKey(patternMeta.getCode())) {
            throw BootstrapException.ofMessage("pattern: ", patternMeta.getCode(), " conflicts with partner code");
        }

        PATTERN_META_MAP.put(patternMeta.getCode(), patternMeta);
        log.debug("indexed {}", patternMeta);
    }

    static void index(PartnerMeta partnerMeta) {
        if (PARTNER_META_MAP.containsKey(partnerMeta.getCode())) {
            throw BootstrapException.ofMessage("duplicated partner code: ", partnerMeta.getCode());
        }

        if (PATTERN_META_MAP.containsKey(partnerMeta.getCode())) {
            throw BootstrapException.ofMessage("partner: ", partnerMeta.getCode(), " conflicts with pattern code");
        }

        PARTNER_META_MAP.put(partnerMeta.getCode(), partnerMeta);
        log.debug("indexed {}", partnerMeta);
    }

    static void index(PolicyMeta policyMeta) {
        if (POLICY_META_MAP.containsKey(policyMeta.getExtClazz())) {
            // 一个扩展点定义只能有一个策略实例
            throw BootstrapException.ofMessage("1 Policy decides only 1 Extension:", policyMeta.policyName(), ", ext:", policyMeta.getExtClazz().getCanonicalName());
        }
        POLICY_META_MAP.put(policyMeta.getExtClazz(), policyMeta);
        log.debug("indexed {}", policyMeta);
    }

    static void postIndexing() {
        for (PatternMeta patternMeta : PATTERN_META_MAP.values()) {
            for (Class<? extends IDomainExtension> extClazz : patternMeta.extClazzSet()) {
                if (!SORTED_PATTERN_MAP.containsKey(extClazz)) {
                    SORTED_PATTERN_MAP.put(extClazz, new ArrayList<>());
                }

                SORTED_PATTERN_MAP.get(extClazz).add(patternMeta);
            }
        }

        for (List<PatternMeta> patternMetas : SORTED_PATTERN_MAP.values()) {
            patternMetas.sort(Comparator.comparingInt(PatternMeta::getPriority));
        }

        // patternMetaMap在运行时已经没有用了
        PATTERN_META_MAP.clear();

        // 把内部注册表信息暴露，以便上层应用方便集成
        DomainArtifacts.getInstance().export();
    }

    static void prepare(PartnerMeta partnerMeta) {
        partnerMetaPrepared = partnerMeta;
    }

    static void prepare(ExtensionMeta extensionMeta) {
        if (partnerMetaPrepared == null) {
            // implicit ordering: 框架内部永远会先 prepare(partnerMeta)，再 prepare(extensionMeta)
            // 由于这个顺序不会暴露外部，这个隐含的条件还OK
            // TODO Partner的定义没有出现在Plugin Jar的场景可能也需要支持
            throw BootstrapException.ofMessage("Partner must reside in Plugin Jar with its extensions!");
        }
        partnerMetaPrepared.registerExtensionMeta(extensionMeta);
    }

    public static void commitPartner() {
        PARTNER_META_MAP.put(partnerMetaPrepared.getCode(), partnerMetaPrepared);
        log.warn("Partner({}) committed", partnerMetaPrepared.getCode());

        partnerMetaPrepared = null;

        // refresh the exported domain artifacts
        DomainArtifacts.getInstance().export();
    }
    //endregion
}
