package org.lan.iti.codegen.support.parser;

import com.squareup.javapoet.TypeName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.lan.iti.codegen.annotation.Description;
import org.lan.iti.codegen.support.meta.AbstractModelSetterMeta;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Lombok生成的Getter方法元数据解析器接口
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public abstract class LombokSetterMetaParser<SetterMeta extends AbstractModelSetterMeta> implements ModelMethodMetaParser<SetterMeta> {

    @Override
    public final List<SetterMeta> parse(TypeElement element) {
        Data data = element.getAnnotation(Data.class);
        Setter setter = element.getAnnotation(Setter.class);
        return ElementFilter.fieldsIn(element.getEnclosedElements()).stream()
                .filter(field -> !field.getModifiers().contains(Modifier.STATIC))
                .map(field -> {
                    Setter fieldSetter = field.getAnnotation(Setter.class);
                    if (fieldSetter != null) {
                        return parseFromElement(field, fieldSetter.value());
                    }
                    if (setter != null) {
                        return parseFromElement(field, setter.value());
                    }
                    if (data != null) {
                        return parseFromElement(field, AccessLevel.PUBLIC);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    /**
     * 获取GetterMeta
     *
     * @param element VariableElement
     * @return ModelMeta
     */
    protected abstract SetterMeta metaFor(VariableElement element);

    /**
     * 从element解析
     *
     * @param element     VariableElement
     * @param accessLevel AccessLevel
     * @return SetterMeta
     */
    private SetterMeta parseFromElement(VariableElement element, AccessLevel accessLevel) {
        SetterMeta setterMeta = metaFor(element);

        setterMeta.setName(element.getSimpleName().toString());
        setterMeta.setType(TypeName.get(element.asType()));
        setterMeta.setAccessLevel(org.lan.iti.codegen.support.AccessLevel.getFromAccessLevel(accessLevel));

        Description description = element.getAnnotation(Description.class);
        if (description != null) {
            setterMeta.setDescription(description.value());
        }

        return setterMeta;
    }
}
