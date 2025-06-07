package pro.shushi.pamirs.framework.connectors.data.kv;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.connectors.data.condition.RedisSimpleModeCondition;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

@Slf4j
@Configuration
@Conditional(RedisSimpleModeCondition.class)
public class RedisConfig {

    //    @Value("${spring.redis.host}")
    private String host;

    //    @Value("${spring.redis.port}")
    private int port;

    //    @Value("${spring.redis.timeout:2000}")
    private int timeout;

    //    @Value("${spring.redis.jedis.pool.max-idle:}")
    private int maxIdle;

    //    @Value("${spring.redis.jedis.pool.max-wait:}")
    private long maxWaitMillis;

    //    @Value("${spring.redis.password:}")
    private String password;

    //    @Value("${spring.redis.block-when-exhausted:true}")
    private boolean blockWhenExhausted;

//    @Bean
//    public JedisPool redisPoolFactory() throws Exception {
//        log.info("JedisPool注入成功！！");
//        log.info("redis地址：" + host + ":" + port);
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(maxIdle);
//        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
//        // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
//        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
//        // 是否启用pool的jmx管理功能, 默认true
//        jedisPoolConfig.setJmxEnabled(true);
//        if (StringUtils.isBlank(password)) {
//            password = null;
//        }
//        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
//        return jedisPool;
//    }

}