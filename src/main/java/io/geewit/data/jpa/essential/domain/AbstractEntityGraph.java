package io.geewit.data.jpa.essential.domain;


import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;

/**
 * Created on 22/11/16.
 *
 * @author Reda.Housni-Alaoui
 * @author geewit
 */
public abstract class AbstractEntityGraph implements EntityGraph {

    protected static final EntityGraphType DEFAULT_ENTITY_GRAPH_TYPE = EntityGraphType.LOAD;
    private EntityGraphType entityGraphType = DEFAULT_ENTITY_GRAPH_TYPE;
    private boolean optional;

    public AbstractEntityGraph() {
    }

    public AbstractEntityGraph(EntityGraphType entityGraphType) {
        this(entityGraphType, false);
    }

    public AbstractEntityGraph(EntityGraphType entityGraphType, boolean optional) {
        this.entityGraphType = requireNonNull(entityGraphType);
        this.optional = optional;
    }

    @Override
    public EntityGraphType getEntityGraphType() {
        return entityGraphType;
    }

    public void setEntityGraphType(EntityGraphType entityGraphType) {
        this.entityGraphType = requireNonNull(entityGraphType);
    }

    /**
     * False by default
     *
     * @return True if the EntityGraph is optional.<br>
     * Passing an optional EntityGraph to an unsupported method will not trigger {@link
     * io.geewit.data.jpa.essential.exception.InapplicableEntityGraphException}.
     */
    @Override
    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

  @Override
  public String toString() {
    return new StringJoiner(", ", AbstractEntityGraph.class.getSimpleName() + "[", "]")
            .add("entityGraphType=" + entityGraphType)
            .add("optional=" + optional)
            .toString();
  }
}
