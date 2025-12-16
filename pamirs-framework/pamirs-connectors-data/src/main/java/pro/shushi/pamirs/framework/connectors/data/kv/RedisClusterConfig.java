package pro.shushi.pamirs.framework.connectors.data.kv;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import pro.shushi.pamirs.framework.connectors.data.condition.RedisClusterModeCondition;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

/**
 * redis封装
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 6:41 下午
 */
@Configuration
@Conditional(RedisClusterModeCondition.class)
public class RedisClusterConfig {

    @Autowired
    private PamirsFrameworkSystemConfiguration systemConfiguration;

    @Value("${spring.data.redis.prefix:}")
    private String prefix;

    @Bean(name = "pamirsStringRedisSerializer")
    @ConditionalOnMissingBean(name = "pamirsStringRedisSerializer")
    @ConditionalOnSingleCandidate(PamirsStringRedisSerializer.class)
    public PamirsStringRedisSerializer pamirsStringRedisSerializer() {
        String prefix = this.prefix;
        if (StringUtils.isBlank(prefix)) {
            prefix = systemConfiguration.getIsolationKey();
        }
        return new PamirsStringRedisSerializer(prefix);
    }

    /**
     * 初始化 RedisTemplate
     * Spring 使用 StringRedisTemplate 封装了 RedisTemplate 对象来进行对redis的各种操作，它支持所有的 redis 原生的 api。
     *
     * @param redisConnectionFactory RedisConnectionFactory
     * @return Redis模板
     */
    @SuppressWarnings("rawtypes")
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
     * Redis Cluster参数配置
     *
     * @param redisClusterProperty 集群配置
     * @return Redis配置
     */
    public RedisClusterConfiguration getClusterConfiguration(RedisClusterProperty redisClusterProperty, String username, String password) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setUsername(username);
        redisClusterConfiguration.setPassword(RedisPassword.of(password));
        redisClusterConfiguration.setMaxRedirects(redisClusterProperty.getMaxRedirects());
        for (String hostAndPort : redisClusterProperty.getNodes()) {
            redisClusterConfiguration.addClusterNode(RedisNode.fromString(hostAndPort));
        }
        return redisClusterConfiguration;
    }


    /**
     * 连接池设置
     *
     * @param redisClusterProperty 集群连接池配置
     * @return 连接工厂
     */
    @Bean
    public RedisConnectionFactory connectionFactory(@Autowired RedisClusterProperty redisClusterProperty,
                                                    @Value("${spring.data.redis.username}") String username,
                                                    @Value("${spring.data.redis.password}") String password) {
        RedisClusterConfiguration configuration = getClusterConfiguration(redisClusterProperty, username, password);
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    private <K, V> void setKeySerializer(RedisTemplate<K, V> redisTemplate, PamirsStringRedisSerializer pamirsStringRedisSerializer) {
        redisTemplate.setKeySerializer(pamirsStringRedisSerializer);
    }

    private <K, V> void setValueSerializer(RedisTemplate<K, V> redisTemplate, Class<V> valueClass) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 在序列化中增加类信息，否则无法反序列化。
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 解决value的序列化方式，使用Json。其中的日期再另外处理。
        Jackson2JsonRedisSerializer<V> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, valueClass);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    }

}
