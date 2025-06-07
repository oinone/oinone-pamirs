package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.core.common.function.AroundCallable;
import pro.shushi.pamirs.core.common.function.AroundRunnable;
import pro.shushi.pamirs.core.common.function.NoThreadFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 线程环绕执行（自动克隆并清理{@link pro.shushi.pamirs.meta.api.session.PamirsSession}）
 *
 * @author Adamancy Zhang on 2021-03-11 17:54
 */
public class ExecutorHelper {

    private ExecutorHelper() {
        //reject create object
    }

    public static void execute(Executor executor, Runnable runnable) {
        executeTemplateWithoutResult(executor, runnable, executor::execute);
    }

    public static <T> Future<T> submit(ExecutorService executor, Callable<T> callable) {
        return executeTemplate(executor, callable, executor::submit);
    }

    public static <T> List<Future<T>> invokeAll(ExecutorService executor, Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        if (executor == null) {
            List<Future<T>> results = new ArrayList<>(tasks.size());
            for (Callable<T> task : tasks) {
                try {
                    results.add(new NoThreadFuture<>(task.call()));
                } catch (Exception e) {
                    throw new ExecutionException(e);
                }
            }
            return results;
        }
        return executor.invokeAll(tasks.stream().map(AroundCallable::new).collect(Collectors.toList()));
    }

    public static void schedule(ScheduledExecutorService executor, Runnable runnable, long delay, TimeUnit unit) {
        executeTemplateWithoutResult(executor, runnable, aroundRunnable -> executor.schedule(aroundRunnable, delay, unit));
    }

    public static void scheduleWithFixedDelay(ScheduledExecutorService executor, Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        executeTemplateWithoutResult(executor, runnable, aroundRunnable -> executor.scheduleWithFixedDelay(aroundRunnable, initialDelay, delay, unit));
    }

    private static <T> void executeTemplateWithoutResult(T executor, Runnable runnable, Consumer<AroundRunnable> consumer) {
        if (executor == null) {
            runnable.run();
        } else {
            consumer.accept(new AroundRunnable(runnable));
        }
    }

    private static <T, R> Future<R> executeTemplate(T executor, Callable<R> callable, Function<AroundCallable<R>, Future<R>> function) {
        if (executor == null) {
            try {
                return new NoThreadFuture<>(callable.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return function.apply(new AroundCallable<>(callable));
        }
    }
}
