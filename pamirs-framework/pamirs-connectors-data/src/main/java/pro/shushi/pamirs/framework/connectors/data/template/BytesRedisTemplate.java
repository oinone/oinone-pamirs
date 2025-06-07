package pro.shushi.pamirs.framework.connectors.data.template;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * BytesRedisTemplate
 *
 * @author yakir on 2022/08/02 16:30.
 */
public class BytesRedisTemplate extends RedisTemplate<String, byte[]> {

    public BytesRedisTemplate() {
        super();
        RedisConnectionFactory connectionFactory = BeanDefinitionUtils.getBean(RedisConnectionFactory.class);
        if (null == connectionFactory) {
            throw new RuntimeException("未获取到Redis连接！！！");
        }
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        this.setConnectionFactory(connectionFactory);
        this.setDefaultSerializer(RedisSerializer.byteArray());
        this.setKeySerializer(keySerializer);
        this.setHashKeySerializer(keySerializer);
        //this.setValueSerializer(null);
        //this.setHashValueSerializer(null);
        this.setEnableDefaultSerializer(false);
        this.afterPropertiesSet();
    }
}
