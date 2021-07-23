package io.geewit.data.jpa.essential.domain;

import java.util.List;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;

/**
 * Created on 22/11/16.
 *
 * @author Reda.Housni-Alaoui
 * @author geewit
 */
public interface EntityGraph {

  /** @return The type of the entity graph. May be null. */
  EntityGraphType getEntityGraphType();

  /** @return The name to use to retrieve the EntityGraph. May be null. */
  String getEntityGraphName();

  /** @return The attribute paths. May be null. */
  List<String> getEntityGraphAttributePaths();

  /**
   * @return True if the EntityGraph is optional.<br>
   *     Passing an optional EntityGraph to an unsupported method will not fail.
   */
  boolean isOptional();
}
