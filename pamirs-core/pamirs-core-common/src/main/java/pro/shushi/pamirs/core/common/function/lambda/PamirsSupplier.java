package pro.shushi.pamirs.core.common.function.lambda;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <h>Pamirs Supplier</h>
 * <p>
 * 该提供者是将{@link Supplier}和{@link Function}结合的产物
 * </p>
 *
 * @author Adamancy Zhang at 10:40 on 2021-06-11
 */
public interface PamirsSupplier<T> extends Supplier<T> {

    /**
     * 类型转换
     *
     * @param after 获得对象后进行类型转换
     * @return 新的提供者
     */
    default <V> PamirsSupplier<V> andThen(Function<? super T, V> after) {
        return () -> after.apply(get());
    }
}
