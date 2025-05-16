package pro.shushi.pamirs.meta.api.core.orm.systems.orm.tpl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ValuesTemplate<T, TT, R> {

    int size;
    Object d;

    public static <T, TT, R> ValuesTemplate<T, TT, R> getInstance(int size) {
        ValuesTemplate<T, TT, R> container = new ValuesTemplate<>();
        container.size = size;
        return container;
    }

    public void supplier(Supplier<T> singleSupplier, Supplier<TT> multiSupplier) {
        if (1 == size) {
            d = singleSupplier.get();
        } else {
            d = multiSupplier.get();
        }
    }

    @SuppressWarnings("unchecked")
    public void consume(Consumer<T> singleConsumer, Consumer<TT> multiConsumer) {
        if (1 == size) {
            singleConsumer.accept((T) d);
        } else {
            multiConsumer.accept((TT) d);
        }
    }

    @SuppressWarnings("unchecked")
    public R provide(Function<T, R> singleProvider, Function<TT, R> multiProvider) {
        if (1 == size) {
            return singleProvider.apply((T) d);
        } else {
            return multiProvider.apply((TT) d);
        }
    }

}