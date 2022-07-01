package io.geewit.data.jpa.essential.dialect;

import io.geewit.data.jpa.essential.function.Functions;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * 自定义 Mysql 5.7 方言
 * @author geewit
 */
public class GeewitMySQL57Dialect extends MySQL57Dialect {
    public GeewitMySQL57Dialect() {
        super();
        super.registerFunction(Functions.BITWISE_AND, new SQLFunctionTemplate(StandardBasicTypes.INTEGER,
                "(?1 & ?2)"));
        super.registerFunction(Functions.BITWISE_OR, new SQLFunctionTemplate(StandardBasicTypes.INTEGER,
                "(?1 | ?2)"));
        super.registerFunction(Functions.BITWISE_XOR, new SQLFunctionTemplate(StandardBasicTypes.INTEGER,
                "(?1 ^ ?2)"));
        super.registerFunction(Functions.GROUP_CONCAT, new StandardSQLFunction(Functions.GROUP_CONCAT));
    }
}
