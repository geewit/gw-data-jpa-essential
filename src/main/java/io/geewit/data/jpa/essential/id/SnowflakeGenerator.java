package io.geewit.data.jpa.essential.id;

import io.geewit.snowflake.SnowFlake;
import io.geewit.snowflake.utils.NetUtils;
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
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

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

    private static final Optional<MessageDigest> MD5_DIGEST;

    static {
        Optional<MessageDigest> messageDigestOptional;
        try {
            messageDigestOptional = Optional.of(MessageDigest.getInstance("md5"));
        } catch (NoSuchAlgorithmException e) {
            messageDigestOptional = Optional.empty();
        }
        MD5_DIGEST = messageDigestOptional;
    }

    private static long MAC = 0L;

    private static long MAC_ID = LOW_WORKER_ID;

    private long workerId;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        if (MAC == 0L) {
            try {
                MAC = NetUtils.getLongMac();
                logger.debug("mac: " + MAC);
                MAC_ID = LOW_WORKER_ID & MAC;
            } catch (SocketException e) {
                logger.warn(e.getMessage());
            }
        }

        String entityName = params.getProperty(ENTITY_NAME);
        this.workerId = workerId(entityName);
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
    public static long id(String entityName) {
        long workerId = workerId(entityName);
        return SnowFlake.ofDefault(workerId).getUID();
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
            if (MD5_DIGEST.isPresent()) {
                byte[] md5 = MD5_DIGEST.get().digest(entityName.getBytes(StandardCharsets.UTF_8));
                entityId = byteToLong(md5) & HIGH_WORKER_ID;
            } else {
                entityId = HIGH_WORKER_ID;
            }
            ID_CACHE.put(entityName, entityId);
        } else {
            entityId = entityIdObj;
        }
        return entityId;
    }

    private static long workerId(String entityName) {
        long entityId = entityId(entityName);
        long workerId = entityId | MAC_ID;
        return workerId;
    }

    private static long byteToLong(byte[] bytes) {
        long padToLong = (bytes[0] & 255);

        padToLong |= IntStream.range(1, Math.min(bytes.length, 8))
                .mapToLong(i -> ((long) bytes[i] & 255L) << i * 8)
                .reduce(0, (a, b) -> a | b);
        return padToLong;
    }
}
