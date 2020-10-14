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

package org.lan.iti.common.doc.springfox;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * 调整swagger2的 PropertySourcedRequestMappingHandlerMapping 优先级为最低，优化接口执行速度
 *
 * @author NorthLan
 * @date 2020-03-08
 * @url https://noahlan.com
 */
public class SwaggerHandlerMappingProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if ("swagger2ControllerMapping".equals(beanName)) {
            ((AbstractHandlerMapping) bean).setOrder(Ordered.LOWEST_PRECEDENCE - 1000);
        }
        return bean;
    }
}
