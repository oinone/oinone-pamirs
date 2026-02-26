package pro.shushi.pamirs.eip.api.strategy.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.strategy.constant.EipLogCountCacheConstant;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;

/**
 * 日志统计RedisTemplate
 *
 * @author yeshenyue on 2025/4/10 11:52.
 */
@Component(LogCountRedisTemplate.LOG_COUNT_REDIS_TEMPLATE_NAME)
public class LogCountRedisTemplate extends RedisTemplate<String, Long> {

    public static final String LOG_COUNT_REDIS_TEMPLATE_NAME = "logCountRedisTemplate";

    public LogCountRedisTemplate(
            @Autowired RedisConnectionFactory redisConnectionFactory,
            @Autowired PamirsStringRedisSerializer pamirsStringRedisSerializer) {
        this.setConnectionFactory(redisConnectionFactory);

        this.setKeySerializer(pamirsStringRedisSerializer);
        this.setHashKeySerializer(RedisSerializer.string());

        this.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        this.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
    }

    /**
     * 增加调用次数
     *
     * @param interfaceName 接口技术名称
     * @param cacheConstant 具体字段
     */
    public void incrementCallCount(InterfaceTypeEnum interfaceType, String interfaceName,
                                   EipLogCountCacheConstant cacheConstant) {
        this.opsForValue().increment(cacheConstant.getKeyPrefix(interfaceType, interfaceName), 1L);
    }
}
