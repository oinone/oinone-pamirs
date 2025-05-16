package pro.shushi.pamirs.auth.api.cache.operation.optset;

import org.springframework.data.redis.core.SessionCallback;

import java.util.Collection;

/**
 * Set类型Add操作会话回调函数
 *
 * @param <V> 缓存值类型
 * @author Adamancy Zhang at 11:27 on 2024-01-08
 */
public class SetAddSessionCallback<V> extends AbstractSetSessionCallback<V> implements SessionCallback<Void> {

    public SetAddSessionCallback(Collection<SetEntity<V>> collection) {
        super(collection, (operations, entry) -> {
            operations.opsForSet().add(entry.getKey(), entry.getCacheSet());
        });
    }
}