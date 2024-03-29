package io.geewit.data.jpa.essential.repository.query;

import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;

/**
 * @author Réda Housni Alaoui
 */
public class EntityGraphAwareJpaQueryMethodFactory implements JpaQueryMethodFactory {

    private final QueryExtractor extractor;

    public EntityGraphAwareJpaQueryMethodFactory(QueryExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public JpaQueryMethod build(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        return new EntityGraphAwareJpaQueryMethod(method, metadata, factory, extractor);
    }
}
