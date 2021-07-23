package io.geewit.data.jpa.essential.repository.support;

import io.geewit.data.jpa.essential.domain.EntityGraph;
import io.geewit.data.jpa.essential.domain.EntityGraphUtils;
import io.geewit.data.jpa.essential.domain.EntityGraphs;
import io.geewit.data.jpa.essential.exception.InapplicableEntityGraphException;
import io.geewit.data.jpa.essential.exception.MultipleDefaultEntityGraphException;
import io.geewit.data.jpa.essential.exception.MultipleEntityGraphException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;

/**
 * Captures {@link EntityGraph} on repositories method calls. Created on 22/11/16.
 *
 * @author Reda.Housni-Alaoui
 * @author geewit
 */
class RepositoryMethodEntityGraphExtractor implements RepositoryProxyPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryMethodEntityGraphExtractor.class);

    private static final ThreadLocal<JpaEntityGraphMethodInterceptor> CURRENT_REPOSITORY =
            new NamedThreadLocal<>("Thread local holding the current repository");

    private final EntityManager entityManager;

    RepositoryMethodEntityGraphExtractor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    static EntityGraphBean getCurrentJpaEntityGraph() {
        JpaEntityGraphMethodInterceptor currentRepository = CURRENT_REPOSITORY.get();
        if (currentRepository == null) {
            return null;
        }
        return currentRepository.getCurrentJpaEntityGraph();
    }

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        factory.addAdvice(new JpaEntityGraphMethodInterceptor(entityManager, repositoryInformation.getDomainType()));
        CURRENT_REPOSITORY.remove();
    }

    private static class JpaEntityGraphMethodInterceptor implements MethodInterceptor {

        private static final String DEFAULT_ENTITYGRAPH_NAME_SUFFIX = ".default";
        private final Class<?> domainClass;
        private final EntityGraph defaultEntityGraph;
        private final ThreadLocal<EntityGraphBean> currentEntityGraph =
                new NamedThreadLocal<>(
                        "Thread local holding the current spring data jpa repository entity graph");

        JpaEntityGraphMethodInterceptor(EntityManager entityManager, Class<?> domainClass) {
            this.domainClass = domainClass;
            this.defaultEntityGraph = findDefaultEntityGraph(entityManager, domainClass);
        }

        /**
         * @return The default entity graph if it exists. Null otherwise.
         */
        private static <T> EntityGraph findDefaultEntityGraph(EntityManager entityManager, Class<T> domainClass) {
            EntityGraph defaultEntityGraph = null;
            List<javax.persistence.EntityGraph<? super T>> entityGraphs =
                    entityManager.getEntityGraphs(domainClass);
            for (javax.persistence.EntityGraph<? super T> entityGraph : entityGraphs) {
                if (!entityGraph.getName().endsWith(DEFAULT_ENTITYGRAPH_NAME_SUFFIX)) {
                    continue;
                }
                if (defaultEntityGraph != null) {
                    throw new MultipleDefaultEntityGraphException(entityGraph.getName(), defaultEntityGraph.getEntityGraphName());
                }
                defaultEntityGraph = EntityGraphUtils.fromName(entityGraph.getName(), true);
            }
            return defaultEntityGraph;
        }

        EntityGraphBean getCurrentJpaEntityGraph() {
            return currentEntityGraph.get();
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            JpaEntityGraphMethodInterceptor oldRepo = CURRENT_REPOSITORY.get();
            CURRENT_REPOSITORY.set(this);
            try {
                return this.doInvoke(invocation);
            } finally {
                CURRENT_REPOSITORY.set(oldRepo);
            }
        }

        private Object doInvoke(MethodInvocation invocation) throws Throwable {
            Object[] arguments = invocation.getArguments();
            EntityGraph providedEntityGraph = null;
            for (Object argument : arguments) {
                if (!(argument instanceof EntityGraph)) {
                    continue;
                }
                EntityGraph newEntityGraph = (EntityGraph) argument;
                if (providedEntityGraph != null) {
                    throw new MultipleEntityGraphException(
                            "Duplicate EntityGraphs detected. '"
                                    + providedEntityGraph
                                    + "' and '"
                                    + newEntityGraph
                                    + "' were passed to method "
                                    + invocation.getMethod());
                }
                providedEntityGraph = newEntityGraph;
            }

            Class<?> implementationClass;
            if (invocation instanceof ReflectiveMethodInvocation) {
                implementationClass = ((ReflectiveMethodInvocation) invocation).getProxy().getClass();
            } else {
                Object invocationObject = invocation.getThis();
                if (invocationObject != null) {
                    implementationClass = invocationObject.getClass();
                } else {
                    throw new IllegalArgumentException("invocation.getThis == null");
                }
            }

            EntityGraphBean entityGraphCandidate =
                    buildEntityGraphCandidate(
                            providedEntityGraph,
                            ResolvableType.forMethodReturnType(invocation.getMethod(), implementationClass));

            if (entityGraphCandidate != null && !entityGraphCandidate.isValid()) {
                if (entityGraphCandidate.isOptional()) {
                    logger.trace("Cannot apply EntityGraph {}", entityGraphCandidate);
                    entityGraphCandidate = null;
                } else {
                    throw new InapplicableEntityGraphException(
                            "Cannot apply EntityGraph " + entityGraphCandidate + " to the the current query");
                }
            }

            EntityGraphBean oldEntityGraphCandidate = currentEntityGraph.get();
            boolean newEntityGraphCandidatePreValidated =
                    entityGraphCandidate != null
                            && (oldEntityGraphCandidate == null || !oldEntityGraphCandidate.isPrimary());
            if (newEntityGraphCandidatePreValidated) {
                currentEntityGraph.set(entityGraphCandidate);
            }
            try {
                return invocation.proceed();
            } finally {
                if (newEntityGraphCandidatePreValidated) {
                    currentEntityGraph.set(oldEntityGraphCandidate);
                }
            }
        }

        private EntityGraphBean buildEntityGraphCandidate(
                EntityGraph providedEntityGraph, ResolvableType returnType) {
            boolean isPrimary = true;
            if (EntityGraphs.isEmpty(providedEntityGraph)) {
                providedEntityGraph = defaultEntityGraph;
                isPrimary = false;
            }
            if (providedEntityGraph == null) {
                return null;
            }

            EntityGraphType type = requireNonNull(providedEntityGraph.getEntityGraphType());

            List<String> attributePaths = providedEntityGraph.getEntityGraphAttributePaths();
            JpaEntityGraph jpaEntityGraph =
                    new JpaEntityGraph(
                            StringUtils.hasText(providedEntityGraph.getEntityGraphName())
                                    ? providedEntityGraph.getEntityGraphName()
                                    : domainClass.getName() + "-_-_-_-_-_-",
                            type,
                            attributePaths != null ? attributePaths.toArray(new String[0]) : null);

            return new EntityGraphBean(
                    jpaEntityGraph, domainClass, returnType, providedEntityGraph.isOptional(), isPrimary);
        }
    }
}
