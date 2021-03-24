package org.lan.iti.codegen.support.parser;

import com.squareup.javapoet.TypeName;
import org.lan.iti.codegen.annotation.Description;
import org.lan.iti.codegen.support.AccessLevel;
import org.lan.iti.codegen.support.meta.AbstractModelSetterMeta;
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
public abstract class PublicSetterMetaParser<SetterMeta extends AbstractModelSetterMeta> implements ModelMethodMetaParser<SetterMeta> {
    @Override
    public final List<SetterMeta> parse(TypeElement typeElement) {
        return ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
                .filter(field -> !field.getModifiers().contains(Modifier.STATIC))
                .filter(MethodUtils::isSetter)
                .map(method -> parseFromElement(method, method.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * 获取GetterMeta
     *
     * @param executableElement ExecutableElement
     * @return ModelMeta
     */
    protected abstract SetterMeta metaFor(ExecutableElement executableElement);

    /**
     * 从element解析
     *
     * @param executableElement element
     * @param modifiers         modifiers
     * @return GetterMeta
     */
    private SetterMeta parseFromElement(ExecutableElement executableElement, Set<Modifier> modifiers) {
        SetterMeta setterMeta = metaFor(executableElement);

        String name = TypeUtils.getNameFromSetter(executableElement.getSimpleName().toString());
        setterMeta.setName(name);
        setterMeta.setType(TypeName.get(executableElement.getParameters().get(0).asType()));
        setterMeta.setAccessLevel(AccessLevel.getFromModifiers(modifiers));

        // TODO xxx
//        GenDtoIgnore voIgnore = executableElement.getAnnotation(GenDtoIgnore.class);
//        if (voIgnore != null){
//            setterMeta.setIgnore(true);
//        }

        Description description = executableElement.getAnnotation(Description.class);
        if (description != null) {
            setterMeta.setDescription(description.value());
        }
        return setterMeta;
    }
}
