package io.geewit.data.jpa.essential.id;

import com.google.common.hash.Hashing;
import io.geewit.snowflake.SnowFlake;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展hibernate生成id的uuid生成器
 *
 * @author geewit
 * @since 2015-05-18
 */
@SuppressWarnings({"unused"})
public class SnowflakeGenerator implements IdentifierGenerator, Configurable {
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeGenerator.class);


    private static final ConcurrentHashMap<String, Long> ID_CACHE = new ConcurrentHashMap<>();

    private long entityId;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        String entityName = params.getProperty(ENTITY_NAME);
        this.entityId = entityId(entityName);
        logger.debug("entityId = {}", this.entityId);
    }

    /**
     * 生成 snowflake id
     * @return snowflake id
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        logger.debug("entityId = {}", this.entityId);
        long id = SnowFlake.ofDefault(this.entityId).getUID();;
        logger.debug("generate.id = {}", id);
        return id;
    }

    /**
     * 生成 snowflake id
     * @return snowflake id
     */
    public static long id(String entityName) {
        long entityId = entityId(entityName);
        return SnowFlake.ofDefault(entityId).getUID();
    }

    /**
     * 生成 snowflake id
     * @return snowflake id
     */
    public static long id(Class<?> entityClass) {
        String entityName = entityClass.getSimpleName();
        return id(entityName);
    }

    private static long entityId(String entityName) {
        long entityId;
        Long entityIdObj = ID_CACHE.get(entityName);
        if (entityIdObj == null) {
            entityId = Hashing.sha256().hashString(entityName, StandardCharsets.UTF_8).padToLong();
            ID_CACHE.put(entityName, entityId);
        } else {
            entityId = entityIdObj;
        }
        return entityId;
    }
}
