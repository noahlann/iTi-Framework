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

import com.squareup.javapoet.*;
import lombok.experimental.UtilityClass;
import org.lan.iti.codegen.annotation.Description;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 类型工具类
 *
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
@UtilityClass
public class TypeUtils {

    public void bindDescription(Element element, MethodSpec.Builder methodBuilder) {
        Description description = element.getAnnotation(Description.class);
        if (description != null) {
            methodBuilder.addAnnotation(AnnotationSpec.builder(Description.class)
                    .addMember("value", "\"" + description.value() + "\"")
                    .build());
        }
    }

    public ParameterSpec createParameterSpecFromElement(VariableElement element) {
        TypeName type = TypeName.get(element.asType());
        String name = element.getSimpleName().toString();
        ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(type, name)
                .addModifiers(element.getModifiers());
        Description description = element.getAnnotation(Description.class);
        if (description != null) {
            parameterBuilder.addAnnotation(AnnotationSpec.builder(Description.class)
                    .addMember("value", "\"" + description.value() + "\"")
                    .build());
        }
        return parameterBuilder.build();
    }

    public ParameterSpec createIdParameter(ClassName idClassName) {
        return ParameterSpec.builder(idClassName, "id")
                .addAnnotation(AnnotationSpec.builder(Description.class)
                        .addMember("value", "\"唯一主键\"")
                        .build())
                .build();
    }

    public String createParamListStr(List<String> params) {
        List<String> result = new ArrayList<>(params);
        return String.join(",", result);
    }

    public String createParamListStr(ExecutableElement executableElement, String... beforeParam) {
        List<String> params = executableElement.getParameters().stream()
                .map(varElement -> varElement.getSimpleName().toString())
                .collect(Collectors.toList());
        for (int i = beforeParam.length - 1; i >= 0; i--) {
            params.add(0, beforeParam[i]);
        }
        return String.join(", ", params);
    }

    public String createParamVarStr(List<String> params) {
        return params.stream()
                .map(varElement -> "{}")
                .collect(Collectors.joining(", "));
    }

    public String getNameFromGetter(String s) {
        String r = null;
        if (s.startsWith("get")) {
            r = s.substring(3);
        } else if (s.startsWith("is")) {
            r = s.substring(2);
        } else {
            r = s;
        }
        return r.substring(0, 1).toLowerCase() + r.substring(1);
    }

    public String getNameFromSetter(String s) {
        String r = null;
        if (s.startsWith("set")) {
            r = s.substring(3);
        } else {
            r = s;
        }
        return r.substring(0, 1).toLowerCase() + r.substring(1);
    }


    public String getFieldName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * 获取当前类的ID类型
     *
     * @param typeElement TypeElement
     */
    public String getIdClassName(TypeElement typeElement) {
        TypeMirror typeMirror = typeElement.getSuperclass();
        if (typeMirror == null) {
            return null;
        }
// 暂不取泛型类型，ID类型固定为String
//        for (TypeMirror itfType : typeElement.getInterfaces()) {
//            String type = itfType.toString();
//            if (type.startsWith(IDomain.class.getName())) {
//                try {
//                    DeclaredType declaredType = ((DeclaredType) itfType);
//                    var arg = declaredType.getTypeArguments();
//                    return arg.get(0).toString();
//                } catch (Exception e) {
//                    String s = type.substring(IDomain.class.getName().length() + 1);
//                    s = s.substring(0, s.length() - 1);
//                    if (StrUtil.isNotEmpty(s)) {
//                        return s;
//                    }
//                }
//            }
//        }
        return String.class.getName();
    }

    private String getIdClassFrom(String parentCls) {
        return parentCls.substring(parentCls.indexOf("<") + 1, parentCls.indexOf(">"));
    }

    public String getPackageFromFullClassName(String fullClassName) {
        return fullClassName.substring(0, fullClassName.lastIndexOf('.'));
    }

    public String getClassNameFromFullClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }

    public String getFieldNameFromType(String type) {
        return type.substring(0, 1).toLowerCase() + type.substring(1);
    }

    public Class<?> getClass(TypeElement typeElement) {
        try {
            return Class.forName(getClassName(typeElement));
        } catch (Exception ignore) {
        }
        return null;
    }

    public Class<?> getClass(TypeMirror type) {
        if (type instanceof DeclaredType) {
            if (((DeclaredType) type).asElement() instanceof TypeElement) {
                return getClass((TypeElement) ((DeclaredType) type)
                        .asElement());
            }
        }
        return null;
    }

    public String getClassName(TypeElement element) {
        Element currElement = element;
        StringBuilder result = new StringBuilder(element.getSimpleName().toString());
        while (currElement.getEnclosingElement() != null) {
            currElement = currElement.getEnclosingElement();
            if (currElement instanceof TypeElement) {
                result.insert(0, currElement.getSimpleName() + "$");
            } else if (currElement instanceof PackageElement) {
                currElement.getSimpleName();
                result.insert(0, ((PackageElement) currElement)
                        .getQualifiedName() + ".");
            }
        }
        return result.toString();
    }

    public String getClassNameSafety(Supplier<Class<?>> classGetter) {
        String clsName;
        try {
            clsName = classGetter.get().getCanonicalName();
        } catch (MirroredTypeException e) {
            clsName = e.getTypeMirror().toString();
        }
        return clsName;
    }

    public boolean isAssignableFrom(TypeMirror targetType, TypeMirror type) {
        if ((targetType instanceof DeclaredType)
                && (type instanceof DeclaredType)) {
            Element targetElement = ((DeclaredType) targetType).asElement();
            Element element = ((DeclaredType) type).asElement();
            if ((targetElement instanceof TypeElement)
                    && (element instanceof TypeElement)) {
                return isAssignableFrom((TypeElement) targetElement, (TypeElement) element);
            }
        }
        return false;
    }

    public boolean isAssignableFrom(TypeElement targetElement, TypeElement element) {
        TypeElement currentElement = element;

        //Compare at interface level
        for (TypeMirror interfaceType : element.getInterfaces()) {
            Element interfaceElement = ((DeclaredType) interfaceType)
                    .asElement();
            if (targetElement.equals(interfaceElement)) {
                return true;
            }
        }

        // Compare at class level
        while (currentElement != null) {
            if (targetElement.equals(currentElement)) {
                return true;
            }
            currentElement = getSuperClass(currentElement);
        }
        return false;
    }

    public TypeElement getSuperClass(TypeElement element) {
        TypeMirror parent = element.getSuperclass();
        if (parent instanceof DeclaredType) {
            Element elt = ((DeclaredType) parent).asElement();
            if (elt instanceof TypeElement) {
                return (TypeElement) elt;
            }
        }
        return null;
    }
}
