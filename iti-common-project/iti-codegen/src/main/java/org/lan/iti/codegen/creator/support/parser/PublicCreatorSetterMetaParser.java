package org.lan.iti.codegen.creator.support.parser;


import org.lan.iti.codegen.creator.support.meta.CreatorSetterMeta;
import org.lan.iti.codegen.support.parser.PublicSetterMetaParser;

import javax.lang.model.element.ExecutableElement;

/**
 * public 的 Setter 方法解析器
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
final class PublicCreatorSetterMetaParser
        extends PublicSetterMetaParser<CreatorSetterMeta>
        implements CreatorSetterMetaParser {

    @Override
    protected CreatorSetterMeta metaFor(ExecutableElement executableElement) {
        return metaOf(executableElement);
    }
}
