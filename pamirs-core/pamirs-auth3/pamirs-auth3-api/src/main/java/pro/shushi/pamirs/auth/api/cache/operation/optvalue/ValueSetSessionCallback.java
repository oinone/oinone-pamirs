package pro.shushi.pamirs.auth.api.cache.operation.optvalue;

import org.springframework.data.redis.core.SessionCallback;

import java.util.Collection;

/**
 * Value类型Set操作会话回调函数
 *
 * @author Adamancy Zhang at 14:13 on 2024-01-22
 */
public class ValueSetSessionCallback<V> extends AbstractValueSessionCallback<V, ValueEntity<V>> implements SessionCallback<Void> {

    public ValueSetSessionCallback(Collection<ValueEntity<V>> collection) {
        super(collection, (operations, entry) -> {
            operations.opsForValue().set(entry.getKey(), entry.getValue());
        });
    }
}
