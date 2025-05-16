package pro.shushi.pamirs.auth.api.cache.operation.optset;

import org.springframework.data.redis.core.SessionCallback;

import java.util.Collection;

/**
 * Set类型Remove操作会话回调函数
 *
 * @param <V> 缓存值类型
 * @author Adamancy Zhang at 11:29 on 2024-01-08
 */
public class SetRemoveSessionCallback<V> extends AbstractSetSessionCallback<V> implements SessionCallback<Void> {

    public SetRemoveSessionCallback(Collection<SetEntity<V>> collection) {
        super(collection, (operations, entry) -> {
            operations.opsForSet().remove(entry.getKey(), (Object[]) entry.getCacheSet());
        });
    }
}