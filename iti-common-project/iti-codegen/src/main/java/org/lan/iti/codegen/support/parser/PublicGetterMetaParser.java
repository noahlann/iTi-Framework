package org.lan.iti.codegen.support.parser;

import com.squareup.javapoet.TypeName;
import org.lan.iti.codegen.annotation.Description;
import org.lan.iti.codegen.support.AccessLevel;
import org.lan.iti.codegen.support.meta.AbstractModelGetterMeta;
import org.lan.iti.codegen.util.MethodUtils;
import org.lan.iti.codegen.util.TypeUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Getter方法元数据解析器基类
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public abstract class PublicGetterMetaParser<GetterMeta extends AbstractModelGetterMeta> implements ModelMethodMetaParser<GetterMeta> {

    @Override
    public final List<GetterMeta> parse(TypeElement typeElement) {
        return ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
                .filter(field -> !field.getModifiers().contains(Modifier.STATIC))
                .filter(MethodUtils::isGetter)
                .map(method -> parseFromElement(method, method.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * 获取GetterMeta
     *
     * @param executableElement ExecutableElement
     * @return ModelMeta
     */
    protected abstract GetterMeta metaFor(ExecutableElement executableElement);

    /**
     * 从element解析
     *
     * @param executableElement element
     * @param modifiers         modifiers
     * @return GetterMeta
     */
    private GetterMeta parseFromElement(ExecutableElement executableElement, Set<Modifier> modifiers) {
        GetterMeta getterMeta = metaFor(executableElement);

        String name = TypeUtils.getNameFromGetter(executableElement.getSimpleName().toString());
        getterMeta.setName(name);
        getterMeta.setType(TypeName.get(executableElement.getReturnType()));
        getterMeta.setAccessLevel(AccessLevel.getFromModifiers(modifiers));

        // TODO GenDtoIgnore
//        GenDtoIgnore voIgnore = executableElement.getAnnotation(GenDtoIgnore.class);
//        if (voIgnore != null) {
//            getterMeta.setIgnore(true);
//        }

        Description description = executableElement.getAnnotation(Description.class);
        if (description != null) {
            getterMeta.setDescription(description.value());
        }
        return getterMeta;
    }
}
