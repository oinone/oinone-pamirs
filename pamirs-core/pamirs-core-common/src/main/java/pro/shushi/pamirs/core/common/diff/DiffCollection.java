package pro.shushi.pamirs.core.common.diff;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 差量集合
 *
 * @author Adamancy Zhang at 20:25 on 2024-01-17
 */
public class DiffCollection<V, C extends Collection<V>> {

    private final C all;

    private final C create;

    private final C update;

    private final C delete;

    private final boolean isAll;

    private final boolean isEmpty;

    protected DiffCollection(C all, C create, C update, C delete) {
        this.all = all;
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.isAll = CollectionUtils.isEmpty(create) && CollectionUtils.isEmpty(update) && CollectionUtils.isEmpty(delete);
        this.isEmpty = this.isAll && CollectionUtils.isEmpty(all);
    }

    public C getAll() {
        return all;
    }

    public C getCreate() {
        return create;
    }

    public C getUpdate() {
        return update;
    }

    public C getDelete() {
        return delete;
    }

    /**
     * 是否为全量集合
     *
     * @return 是否为全量集合
     */
    public boolean isAll() {
        return isAll;
    }

    /**
     * 是否为差量集合
     *
     * @return 是否为差量集合
     */
    public boolean isDiff() {
        return !isAll;
    }

    /**
     * 是否为空集合
     *
     * @return 是否为空集合
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    public static <T> DiffList<T> list(List<T> all, List<T> create, List<T> update, List<T> delete) {
        return new DiffList<>(all, create, update, delete);
    }

    public static <T> DiffList<T> list(List<T> create, List<T> update, List<T> delete) {
        return new DiffList<>(null, create, update, delete);
    }

    public static <T> DiffList<T> list(List<T> all) {
        return new DiffList<>(all, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> DiffList<T> emptyList() {
        return (DiffList<T>) DiffList.EMPTY;
    }

    public static <T> DiffSet<T> set(Set<T> all, Set<T> create, Set<T> update, Set<T> delete) {
        return new DiffSet<>(all, create, update, delete);
    }

    public static <T> DiffSet<T> set(Set<T> create, Set<T> update, Set<T> delete) {
        return new DiffSet<>(null, create, update, delete);
    }

    public static <T> DiffSet<T> set(Set<T> all) {
        return new DiffSet<>(all, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> DiffSet<T> emptySet() {
        return (DiffSet<T>) DiffSet.EMPTY;
    }

    public static <T> DiffValue<T> value(T all, T create, T update, T delete) {
        return new DiffValue<>(all, create, update, delete);
    }

    public static <T> DiffValue<T> value(T create, T update, T delete) {
        return new DiffValue<>(null, create, update, delete);
    }

    public static <T> DiffValue<T> value(T all) {
        return new DiffValue<>(all, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> DiffValue<T> emptyValue() {
        return (DiffValue<T>) DiffValue.EMPTY;
    }
}
