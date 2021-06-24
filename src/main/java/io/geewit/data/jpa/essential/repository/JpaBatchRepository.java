package io.geewit.data.jpa.essential.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * 批量保存
 * @author geewit
 */
@NoRepositoryBean
public interface JpaBatchRepository<T> {
    /**
     * Batch Saves all given entities.
     *
     * @param entities must not be {@literal null} nor must it contain {@literal null}.
     * @return the saved entities; will never be {@literal null}. The returned {@literal Iterable} will have the same size
     *         as the {@literal Iterable} passed as an argument.
     * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its entities is
     *           {@literal null}.
     */
    List<T> saveBatch(Iterable<T> entities);
}
