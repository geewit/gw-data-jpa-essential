package io.geewit.data.jpa.essential.dao;

import io.geewit.data.jpa.essential.entity.IDGenerator;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author geewit
 */
@SuppressWarnings({"unused"})
public interface IDGeneratorDao extends PagingAndSortingRepository<IDGenerator, String>, JpaSpecificationExecutor<IDGenerator> {
}
