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

package org.lan.iti.cloud.ddd.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 领域服务注解
 * <p>领域服务用于协调 界限上下文 内的多个聚合根，将转为saga模式实现</p>
 * <p>将使用@Saga代替</p>
 *
 * @author NorthLan
 * @date 2021-02-08
 * @url https://noahlan.com
 * @deprecated 将使用@Saga代替
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Service
@Deprecated
public @interface DomainService {

    /**
     * 所属业务域
     */
    String domain() default "";
}
