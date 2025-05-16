package pro.shushi.pamirs.core.common.function;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

/**
 * @author Adamancy Zhang on 2021-04-22 18:03
 */
public class NoThreadFuture<V> implements Future<V> {

    private final V value;

    public NoThreadFuture(V value) {
        this.value = value;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return value;
    }

    @Override
    public V get(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return value;
    }
}
