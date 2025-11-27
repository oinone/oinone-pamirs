package pro.shushi.pamirs.framework.connectors.data.kv;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import pro.shushi.pamirs.framework.connectors.data.condition.RedisSentinelModeCondition;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

@Configuration
@Conditional(RedisSentinelModeCondition.class)
public class RedisSentinelConfig {

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

    /**
     * 构建Redis哨兵配置
     * @param sentinelProperty 哨兵配置属性
     * @param password Redis主从节点的密码（非哨兵密码）
     * @return 哨兵配置对象
     */
    public RedisSentinelConfiguration getSentinelConfiguration(RedisSentinelProperty sentinelProperty, String password) {
        // 1. 创建哨兵配置，指定主节点名称 与 哨兵节点
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration(sentinelProperty.getMaster(), sentinelProperty.getNodes());
        // 2. 设置Redis主从节点的密码（核心：是Redis实例的密码，不是哨兵的）
        sentinelConfig.setPassword(RedisPassword.of(password));
        return sentinelConfig;
    }

    /**
     * 创建哨兵模式的Redis连接工厂
     * @param sentinelProperty 哨兵配置属性
     * @param password Redis主从节点的密码（从配置文件读取）
     * @return 连接工厂
     */
    @Bean
    public RedisConnectionFactory connectionFactory(
            RedisSentinelProperty sentinelProperty,
            @Value("${spring.redis.password}") String password) {

        // 1. 获取哨兵配置
        RedisSentinelConfiguration configuration = getSentinelConfiguration(sentinelProperty, password);

        // 2. 创建Jedis连接工厂（哨兵模式仍用JedisConnectionFactory，入参为哨兵配置）
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);

        // 3. 初始化配置（必填，触发连接池初始化）
        connectionFactory.afterPropertiesSet();

        return connectionFactory;
    }
    private <K, V> void setKeySerializer(RedisTemplate<K, V> redisTemplate, PamirsStringRedisSerializer pamirsStringRedisSerializer) {
        redisTemplate.setKeySerializer(pamirsStringRedisSerializer);
    }

    private <K, V> void setValueSerializer(RedisTemplate<K, V> redisTemplate, Class<V> valueClass) {
        // 解决value的序列化方式，使用Json。其中的日期再另外处理。
        Jackson2JsonRedisSerializer<V> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(valueClass);
        ObjectMapper objectMapper                = new ObjectMapper();

        // 在序列化中增加类信息，否则无法反序列化。
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    }


}
