package pro.shushi.pamirs.core.common.diff;

import java.util.List;

/**
 * 差量集合 - 列表
 *
 * @author Adamancy Zhang at 20:30 on 2024-01-17
 */
public class DiffList<V> extends DiffCollection<V, List<V>> {

    protected static final DiffList<?> EMPTY = new DiffList<>(null, null, null, null);

    protected DiffList(List<V> all, List<V> create, List<V> update, List<V> delete) {
        super(all, create, update, delete);
    }
}
