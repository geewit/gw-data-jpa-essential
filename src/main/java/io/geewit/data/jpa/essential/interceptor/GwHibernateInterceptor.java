package io.geewit.data.jpa.essential.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GwHibernateInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;

    @Override
    public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
                           String[] propertyNames, Type[] types) {
        Set<String> dirtyProperties = new HashSet<>();
        for (int i = 0; i < propertyNames.length; i++) {
            if (isModified(currentState, previousState, types, i)) {
                dirtyProperties.add(propertyNames[i]);
            }
        }

        int[] dirtyPropertiesIndices = new int[dirtyProperties.size()];
        List<String> propertyNamesList = Stream.of(propertyNames).collect(Collectors.toList());
        int i = 0;
        for (String dirtyProperty : dirtyProperties) {
            dirtyPropertiesIndices[i++] = propertyNamesList.indexOf(dirtyProperty);
        }
        return dirtyPropertiesIndices;
    }


    private boolean isModified(Object[] currentState, Object[] previousState, Type[] types, int i) {
        boolean equals = true;
        Object oldValue = previousState[i];
        Object newValue = currentState[i];

        if (oldValue != null || newValue != null) {
            if (types[i] instanceof BasicType) {
                equals = types[i].isEqual(currentState[i], previousState[i]);
            } else if (types[i] instanceof SingleColumnType) {
                equals = String.valueOf(oldValue).equals(String.valueOf(newValue));
            } else if (!(types[i] instanceof CollectionType)) {
                equals = Objects.equals(oldValue, newValue);
            }
        }

        return !equals;
    }
}