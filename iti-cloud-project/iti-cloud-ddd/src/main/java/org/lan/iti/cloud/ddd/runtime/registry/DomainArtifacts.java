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

import lombok.*;
import org.lan.iti.common.ddd.ext.IDomainExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对外输出的领域物件，即核心的领域抽象.
 * <p>
 * <p>方便上层集成，例如：构建配置中心，业务可视化平台等.</p>
 * <p>业务抽象的可视化，在构建业务系统时非常重要：平台能力可以透出，需求传递高效.</p>
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainArtifacts {
    private static final DomainArtifacts INSTANCE = new DomainArtifacts();

    @Getter
    private List<Domain> domains;

    @Getter
    private List<Specification> specifications;

    /**
     * key is activityCode
     */
    @Getter
    private Map<String, List<Step>> steps;

    @Getter
    private List<Extension> extensions;

    public static DomainArtifacts getInstance() {
        return INSTANCE;
    }

    synchronized void export() {
        // domains
        this.domains = new ArrayList<>(InternalIndexer.DOMAIN_META_MAP.size());
        this.domains.addAll(InternalIndexer.DOMAIN_META_MAP.values()
                .stream().map(it -> new Domain(it.getCode(), it.getName())).collect(Collectors.toList()));

        // steps
        this.steps = new HashMap<>();
        for (Map.Entry<String, Map<String, StepMeta>> entry : InternalIndexer.DOMAIN_STEP_META_MAP.entrySet()) {
            final String activity = entry.getKey();
            this.steps.put(activity, new ArrayList<>());
            for (StepMeta stepMeta : entry.getValue().values()) {
                this.steps.get(activity).add(
                        new Step(activity, stepMeta.getCode(), stepMeta.getName(), stepMeta.getTags()));
            }
        }

        // specifications
        this.specifications = new ArrayList<>(InternalIndexer.SPECIFICATION_METAS.size());
        specifications.addAll(InternalIndexer.SPECIFICATION_METAS
                .stream().map(it -> new Specification(it.getName(), it.getTags())).collect(Collectors.toList()));

        // extensions
        this.extensions = new ArrayList<>();
        // parse indexer pattern extensions
        for (Map.Entry<Class<? extends IDomainExtension>, List<PatternMeta>> entry : InternalIndexer.SORTED_PATTERN_MAP.entrySet()) {
            final Extension extension = new Extension(entry.getKey());
            for (PatternMeta patternMeta : entry.getValue()) {
                extension.getPatterns().add(new Pattern(patternMeta.getCode(), patternMeta.getName()));
            }
            for (PartnerMeta partnerMeta : InternalIndexer.PARTNER_META_MAP.values()) {
                if (partnerMeta.getExtension(extension.ext) != null) {
                    // 该前台实现了该扩展点
                    extension.getPartners().add(new Partner(partnerMeta.getCode(), partnerMeta.getName()));
                }
            }

            this.extensions.add(extension);
        }
        // parse indexer partner extensions and merge with pattern extensions
        for (PartnerMeta partnerMeta : InternalIndexer.PARTNER_META_MAP.values()) {
            for (Class<? extends IDomainExtension> ext : partnerMeta.getExtensionMetaMap().keySet()) {
                Extension extensionsOfPattern = null;
                for (Extension extension : this.extensions) {
                    if (extension.ext == ext) {
                        extensionsOfPattern = extension;
                        break;
                    }
                }

                if (extensionsOfPattern == null) {
                    // this extension is implemented only by Partner
                    final Extension extension = new Extension(ext);
                    extension.getPartners().add(new Partner(partnerMeta.getCode(), partnerMeta.getName()));
                    this.extensions.add(extension);
                } else {
                    // this extension is implemented in both Partner and Pattern
                    // do the merge
                    final Partner partner = new Partner(partnerMeta.getCode(), partnerMeta.getName());
                    if (!extensionsOfPattern.getPartners().contains(partner)) {
                        extensionsOfPattern.getPartners().add(partner);
                    }
                }
            }
        }
    }

    /**
     * 领域.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Domain {
        private final String code;
        private final String name;
    }

    /**
     * 业务约束规则.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Specification {
        private final String name;
        private final String[] tags;
    }

    /**
     * 领域步骤.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Step {
        private final String activity;
        private final String code;
        private final String name;
        private final String[] tags;
    }

    //region 扩展点

    /**
     * 扩展点
     */
    @Getter
    public static class Extension {
        private final Class<? extends IDomainExtension> ext;
        private final List<Pattern> patterns;
        private final List<Partner> partners;

        private Extension(Class<? extends IDomainExtension> ext) {
            this.ext = ext;
            this.patterns = new ArrayList<>();
            this.partners = new ArrayList<>();
        }
    }

    /**
     * 领域模式.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class Pattern {
        private final String code;
        private final String name;
    }

    /**
     * 业务前台.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode
    public static class Partner {
        private final String code;
        private final String name;
    }
    //endregion
}
