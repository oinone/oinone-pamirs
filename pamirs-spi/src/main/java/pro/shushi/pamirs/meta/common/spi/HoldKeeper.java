package pro.shushi.pamirs.meta.common.spi;

import java.util.function.Supplier;

/**
 * Holder容器管理者
 * 2021/9/17 1:30 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class HoldKeeper<T> {

    private final Holder<T> holder = new Holder<>();

    public T supply(Supplier<T> supplier) {
        T spi = holder.get();
        if (null == spi) {
            synchronized (holder) {
                spi = holder.get();
                if (null == spi) {
                    spi = supplier.get();
                    holder.set(spi);
                }
            }
        }
        return spi;
    }

}
