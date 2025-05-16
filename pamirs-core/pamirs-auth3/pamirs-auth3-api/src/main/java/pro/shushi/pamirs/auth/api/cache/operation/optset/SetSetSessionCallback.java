package pro.shushi.pamirs.auth.api.cache.operation.optset;

import org.springframework.data.redis.core.SessionCallback;

import java.util.Collection;

/**
 * Set类型Set操作会话回调函数
 *
 * @param <V> 缓存值类型
 * @author Adamancy Zhang at 11:25 on 2024-01-08
 */
public class SetSetSessionCallback<V> extends AbstractSetSessionCallback<V> implements SessionCallback<Void> {

    public SetSetSessionCallback(Collection<SetEntity<V>> collection) {
        super(collection, (operations, entry) -> {
            String key = entry.getKey();
            operations.delete(key);
            V[] values = entry.getCacheSet();
            if (values.length >= 1) {
                operations.opsForSet().add(key, values);
            }
        });
    }
}