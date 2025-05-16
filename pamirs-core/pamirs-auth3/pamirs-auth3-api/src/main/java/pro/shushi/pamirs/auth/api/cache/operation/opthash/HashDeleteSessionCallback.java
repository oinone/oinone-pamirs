package pro.shushi.pamirs.auth.api.cache.operation.opthash;

import org.springframework.data.redis.core.SessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.optset.AbstractSetSessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.optset.SetEntity;

import java.util.Collection;

/**
 * Hash类型Delete操作会话回调函数
 *
 * @author Adamancy Zhang at 17:39 on 2024-01-10
 */
public class HashDeleteSessionCallback<HK> extends AbstractSetSessionCallback<HK> implements SessionCallback<Void> {

    public HashDeleteSessionCallback(Collection<SetEntity<HK>> collection) {
        super(collection, (operations, entry) -> {
            operations.opsForHash().delete(entry.getKey(), (Object[]) entry.getCacheSet());
        });
    }
}
