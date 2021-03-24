package org.lan.iti.codegen.support;

import com.squareup.javapoet.TypeSpec;

/**
 * TypeSpec构建工厂接口
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public interface TypeBuilderFactory {
    /**
     * 创建 构建者
     *
     * @return 类型构建器
     */
    TypeSpec.Builder create();
}
