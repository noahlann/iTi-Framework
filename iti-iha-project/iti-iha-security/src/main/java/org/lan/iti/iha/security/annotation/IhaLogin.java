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

package org.lan.iti.iha.security.annotation;

import java.lang.annotation.*;

/**
 * 登录认证，登录后才能进入该方法
 * <p>
 * （默认开）
 * </p>
 * 可标注在方法、类上（效果等同于使用在该类的所有方法，方法上优先级较高）
 *
 * @author NorthLan
 * @date 2021/7/27
 * @url https://blog.noahlan.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface IhaLogin {

    /**
     * 多账号体系下的账号类型
     */
    String type() default "";

    /**
     * 是否忽略
     */
    boolean ignore() default false;
}
