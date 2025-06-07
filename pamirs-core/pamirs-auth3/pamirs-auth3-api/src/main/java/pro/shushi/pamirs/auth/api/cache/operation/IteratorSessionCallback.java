package pro.shushi.pamirs.auth.api.cache.operation;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.function.BiConsumer;

/**
 * 可迭代的会话回调
 *
 * @author Adamancy Zhang at 19:15 on 2024-01-22
 */
public class IteratorSessionCallback<RV, V> implements SessionCallback<Void> {

    private final Iterable<V> collection;

    private final BiConsumer<RedisOperations<String, RV>, V> operationsConsumer;

    public IteratorSessionCallback(Iterable<V> collection, BiConsumer<RedisOperations<String, RV>, V> operationsConsumer) {
        this.collection = collection;
        this.operationsConsumer = operationsConsumer;
    }

    public Iterable<V> getCollection() {
        return collection;
    }

    public BiConsumer<RedisOperations<String, RV>, V> getOperationsConsumer() {
        return operationsConsumer;
    }

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public Void execute(RedisOperations operations) throws DataAccessException {
        Iterable<V> collection = getCollection();
        BiConsumer<RedisOperations<String, RV>, V> consumer = getOperationsConsumer();
        for (V item : collection) {
            consumer.accept(operations, item);
        }
        return null;
    }
}
