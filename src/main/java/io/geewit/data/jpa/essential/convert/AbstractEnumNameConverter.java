package io.geewit.data.jpa.essential.convert;


import io.geewit.core.utils.lang.enums.EnumUtils;
import io.geewit.core.utils.lang.enums.Name;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;

@SuppressWarnings({"unused"})
public abstract class AbstractEnumNameConverter<E extends Enum<E> & Name> implements AttributeConverter<E, String> {
    public AbstractEnumNameConverter() {
        clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Class<E> clazz;

    @SuppressWarnings({"unchecked"})
    @Override
    public String convertToDatabaseColumn(E enumValue) {
        return enumValue.toString();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public E convertToEntityAttribute(String columnValue) {
        return EnumUtils.forToken(clazz, columnValue);
    }
}
