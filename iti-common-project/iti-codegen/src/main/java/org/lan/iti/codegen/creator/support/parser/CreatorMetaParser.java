package org.lan.iti.codegen.creator.support.parser;


import org.lan.iti.codegen.creator.support.meta.CreatorMeta;
import org.lan.iti.codegen.creator.support.meta.CreatorSetterMeta;
import org.lan.iti.codegen.support.parser.AbstractModelMetaParser;

import javax.lang.model.element.TypeElement;

/**
 * CreatorSetterMeta 解析器
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public class CreatorMetaParser extends AbstractModelMetaParser<CreatorSetterMeta, CreatorMeta, CreatorSetterMetaParser> {

    @Override
    protected CreatorMeta modelFor(TypeElement typeElement) {
        return new CreatorMeta(typeElement);
    }

    @Override
    protected CreatorSetterMetaParser[] parsers() {
        return new CreatorSetterMetaParser[]{
                new LombokCreatorSetterMetaParser(),
                new PublicCreatorSetterMetaParser()
        };
    }
}
