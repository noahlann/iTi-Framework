/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.cloud.security.annotation;

import java.lang.annotation.*;

/**
 * 服务调用鉴权注解
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {
    /**
     * 将此注解标定的接口仅当作内部接口使用
     *
     * @return false, true
     */
    boolean value() default true;

    /**
     * 需要特殊判空的字段(预留)
     */
    String[] field() default {};
}
