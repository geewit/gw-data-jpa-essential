package io.geewit.data.jpa.essential.convert;


import io.geewit.core.utils.enums.EnumMapUtils;

import javax.persistence.AttributeConverter;
import java.util.Map;

@SuppressWarnings({"unused"})
public abstract class AbstractEnumMapConverter<E extends Enum<E>> implements AttributeConverter<Map<E, Boolean>, Integer> {
    protected Class<E> clazz;

    @SuppressWarnings({"unchecked"})
    @Override
    public Integer convertToDatabaseColumn(Map<E, Boolean> attribute) {
        return EnumMapUtils.toBinary(attribute);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Map<E, Boolean> convertToEntityAttribute(Integer columnValue) {
        return EnumMapUtils.toEnumMap(clazz, columnValue);
    }
}
