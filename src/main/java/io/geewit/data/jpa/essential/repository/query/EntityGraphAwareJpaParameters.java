package io.geewit.data.jpa.essential.repository.query;

import io.geewit.data.jpa.essential.domain.EntityGraph;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.repository.query.JpaParameters;

import java.lang.reflect.Method;

/**
 * @author Réda Housni Alaoui
 */
class EntityGraphAwareJpaParameters extends JpaParameters {

    public EntityGraphAwareJpaParameters(Method method) {
        super(method);
    }

    @Override
    protected JpaParameter createParameter(MethodParameter parameter) {
        return new EntityGraphAwareJpaParameter(parameter);
    }

    private static class EntityGraphAwareJpaParameter extends JpaParameter {

        private final boolean entityGraph;

        protected EntityGraphAwareJpaParameter(MethodParameter parameter) {
            super(parameter);
            this.entityGraph = EntityGraph.class.equals(parameter.getParameterType());
        }

        @Override
        public boolean isBindable() {
            return !entityGraph && super.isBindable();
        }

        @Override
        public boolean isSpecialParameter() {
            return entityGraph || super.isSpecialParameter();
        }
    }
}
