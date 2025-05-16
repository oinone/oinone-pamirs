package pro.shushi.pamirs.auth.api.cache.operation.opthash;

import org.springframework.data.redis.core.SessionCallback;

import java.util.Collection;

/**
 * Hash类型Put操作会话回调函数
 *
 * @author Adamancy Zhang at 17:35 on 2024-01-10
 */
public class HashPutSessionCallback<HK, HV> extends AbstractHashSessionCallback<HK, HV> implements SessionCallback<Void> {

    public HashPutSessionCallback(Collection<HashEntity<HK, HV>> collection) {
        super(collection, (operations, entry) -> {
            operations.<HK, HV>opsForHash().putAll(entry.getKey(), entry.getCacheHash());
        });
    }
}
