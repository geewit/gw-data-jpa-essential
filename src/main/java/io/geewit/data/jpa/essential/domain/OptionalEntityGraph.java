package io.geewit.data.jpa.essential.domain;

import java.util.Optional;

/**
 * Created on 04/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public final class OptionalEntityGraph {

    /**
     * @return The entity graph if not null and not empty, empty otherwise
     */
    public static Optional<EntityGraph> of(EntityGraph entityGraph) {
        if (EntityGraphs.isEmpty(entityGraph)) {
            return Optional.empty();
        }
        return Optional.of(entityGraph);
    }
}
