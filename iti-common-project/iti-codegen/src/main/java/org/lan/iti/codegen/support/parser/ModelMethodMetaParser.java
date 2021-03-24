package org.lan.iti.codegen.support.parser;


import org.lan.iti.codegen.support.meta.AbstractModelMethodMeta;

import javax.lang.model.element.TypeElement;
import java.util.List;

/**
 * 方法元数据解析器接口
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public interface ModelMethodMetaParser<M extends AbstractModelMethodMeta> {
    /**
     * 解析为方法元数据
     *
     * @param typeElement TypeElement
     * @return 方法元数据列表
     */
    List<M> parse(TypeElement typeElement);
}
