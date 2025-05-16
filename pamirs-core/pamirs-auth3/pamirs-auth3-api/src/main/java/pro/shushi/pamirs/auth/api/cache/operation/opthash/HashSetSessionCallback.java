package pro.shushi.pamirs.auth.api.cache.operation.opthash;

import org.springframework.data.redis.core.SessionCallback;

import java.util.Collection;

/**
 * Hash类型Set操作会话回调函数
 *
 * @author Adamancy Zhang at 17:06 on 2024-01-10
 */
public class HashSetSessionCallback<HK, HV> extends AbstractHashSessionCallback<HK, HV> implements SessionCallback<Void> {

    public HashSetSessionCallback(Collection<HashEntity<HK, HV>> collection) {
        super(collection, (operations, entry) -> {
            String key = entry.getKey();
            operations.delete(key);
            operations.<HK, HV>opsForHash().putAll(key, entry.getCacheHash());
        });
    }
}
