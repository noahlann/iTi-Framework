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

package org.lan.iti.iha.security.processor;

import org.lan.iti.common.extension.ExtensionLoader;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 责任链处理器
 *
 * @author NorthLan
 * @date 2021/8/3
 * @url https://blog.noahlan.com
 */
public class ProcessorManager implements ProcessChain {
    /**
     * 当前类型处理器
     */
    private final List<AuthenticationProcessor> processors = new ArrayList<>();

    /**
     * 是否排过序
     */
    private boolean sorted = false;

    /**
     * 当前管理的处理器类型
     */
    private final String type;

    /**
     * 是否开启ExtensionLoader加载机制
     */
    private final boolean enableExtensionLoad;

    /**
     * 当前处理链位置
     */
    private int index = 0;

    public ProcessorManager(String type, boolean enableExtensionLoad) {
        this.type = type;
        this.enableExtensionLoad = enableExtensionLoad;
        loadExtension();
    }

    private void loadExtension() {
        if (enableExtensionLoad) {
            List<AuthenticationProcessor> loaded = ExtensionLoader.getLoader(AuthenticationProcessor.class).getList(type);
            if (loaded != null) {
                this.processors.addAll(loaded);
            }
        }
    }

    public ProcessorManager addProcessor(AuthenticationProcessor process) {
        if (process == null) {
            return this;
        }
        this.processors.add(process);
        this.sorted = false;
        return this;
    }

    public List<AuthenticationProcessor> getProcessors() {
        if (!sorted) {
            this.processors.sort(Comparator.comparingInt(AuthenticationProcessor::getOrder));
            sorted = true;
        }
        return this.processors;
    }

    public Authentication process(RequestParameter parameter) throws AuthenticationException {
        resetIndex();
        return this.process(parameter, null);
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        // 最后一个处理器已被处理，直接返回
        if (index >= getProcessors().size()) {
            return authentication;
        }
        AuthenticationProcessor processor = getProcessors().get(index++);
        if (processor.support(parameter, authentication)) {
            return processor.process(parameter, authentication, this);
        } else {
            return process(parameter, authentication);
        }
    }

    private void resetIndex() {
        index = 0;
    }
}
