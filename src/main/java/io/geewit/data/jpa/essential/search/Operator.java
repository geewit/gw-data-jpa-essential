package io.geewit.data.jpa.essential.search;

/**
 * @author geewit
 */
public enum Operator implements Operation {
    LIKE, LLIKE, RLIKE, EQ, GT, LT, GTE, LTE, NE,
    /**
     * //BETWEEN_fieldname_LOW, BETWEEN_fieldname_HIGH
     */
    BETWEEN,
    IN, NOTIN, ISNULL, ISNOTNULL;

    @Override
    public SearchFilter toSearchFilter(String fieldName, Object... value) {
        return new SearchFilter(fieldName, this, value);
    }
}
