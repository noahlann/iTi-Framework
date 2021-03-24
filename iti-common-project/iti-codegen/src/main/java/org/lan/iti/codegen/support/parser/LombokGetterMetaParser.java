package org.lan.iti.codegen.support.parser;

import com.squareup.javapoet.TypeName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.lan.iti.codegen.annotation.Description;
import org.lan.iti.codegen.support.meta.AbstractModelGetterMeta;

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
public abstract class LombokGetterMetaParser<GetterMeta extends AbstractModelGetterMeta> implements ModelMethodMetaParser<GetterMeta> {

    @Override
    public final List<GetterMeta> parse(TypeElement element) {
        Data data = element.getAnnotation(Data.class);
        Getter getter = element.getAnnotation(Getter.class);
        return ElementFilter.fieldsIn(element.getEnclosedElements()).stream()
                .filter(field -> !field.getModifiers().contains(Modifier.STATIC))
                .map(field -> {
                    Getter fieldGetter = field.getAnnotation(Getter.class);
                    if (fieldGetter != null) {
                        return parseFromElement(field, fieldGetter.value());
                    }
                    if (getter != null) {
                        return parseFromElement(field, getter.value());
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
    protected abstract GetterMeta metaFor(VariableElement element);

    /**
     * 从element解析
     *
     * @param element     VariableElement
     * @param accessLevel AccessLevel
     * @return GetterMeta
     */
    private GetterMeta parseFromElement(VariableElement element, AccessLevel accessLevel) {
        GetterMeta getterMeta = metaFor(element);

        getterMeta.setAccessLevel(org.lan.iti.codegen.support.AccessLevel.getFromAccessLevel(accessLevel));
        getterMeta.setName(element.getSimpleName().toString());

        getterMeta.setType(TypeName.get(element.asType()));

        Description description = element.getAnnotation(Description.class);
        if (description != null) {
            getterMeta.setDescription(description.value());
        }

        return getterMeta;

    }
}
