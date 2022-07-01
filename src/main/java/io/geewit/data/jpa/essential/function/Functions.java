package io.geewit.data.jpa.essential.function;

/**
 * 自定义函数
 * @author geewit
 */
public interface Functions {
    /**
     * 按位与运算
     */
    String BITWISE_AND = "bitwiseAnd";

    /**
     * 按位或运算
     */
    String BITWISE_OR = "bitwiseOr";

    /**
     * 按位异或运算
     */
    String BITWISE_XOR = "bitwiseXor";

    /**
     * group_concat
     */
    String GROUP_CONCAT = "group_concat";
}
