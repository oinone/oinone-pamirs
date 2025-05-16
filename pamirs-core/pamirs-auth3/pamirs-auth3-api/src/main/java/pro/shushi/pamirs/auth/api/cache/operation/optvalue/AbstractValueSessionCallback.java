package pro.shushi.pamirs.auth.api.cache.operation.optvalue;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.IteratorSessionCallback;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * 抽象Value类型会话回调函数
 *
 * @author Adamancy Zhang at 14:14 on 2024-01-22
 */
public abstract class AbstractValueSessionCallback<V, E extends ValueEntity<V>> extends IteratorSessionCallback<V, E> implements SessionCallback<Void> {

    protected AbstractValueSessionCallback(Collection<E> collection, BiConsumer<RedisOperations<String, V>, E> operationsConsumer) {
        super(collection, operationsConsumer);
    }
}