package io.geewit.data.jpa.essential.id;

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

    private static final long MAX_WORKER_ID = ~(-1L << 22);

    private static final long LOW_WORKER_ID = ~(-1L << (22 >> 1));

    private static final long HIGH_WORKER_ID = LOW_WORKER_ID ^ MAX_WORKER_ID;

    private long workerId;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        this.workerId = SnowFlake.ofWorkerId();
        logger.debug("workerId = {}", this.workerId);
    }

    /**
     * 生成 snowflake id
     * @return snowflake id
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        logger.debug("workerId = {}", this.workerId);
        long id = SnowFlake.ofDefault(this.workerId).getUID();;
        logger.debug("generate.id = {}", id);
        return id;
    }

    /**
     * 生成 snowflake id
     * @return snowflake id
     */
    public static long id() {
        return SnowFlake.ofDefault(SnowFlake.ofWorkerId()).getUID();
    }
}
