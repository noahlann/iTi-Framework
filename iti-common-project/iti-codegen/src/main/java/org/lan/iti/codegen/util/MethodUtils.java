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

package org.lan.iti.codegen.util;

import cn.hutool.core.collection.CollUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.codegen.annotation.CreateMethod;
import org.lan.iti.codegen.annotation.QueryMethod;
import org.lan.iti.codegen.annotation.UpdateMethod;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 方法工具类
 *
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
@UtilityClass
public class MethodUtils {
    public final String PREFIX_CREATE_METHOD = "create";
    public final String PREFIX_UPDATE_METHOD = "update";
    public final String METHOD_NAME_GET_BY_ID = "getById";

    /**
     * 是否为 创建方法
     *
     * @param element 方法
     * @return true 是 false 否
     */
    public boolean isCreateMethod(ExecutableElement element) {
        if (hasAnnotation(element, CreateMethod.class)) {
            return true;
        }
        String methodName = element.getSimpleName().toString();
        return methodName.startsWith(PREFIX_CREATE_METHOD);
    }

    /**
     * 是否为 更新方法
     *
     * @param element 方法
     * @return true 是 false 否
     */
    public boolean isUpdateMethod(ExecutableElement element) {
        if (hasAnnotation(element, UpdateMethod.class)) {
            return true;
        }
        String methodName = element.getSimpleName().toString();
        return methodName.startsWith(PREFIX_UPDATE_METHOD);
    }

    /**
     * 是否为 查询方法
     *
     * @param element 方法
     * @return true 是 false 否
     */
    public boolean isQueryMethod(ExecutableElement element) {
        if (hasAnnotation(element, QueryMethod.class)) {
            return true;
        }
        String methodName = element.getSimpleName().toString();
        return methodName.startsWith(PREFIX_UPDATE_METHOD);
    }

    /**
     * 是否为 GetById 方法
     *
     * @param element 方法
     * @return true 是 false 否
     */
    public boolean isGetByIdMethod(ExecutableElement element) {
        return isOptionalMethod(element)
                && element.getParameters().size() == 1
                && METHOD_NAME_GET_BY_ID.equalsIgnoreCase(element.getSimpleName().toString());
    }

    /**
     * 返回值类型是否为 Optional
     *
     * @param element 方法
     * @return true 是 false 否
     */
    public static boolean isOptionalMethod(ExecutableElement element) {
        return element.getReturnType().toString().startsWith(Optional.class.getName());
    }

    /**
     * 返回值类型是否为 List
     *
     * @param element 方法
     * @return true 是 false 否
     */
    public static boolean isListMethod(ExecutableElement element) {
        return element.getReturnType().toString().startsWith(List.class.getName());
    }

//    /**
//     * 返回值类型是否为 Page
//     *
//     * @param element 方法
//     * @return true 是 false 否
//     */
//    public static boolean isPageMethod(ExecutableElement element) {
//        return element.getReturnType().toString().startsWith(Page.class.getName());
//    }

    /**
     * 是否为 简单setter
     *
     * @param element 方法
     * @return true 是 false 否
     */
    public static boolean isSetter(ExecutableElement element) {
        String name = element.getSimpleName().toString();
        List<? extends VariableElement> parameters = element.getParameters();
        return name.startsWith("set") && parameters.size() == 1;
    }

    /**
     * 是否为 简单getter
     * 包括 isXXX
     *
     * @param element 方法
     * @return true 是 false 否
     */
    public static boolean isGetter(ExecutableElement element) {
        String s = element.getSimpleName().toString();
        TypeMirror typeMirror = element.getReturnType();
        boolean is = s.startsWith("is")
                && typeMirror.getKind() == TypeKind.BOOLEAN;
        boolean getter = s.startsWith("get")
                && typeMirror.getKind() != TypeKind.VOID
                && !element.getModifiers().contains(Modifier.STATIC);
        return is || getter;
    }

    /**
     * 将方法参数 joinToString
     * 以逗号分隔
     * 例：
     * <pre>
     *     test(String test, String a)
     *     createParamListStr(test, "nice") => "nice, test, a"
     * </pre>
     *
     * @param element     方法
     * @param beforeParam 前置参数列表（方法之外）
     * @return 以逗号分隔的方法参数
     */
    public static String createParamListStr(ExecutableElement element, String... beforeParam) {
        List<String> params = element.getParameters().stream()
                .map(it -> it.getSimpleName().toString())
                .collect(Collectors.toList());
        return createParamListStr(params, beforeParam);
    }

    /**
     * 将多个参数组合，以逗号分隔
     *
     * @param params      参数列表
     * @param beforeParam 前置参数列表
     * @return 以逗号分隔的参数
     */
    public static String createParamListStr(List<String> params, String... beforeParam) {
        List<String> tempParams = new ArrayList<>(params);
        // 实际是顺序加入
        for (int i = beforeParam.length - 1; i >= 0; i--) {
            tempParams.add(0, beforeParam[i]);
        }
        return String.join(", ", tempParams);
    }

    /**
     * 获取方法唯一键
     * 以 方法名-参数 为返回值
     *
     * @param element 方法
     * @return 方法名-参数 的组合
     */
    public static String getMethodKey(ExecutableElement element) {
        StringBuilder stringBuilder = new StringBuilder(element.getSimpleName().toString());
        if (!CollUtil.isEmpty(element.getParameters())) {
            stringBuilder.append(element.getParameters().stream()
                    .map(it -> it.asType().toString())
                    .collect(Collectors.joining("-")));
        }
        return stringBuilder.toString();

    }

    // region Helper

    /**
     * 方法上是否具有指定注解
     *
     * @param element    方法
     * @param annotation 注解类型
     * @param <A>        泛型
     * @return true 是 false 否
     */
    private <A extends Annotation> boolean hasAnnotation(ExecutableElement element, Class<A> annotation) {
        return element.getAnnotation(annotation) != null;
    }
    // endregion
}
