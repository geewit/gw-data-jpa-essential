package io.geewit.data.jpa.essential.utils;

import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;


/**
 * @author geewit
 */
public class TupleQueryUtils {

    /**
     * Executes a count query and transparently sums up all values returned.
     *
     * @param entityManager must not be {@literal null}.
     * @return
     */
    public static <T> long executeCountQuery(EntityManager entityManager, Root<T> root, Predicate predicate) {

        Assert.notNull(entityManager, "entityManager must not be null!");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.where(predicate);
        if (criteriaQuery.isDistinct()) {
            criteriaQuery.select(criteriaBuilder.countDistinct(root));
        } else {
            criteriaQuery.select(criteriaBuilder.count(root));
        }
        TypedQuery<Long> criteriaCountQuery = entityManager.createQuery(criteriaQuery);
        List<Long> totals = criteriaCountQuery.getResultList();
        long total = 0L;
        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }
}
