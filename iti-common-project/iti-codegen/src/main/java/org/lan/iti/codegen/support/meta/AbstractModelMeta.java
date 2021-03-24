package org.lan.iti.codegen.support.meta;

import com.google.common.collect.Maps;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模型元数据基类
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public abstract class AbstractModelMeta<M extends AbstractModelMethodMeta> {
    private final TypeElement typeElement;
    private final Map<String, M> methods = Maps.newHashMap();

    public AbstractModelMeta(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public void addMethodMeta(M m) {
        M meta = this.methods.get(m.name());
        if (meta != null) {
            merge(meta, m);
        } else {
            this.methods.put(m.name(), m);
        }
    }

    protected void merge(M s, M n) {

    }

    protected boolean accept(M m) {
        return true;
    }

    public List<M> getMethodMetas() {
        return this.methods.values().stream()
                .filter(m -> !m.ignore())
                .filter(this::accept).collect(Collectors.toList());
    }
}
