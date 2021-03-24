package org.lan.iti.codegen.support.meta;

import cn.hutool.core.util.StrUtil;
import com.squareup.javapoet.TypeName;
import lombok.AccessLevel;
import lombok.Setter;

/**
 * 模型Getter元数据
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
@Setter(AccessLevel.PUBLIC)
public abstract class AbstractModelGetterMeta extends AbstractModelMethodMeta {
    private TypeName type;

    public final TypeName type() {
        return this.type;
    }

    protected void merge(AbstractModelGetterMeta methodMeta) {
        super.merge(methodMeta);
        if (methodMeta.ignore()) {
            setIgnore(true);
        }
        if (StrUtil.isNotEmpty(methodMeta.description())) {
            setDescription(methodMeta.description());
        }
    }
}
