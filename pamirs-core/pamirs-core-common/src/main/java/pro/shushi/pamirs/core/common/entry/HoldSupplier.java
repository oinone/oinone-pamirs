package pro.shushi.pamirs.core.common.entry;

import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.List;
import java.util.function.Supplier;

/**
 * Value Supplier holder
 *
 * @author Adamancy Zhang at 12:03 on 2024-10-12
 */
@Deprecated
public class HoldSupplier<T> extends pro.shushi.pamirs.ux.common.entity.HoldSupplier<T> {

    public HoldSupplier(Supplier<T> supplier) {
        super(supplier);
    }

    public static <R> HoldSupplier<R> getDefaultExtension(Class<R> clazz) {
        return new HoldSupplier<>(() -> Spider.getDefaultExtension(clazz));
    }

    public static <R> HoldSupplier<List<R>> getOrderedExtensions(Class<R> clazz) {
        return new HoldSupplier<>(() -> Spider.getLoader(clazz).getOrderedExtensions());
    }
}
