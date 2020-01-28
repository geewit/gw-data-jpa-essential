package io.geewit.data.jpa.essential.convert;


import io.geewit.core.utils.enums.EnumUtils;
import io.geewit.core.utils.enums.Value;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;

@SuppressWarnings({"unused"})
public abstract class AbstractEnumValueConverter<E extends Enum<E> & Value> implements AttributeConverter<E, Integer> {
    public AbstractEnumValueConverter() {
        clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Class<E> clazz;

    @SuppressWarnings({"unchecked"})
    @Override
    public Integer convertToDatabaseColumn(E enumValue) {
        return enumValue.value();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public E convertToEntityAttribute(Integer columnValue) {
        return EnumUtils.forToken(clazz, columnValue);
    }
}
