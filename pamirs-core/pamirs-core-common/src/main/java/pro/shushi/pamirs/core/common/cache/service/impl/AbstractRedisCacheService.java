package pro.shushi.pamirs.core.common.cache.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import pro.shushi.pamirs.core.common.cache.service.RedisCacheService;
import pro.shushi.pamirs.core.common.cache.service.template.AbstractStringCacheServiceTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

/**
 * 抽象Redis缓存服务
 *
 * @author Adamancy Zhang at 16:41 on 2021-06-12
 */
@Slf4j
public abstract class AbstractRedisCacheService<T> extends AbstractStringCacheServiceTemplate<T> implements RedisCacheService<T> {

    /**
     * 获取Redis操作模板
     *
     * @return Redis操作模板
     */
    protected abstract StringRedisTemplate fetchRedisTemplate();

    @Override
    protected String getCacheData(String key) {
        return fetchRedisTemplate().opsForValue().get(key);
    }

    @Override
    protected void setCacheData(String key, String data) {
        fetchRedisTemplate().opsForValue().set(key, data);
    }

    @Override
    public void clear(String key) {
        fetchRedisTemplate().delete(key);
    }
}
