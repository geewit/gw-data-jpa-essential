package io.geewit.data.jpa.essential.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * 批量保存
 * @author geewit
 */
public interface JpaSpecificationGraphExecutor<T> extends JpaSpecificationExecutor<T> {
    /**
     * Returns a single entity matching the given {@link Specification} or {@link Optional#empty()} if none found.
     *
     * @param specification can be {@literal null}.
     * @return never {@literal null}.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one entity found.
     */
    Optional<T> findOne(@Nullable Specification<T> specification, EntityGraph.EntityGraphType entityGraphType, String entityGraphName);

    /**
     * Returns all entities matching the given {@link Specification}.
     *
     * @param specification can be {@literal null}.
     * @return never {@literal null}.
     */
    List<T> findAll(@Nullable Specification<T> specification, EntityGraph.EntityGraphType entityGraphType, String entityGraphName);

    /**
     * Returns a {@link Page} of entities matching the given {@link Specification}.
     *
     * @param specification can be {@literal null}.
     * @param pageable must not be {@literal null}.
     * @return never {@literal null}.
     */
    Page<T> findAll(@Nullable Specification<T> specification, Pageable pageable, EntityGraph.EntityGraphType entityGraphType, String entityGraphName);

    /**
     * Returns all entities matching the given {@link Specification} and {@link Sort}.
     *
     * @param specification can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @return never {@literal null}.
     */
    List<T> findAll(@Nullable Specification<T> specification, Sort sort, EntityGraph.EntityGraphType entityGraphType, String entityGraphName);

    /**
     * Returns the number of instances that the given {@link Specification} will return.
     *
     * @param specification the {@link Specification} to count instances for. Can be {@literal null}.
     * @return the number of instances.
     */
    long count(@Nullable Specification<T> specification, EntityGraph.EntityGraphType entityGraphType, String entityGraphName);
}
