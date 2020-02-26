package io.geewit.data.jpa.essential.id;

import io.geewit.snowflake.SnowFlake;
import io.geewit.snowflake.utils.NetUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.SocketException;
import java.util.Map;
import java.util.Properties;

/**
 * 扩展hibernate生成id的uuid生成器
 *
 * @author geewit
 * @since 2015-05-18
 */
@SuppressWarnings({"unused"})
public class SnowflakeGenerator implements IdentifierGenerator, Configurable {
    private final static Logger logger = LoggerFactory.getLogger(SnowflakeGenerator.class);

    private static final String WORKER_ID_SETTING_KEY = "geewit.snowflake.worker_id";
    private static final String APP_ID_SETTING_KEY = "geewit.snowflake.app_id";

    private static long WORKER_ID;
    private static long APP_ID;


    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        if(WORKER_ID == 0) {
            Map<?, ?> settings = serviceRegistry.getService(ConfigurationService.class).getSettings();
            Object objWorkerId = settings.get(WORKER_ID_SETTING_KEY);
            String workerId;
            if(objWorkerId != null) {
                workerId = objWorkerId.toString();
                logger.debug("settings.workerId = " + workerId);
                try {
                    WORKER_ID = Long.parseLong(workerId);
                } catch (NumberFormatException ignored) {
                }
            } else {
                Object objAppId = settings.get(APP_ID_SETTING_KEY);
                if(objAppId != null) {
                    String appId = objAppId.toString();
                    logger.debug("settings.appId = " + appId);
                    try {
                        APP_ID = Long.parseLong(appId);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if(APP_ID == 0) {
                    APP_ID = RandomUtils.nextLong(1, 10000);
                }
                try {
                    long mac = NetUtils.getLongMac();
                    logger.info("mac: " + mac);
                    WORKER_ID = (APP_ID ^ mac);
                } catch (SocketException e) {
                    logger.warn(e.getMessage());
                }
            }
            if(WORKER_ID == 0) {
                WORKER_ID = RandomUtils.nextLong(1, 10000);
            }
        }
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        long id = id();
        logger.debug("generate.id = " + id);
        return id;
    }

    /**
     * 生成 snowflake id
     * @return snowflake id
     */
    public static long id() {
        return SnowFlake.ofCached(WORKER_ID).getUID();
    }
}
