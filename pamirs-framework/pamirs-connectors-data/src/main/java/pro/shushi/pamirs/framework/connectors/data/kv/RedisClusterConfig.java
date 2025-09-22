package pro.shushi.pamirs.framework.connectors.data.kv;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import pro.shushi.pamirs.framework.connectors.data.condition.RedisClusterModeCondition;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param clusterNodes 云节点
     * @param timeout      超时时间
     * @param redirects    redirects
     * @return Redis模板
     */
    public RedisClusterConfiguration getClusterConfiguration(List<String> clusterNodes, Long timeout, int redirects, String password) {
        Map<String, Object> source = new HashMap<>();
        source.put("spring.data.redis.cluster.nodes", String.join(",", clusterNodes));
        source.put("spring.data.redis.cluster.timeout", timeout);
        source.put("spring.data.redis.cluster.max-redirects", redirects);
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
        redisClusterConfiguration.setPassword(RedisPassword.of(password));
        return redisClusterConfiguration;
    }


    /**
     * 连接池设置
     *
     * @param redisClusterProperty 集群连接池配置
     * @return 连接工厂
     */
    @Bean
    public RedisConnectionFactory connectionFactory(@Autowired RedisClusterProperty redisClusterProperty, @Value("${spring.data.redis.password}") String password) {
        RedisClusterConfiguration configuration = getClusterConfiguration(redisClusterProperty.getNodes(), redisClusterProperty.getTimeout(), redisClusterProperty.getMaxRedirects(), password);
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    /**
     * 序列化工具
     * 使用 Spring 提供的序列化工具替换 Java 原生的序列化工具，这样 ReportBean 不需要实现 Serializable 接口
     *
     * @param template 连接模板
     */
    private void setSerializer(StringRedisTemplate template) {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(om, Object.class);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
    }

    /**
     * 管理缓存
     *
     * @param redisConnectionFactory 连接工厂
     * @return 缓存管理器
     */
//    @Bean
    public CacheManager cacheManager(@Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration).build();
    }


    /**
     * 生产key的策略
     *
     * @return key生成策略
     */
//    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
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
