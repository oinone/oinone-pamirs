package pro.shushi.pamirs.core.common.cache;

import pro.shushi.pamirs.core.common.entry.InitializationBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-11-25 10:34
 */
public class MemoryListSearchCache<K, V> extends AbstractSearchCache<K, V, List<V>> implements SearchCache<K, V, List<V>> {

    private static final int DEFAULT_OFFSET = 0;

    private int offset;

    public MemoryListSearchCache(List<V> origin, UniqueKeyGenerator<V, K> keyGetter) {
        this(origin, keyGetter, new HashMap<>());
    }

    public MemoryListSearchCache(List<V> origin, UniqueKeyGenerator<V, K> keyGetter, Map<K, InitializationBody<K, V>> cache) {
        this(origin, keyGetter, cache, (k, v) -> v != null);
    }

    public MemoryListSearchCache(List<V> origin, UniqueKeyGenerator<V, K> keyGetter, Map<K, InitializationBody<K, V>> cache, ValuePredicate<K, V> predicate) {
        super(origin, cache, keyGetter, predicate);
        if (origin == null) {
            this.origin = Collections.emptyList();
        } else {
            this.origin = origin;
        }
        this.offset = DEFAULT_OFFSET;
    }

    @Override
    protected V next() {
        if (offset < origin.size()) {
            return origin.get(offset++);
        }
        return null;
    }

    @Override
    public boolean reset() {
        this.offset = DEFAULT_OFFSET;
        return Boolean.TRUE;
    }

    @Override
    public boolean reset(List<V> target) {
        this.origin = target;
        this.offset = DEFAULT_OFFSET;
        return Boolean.TRUE;
    }

    /**
     * 重置搜索指针到指定偏移位置
     *
     * @param offset 偏移量
     */
    public void reset(int offset) {
        this.offset = offset;
    }
}
