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

package org.lan.iti.cloud.codegen.creator;

import java.lang.annotation.*;

/**
 * 领域对象为限界上下文中受保护对象，绝对不应该将其暴露到外面。
 * 因此，在创建一个新的领域对象时，需要一种机制将所需数据传递到模型中。
 * 在类上添加@GenCreator注解即可，使用Lombok进行封装生成
 * <pre>
 *     运行原理
 *     1. 自动读取当前类的 setter 方法
 *     2. 筛选 public/protected/package 访问级别的 setter 方法，将其作为属性添加到 BaseXXXCreator 类中
 *     3. 建 accept 方法，读取 BaseXXXXCreator 的属性，并通过 setter 方法写回业务数据
 * 对于不需要添加到 Creator 的 setter 方法，可以使用 @GenCreatorIgnore 忽略该方法
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Inherited
@Documented
public @interface GenCreator {

    /**
     * 是否生成普通类
     * <p>值为true表示 生成 abstract 修饰的 BaseXXXCreator,继承使用</p>
     * <p>若无特殊需求,则置为true,可直接生成非 abstract 的 XXXCreator</p>
     */
    boolean genClass() default true;

    /**
     * 父对象名称
     */
    String parent() default "";
}
