package pro.shushi.pamirs.core.common.diff;

/**
 * 差量值
 *
 * @author Adamancy Zhang at 10:59 on 2024-02-19
 */
public class DiffValue<V> {

    protected static final DiffValue<?> EMPTY = new DiffValue<>(null, null, null, null);

    private final V all;

    private final V create;

    private final V update;

    private final V delete;

    private final boolean isAll;

    private final boolean isEmpty;

    protected DiffValue(V all, V create, V update, V delete) {
        this.all = all;
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.isAll = create == null && update == null && delete == null;
        this.isEmpty = this.isAll && all == null;
    }

    public V getAll() {
        return all;
    }

    public V getCreate() {
        return create;
    }

    public V getUpdate() {
        return update;
    }

    public V getDelete() {
        return delete;
    }

    /**
     * 是否为全量值
     *
     * @return 是否为全量值
     */
    public boolean isAll() {
        return isAll;
    }

    /**
     * 是否为差量值
     *
     * @return 是否为差量值
     */
    public boolean isDiff() {
        return !isAll;
    }

    /**
     * 是否为空值
     *
     * @return 是否为空值
     */
    public boolean isEmpty() {
        return isEmpty;
    }
}
