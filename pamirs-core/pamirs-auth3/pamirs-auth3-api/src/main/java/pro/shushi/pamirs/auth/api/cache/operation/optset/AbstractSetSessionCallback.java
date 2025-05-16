package pro.shushi.pamirs.auth.api.cache.operation.optset;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.IteratorSessionCallback;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * 抽象Set类型会话回调函数
 *
 * @param <V> 缓存值类型
 * @author Adamancy Zhang at 11:26 on 2024-01-08
 */
public abstract class AbstractSetSessionCallback<V> extends IteratorSessionCallback<V, SetEntity<V>> implements SessionCallback<Void> {

    protected AbstractSetSessionCallback(Collection<SetEntity<V>> collection, BiConsumer<RedisOperations<String, V>, SetEntity<V>> operationsConsumer) {
        super(collection, operationsConsumer);
    }
}