package io.geewit.data.jpa.essential.utils;

import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;


/**
 * @author geewit
 */
public class TupleQueryUtils {

    /**
     * Executes a count query and transparently sums up all values returned.
     *
     * @param query must not be {@literal null}.
     * @return
     */
    public static long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query, "TypedQuery must not be null!");

        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    /**
     * Creates a new count query for the given {@link EntityManager}.
     *
     * @param entityManager can be {@literal null}.
     * @param domainClass must not be {@literal null}.
     * @return
     */
    public static <T> TypedQuery<Long> getCountQuery(EntityManager entityManager, Class<T> domainClass, CriteriaQuery<Long> criteriaQuery, CriteriaBuilder criteriaBuilder, Predicate predicate) {

        Assert.notNull(entityManager, "entityManager must not be null!");
        Root<T> root = criteriaQuery.from(domainClass);
        if (criteriaQuery.isDistinct()) {
            criteriaQuery.select(criteriaBuilder.countDistinct(root));
        } else {
            criteriaQuery.select(criteriaBuilder.count(root));
        }
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(Collections.emptyList());
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery;
    }
}
