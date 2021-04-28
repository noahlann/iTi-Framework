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

import lombok.Getter;
import lombok.ToString;
import org.lan.iti.common.core.exception.BootstrapException;
import org.lan.iti.cloud.util.AopUtils;
import org.lan.iti.cloud.ddd.annotation.Step;
import org.lan.iti.common.ddd.step.IDomainStep;

import javax.validation.constraints.NotNull;

/**
 * {@link Step}注解 元数据
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 * @deprecated 将取消步骤编排功能
 */
@SuppressWarnings("rawtypes")
@Getter
@ToString
@Deprecated
public class StepMeta implements IRegistryAware {
    private String activity;
    private String code;
    private String name;
    private String[] tags;
    private IDomainStep stepBean;

    @Override
    public void registerBean(@NotNull Object bean) {
        Step domainStep = AopUtils.getAnnotation(bean, Step.class);
        this.name = domainStep.name();
        this.tags = domainStep.tags();

        if (!(bean instanceof IDomainStep)) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName(), " MUST implement IDomainStep");
        }
        this.stepBean = (IDomainStep) bean;
        this.activity = this.stepBean.activityCode();
        this.code = this.stepBean.stepCode();
        if (this.activity == null || this.activity.trim().isEmpty()) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName(), " activityCode cannot be empty");
        }
        if (this.code == null || this.code.trim().isEmpty()) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName(), " stepCode cannot be empty");
        }
        
        InternalIndexer.index(this);
    }
}
