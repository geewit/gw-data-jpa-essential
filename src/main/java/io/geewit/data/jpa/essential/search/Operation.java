package io.geewit.data.jpa.essential.search;

public interface Operation {
    SearchFilter toSearchFilter(String fieldName, Object... value);
}
