package org.lan.iti.codegen.support.parser;


import org.lan.iti.codegen.support.meta.AbstractModelMeta;
import org.lan.iti.codegen.support.meta.AbstractModelMethodMeta;

import javax.lang.model.element.TypeElement;

/**
 * 方法元数据解析器基类
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public abstract class AbstractModelMetaParser<MethodMeta extends AbstractModelMethodMeta,
        M extends AbstractModelMeta<MethodMeta>,
        MP extends ModelMethodMetaParser<MethodMeta>> {

    /**
     * 获取ModelMeta
     *
     * @param typeElement TypeElement
     * @return ModelMeta
     */
    protected abstract M modelFor(TypeElement typeElement);

    /**
     * 获取解析器列表
     *
     * @return 具体的解析器列表
     */
    protected abstract MP[] parsers();

    /**
     * 解析开始
     *
     * @param typeElement TypeElement
     * @return ModelMeta
     */
    public final M parse(TypeElement typeElement) {
        M m = modelFor(typeElement);
        for (MP mp : parsers()) {
            mp.parse(typeElement).forEach(m::addMethodMeta);
        }
        return m;
    }
}
