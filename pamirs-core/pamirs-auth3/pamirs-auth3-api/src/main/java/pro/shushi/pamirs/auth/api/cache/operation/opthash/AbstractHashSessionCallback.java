package pro.shushi.pamirs.auth.api.cache.operation.opthash;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.IteratorSessionCallback;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * 抽象Hash类型会话回调函数
 *
 * @param <HK> 缓存Hash键
 * @param <HV> 缓存Hash值
 * @author Adamancy Zhang at 17:07 on 2024-01-10
 */
public abstract class AbstractHashSessionCallback<HK, HV> extends IteratorSessionCallback<HV, HashEntity<HK, HV>> implements SessionCallback<Void> {

    protected AbstractHashSessionCallback(Collection<HashEntity<HK, HV>> collection, BiConsumer<RedisOperations<String, HV>, HashEntity<HK, HV>> operationsConsumer) {
        super(collection, operationsConsumer);
    }
}
