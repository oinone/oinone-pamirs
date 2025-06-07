package pro.shushi.pamirs.core.common.entry;

import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.List;
import java.util.function.Supplier;

/**
 * Value Supplier holder
 *
 * @author Adamancy Zhang at 12:03 on 2024-10-12
 */
public class HoldSupplier<T> {

    private final Holder<T> holder = new Holder<>();

    private final Supplier<T> supplier;

    public HoldSupplier(Supplier<T> supplier) {
        assert supplier != null : "Invalid hold supplier";
        this.supplier = supplier;
    }

    public T get() {
        T value = holder.get();
        if (value == null && holder.isNotSetValue()) {
            synchronized (holder) {
                value = holder.get();
                if (value == null && holder.isNotSetValue()) {
                    value = supplier.get();
                    holder.set(value);
                }
            }
        }
        return value;
    }

    public static <R> HoldSupplier<R> getDefaultExtension(Class<R> clazz) {
        return new HoldSupplier<>(() -> Spider.getDefaultExtension(clazz));
    }

    public static <R> HoldSupplier<List<R>> getOrderedExtensions(Class<R> clazz) {
        return new HoldSupplier<>(() -> Spider.getLoader(clazz).getOrderedExtensions());
    }
}
