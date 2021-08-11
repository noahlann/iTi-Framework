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
 * 角色认证：必须拥有指定的角色标识才能进入方法
 * <p>
 * 与 {@see IhaLogin} 一致，可标注在方法、类上，方法上优先级较高。
 * </p>
 *
 * @author NorthLan
 * @date 2021/7/27
 * @url https://blog.noahlan.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface IhaRole {
    /**
     * 需要校验的角色标识
     *
     * @return 需要校验的角色标识
     */
    String[] value() default {};

    /**
     * 验证模式：AND | OR，默认AND
     */
    IhaLogics logics() default IhaLogics.AND;

    /**
     * 账号类型
     * <p> 建议使用常量，避免因错误拼写带来的bug
     */
    String type() default "";
}
