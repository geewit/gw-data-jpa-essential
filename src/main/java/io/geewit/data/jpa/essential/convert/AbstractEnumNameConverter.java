package io.geewit.data.jpa.essential.convert;


import io.geewit.core.utils.enums.EnumUtils;
import io.geewit.core.utils.enums.Name;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;

@SuppressWarnings({"unused"})
public abstract class AbstractEnumNameConverter<E extends Enum<E> & Name> implements AttributeConverter<E, String> {
    public AbstractEnumNameConverter() {
        clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Class<E> clazz;

    @Override
    public String convertToDatabaseColumn(E enumValue) {
        return enumValue.toString();
    }

    @Override
    public E convertToEntityAttribute(String columnValue) {
        try {
            return EnumUtils.forToken(clazz, columnValue);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
