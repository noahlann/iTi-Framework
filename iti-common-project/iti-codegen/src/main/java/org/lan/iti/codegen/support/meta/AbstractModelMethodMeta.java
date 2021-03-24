package org.lan.iti.codegen.support.meta;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.Setter;

/**
 * 模型方法元数据
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
@Setter(AccessLevel.PUBLIC)
public abstract class AbstractModelMethodMeta {
    private boolean ignore;
    private String name;
    private String description;
    private org.lan.iti.codegen.support.AccessLevel accessLevel;

    public final org.lan.iti.codegen.support.AccessLevel accessLevel() {
        return this.accessLevel;
    }

    public final boolean ignore() {
        return this.ignore;
    }

    public final String name() {
        return this.name;
    }

    public final String description() {
        return this.description == null ? "" : this.description;
    }

    protected void merge(AbstractModelMethodMeta other) {
        if (other.ignore()) {
            setIgnore(true);
        }
        if (StrUtil.isNotEmpty(other.description())) {
            setDescription(other.description());
        }
        if (this.accessLevel == null) {
            setAccessLevel(other.accessLevel());
        }
    }
}
