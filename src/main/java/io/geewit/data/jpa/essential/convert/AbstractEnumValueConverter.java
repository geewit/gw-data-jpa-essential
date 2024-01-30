package io.geewit.data.jpa.essential.convert;


import io.geewit.core.utils.enums.EnumUtils;
import io.geewit.core.utils.enums.Value;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;

@SuppressWarnings({"unused"})
public abstract class AbstractEnumValueConverter<E extends Enum<E> & Value<N>, N extends Number> implements AttributeConverter<E, N> {
    public AbstractEnumValueConverter() {
        clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Class<E> clazz;

    @Override
    public N convertToDatabaseColumn(E enumValue) {
        return enumValue.value();
    }

    @Override
    public E convertToEntityAttribute(N columnValue) {
        try {
            return EnumUtils.forValue(clazz, columnValue);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
