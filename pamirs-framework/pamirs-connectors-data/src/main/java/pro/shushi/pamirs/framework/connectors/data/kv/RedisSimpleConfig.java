package pro.shushi.pamirs.framework.connectors.data.kv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.framework.connectors.data.condition.RedisSimpleModeCondition;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

/**
 * @author shier
 * date 2020/4/21
 */
@Validated
@Component
@Conditional(RedisSimpleModeCondition.class)
public class RedisSimpleConfig {

    @Autowired
    private PamirsFrameworkSystemConfiguration systemConfiguration;

    @Value("${spring.redis.prefix:}")
    private String prefix;

    @Bean(name = "pamirsStringRedisSerializer")
    public PamirsStringRedisSerializer pamirsStringRedisSerializer() {
        String prefix = this.prefix;
        if (StringUtils.isBlank(prefix)) {
            prefix = systemConfiguration.getIsolationKey();
        }
        return new PamirsStringRedisSerializer(prefix);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory, @Autowired PamirsStringRedisSerializer pamirsStringRedisSerializer) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        setKeySerializer(redisTemplate, pamirsStringRedisSerializer);
        setValueSerializer(redisTemplate, Object.class);
        return redisTemplate;
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory, @Autowired PamirsStringRedisSerializer pamirsStringRedisSerializer) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        setKeySerializer(redisTemplate, pamirsStringRedisSerializer);
        setValueSerializer(redisTemplate, String.class);
        return redisTemplate;
    }

    private <K, V> void setKeySerializer(RedisTemplate<K, V> redisTemplate, PamirsStringRedisSerializer pamirsStringRedisSerializer) {
        redisTemplate.setKeySerializer(pamirsStringRedisSerializer);
    }

    private <K, V> void setValueSerializer(RedisTemplate<K, V> redisTemplate, Class<V> valueClass) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 在序列化中增加类信息，否则无法反序列化。
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 解决value的序列化方式，使用Json。其中的日期再另外处理。
        Jackson2JsonRedisSerializer<V> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, valueClass);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    }
}
