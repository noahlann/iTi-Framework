package org.lan.iti.codegen.support.meta;

import cn.hutool.core.util.StrUtil;
import com.squareup.javapoet.TypeName;
import lombok.AccessLevel;
import lombok.Setter;

/**
 * 模型Setter元数据
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
@Setter(AccessLevel.PUBLIC)
public abstract class AbstractModelSetterMeta extends AbstractModelMethodMeta {
    private TypeName type;

    public final TypeName type() {
        return this.type;
    }

    protected void merge(AbstractModelSetterMeta other) {
        super.merge(other);
        if (other.ignore()) {
            setIgnore(true);
        }
        if (StrUtil.isNotEmpty(other.description())) {
            setDescription(other.description());
        }
    }
}
