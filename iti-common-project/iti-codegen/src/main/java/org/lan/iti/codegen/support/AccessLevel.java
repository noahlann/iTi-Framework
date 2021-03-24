package org.lan.iti.codegen.support;


import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * 访问级别
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public enum AccessLevel {
    /**
     * 公开
     */
    PUBLIC,

    /**
     * 保护
     */
    PROTECTED,

    /**
     * 包级别
     */
    PACKAGE,

    /**
     * 私有
     */
    PRIVATE;

    /**
     * 从类的元数据中转换
     *
     * @param modifiers Modifiers
     */
    public static AccessLevel getFromModifiers(Set<Modifier> modifiers) {
        if (modifiers.contains(Modifier.PUBLIC)) {
            return PUBLIC;
        }
        if (modifiers.contains(Modifier.PRIVATE)) {
            return PRIVATE;
        }
        if (modifiers.contains(Modifier.PROTECTED)) {
            return PROTECTED;
        }
        return PACKAGE;
    }

    /**
     * 从Lombok的AccessLevel注解转换，兼容Lombok
     *
     * @param accessLevel 访问级别
     */
    public static AccessLevel getFromAccessLevel(lombok.AccessLevel accessLevel) {
        switch (accessLevel) {
            case PUBLIC:
                return PUBLIC;
            case PROTECTED:
                return PROTECTED;
            case PRIVATE:
                return PRIVATE;
            default:
                return PACKAGE;
        }
    }
}
