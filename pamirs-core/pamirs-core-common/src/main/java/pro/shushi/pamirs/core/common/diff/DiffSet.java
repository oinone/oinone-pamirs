package pro.shushi.pamirs.core.common.diff;

import java.util.Set;

/**
 * 差量集合 - 集合
 *
 * @author Adamancy Zhang at 20:31 on 2024-01-17
 */
public class DiffSet<V> extends DiffCollection<V, Set<V>> {

    protected static final DiffSet<?> EMPTY = new DiffSet<>(null, null, null, null);

    protected DiffSet(Set<V> all, Set<V> create, Set<V> update, Set<V> delete) {
        super(all, create, update, delete);
    }
}
