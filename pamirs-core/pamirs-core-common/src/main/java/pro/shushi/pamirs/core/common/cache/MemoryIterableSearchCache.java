package pro.shushi.pamirs.core.common.cache;

import pro.shushi.pamirs.core.common.entry.InitializationBody;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-11-25 11:13
 */
public class MemoryIterableSearchCache<K, V> extends AbstractSearchCache<K, V, Iterable<V>> implements SearchCache<K, V, Iterable<V>> {

    private Iterator<V> iterator;

    public MemoryIterableSearchCache(Iterable<V> origin, UniqueKeyGenerator<V, K> keyGetter) {
        this(origin, keyGetter, new HashMap<>(16));
    }

    public MemoryIterableSearchCache(Iterable<V> origin, UniqueKeyGenerator<V, K> keyGetter, Map<K, InitializationBody<K, V>> cache) {
        this(origin, keyGetter, cache, (k, v) -> v != null);
    }

    public MemoryIterableSearchCache(Iterable<V> origin, UniqueKeyGenerator<V, K> keyGetter, Map<K, InitializationBody<K, V>> cache, ValuePredicate<K, V> computePredicate) {
        super(origin, cache, keyGetter, computePredicate);
        if (this.origin == null) {
            this.iterator = NullIterator.getInstance();
        } else {
            this.iterator = this.origin.iterator();
        }
    }

    @Override
    protected V next() {
        if (this.iterator.hasNext()) {
            return this.iterator.next();
        }
        return null;
    }

    @Override
    public boolean reset() {
        this.iterator = this.origin.iterator();
        return true;
    }

    @Override
    public boolean reset(Iterable<V> target) {
        this.origin = target;
        this.iterator = target.iterator();
        return true;
    }

    public Iterator<V> getIterator() {
        return iterator;
    }
}
