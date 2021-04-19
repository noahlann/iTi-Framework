package org.lan.iti.cloud.codegen.creator.support.parser;


import org.lan.iti.cloud.codegen.creator.support.meta.CreatorSetterMeta;
import org.lan.iti.codegen.support.parser.LombokSetterMetaParser;

import javax.lang.model.element.VariableElement;

/**
 * lombok 的 Setter 方法解析器
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
final class LombokCreatorSetterMetaParser
        extends LombokSetterMetaParser<CreatorSetterMeta>
        implements CreatorSetterMetaParser {

    @Override
    protected CreatorSetterMeta metaFor(VariableElement element) {
        return metaOf(element);
    }

}
