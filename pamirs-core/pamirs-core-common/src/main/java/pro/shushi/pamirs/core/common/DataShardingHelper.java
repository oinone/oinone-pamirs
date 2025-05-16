package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据分片帮助类（异步支持）
 *
 * @author Adamancy Zhang at 15:36 on 2023-12-02
 */
public class DataShardingHelper extends pro.shushi.pamirs.framework.common.utils.DataShardingHelper {

    protected DataShardingHelper(int eachShardMax) {
        super(eachShardMax);
    }

    public static DataShardingHelper build() {
        return new DataShardingHelper(DEFAULT_EACH_SHARD_MAX);
    }

    public static DataShardingHelper build(int shardingMax) {
        return new DataShardingHelper(shardingMax);
    }

    /**
     * 使用异步处理
     *
     * @return 异步分片帮助类
     */
    public <R> Async<R> async() {
        return new Async<>(this);
    }

    public static class Async<R> {

        private final DataShardingHelper origin;

        private boolean usingExecutorHelper;

        private ExecutorService executorService;

        private long timeout;

        private TimeUnit unit;

        private Async(DataShardingHelper origin) {
            this.origin = origin;
            ExecutorService executorService = TtlAsyncTaskExecutor.getExecutorService();
            if (executorService == null) {
                this.executorService = BeanDefinitionUtils.getBean(ExecutorService.class);
                this.usingExecutorHelper = true;
            } else {
                this.executorService = executorService;
                this.usingExecutorHelper = false;
            }
            this.timeout = 1L;
            this.unit = TimeUnit.MINUTES;
        }

        public Async<R> usingExecutorHelper(boolean usingExecutorHelper) {
            this.usingExecutorHelper = usingExecutorHelper;
            return this;
        }

        public Async<R> setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Async<R> timeout(long timeout, TimeUnit unit) {
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        public <T> List<R> sharding(List<T> list, Function<List<T>, List<R>> function) throws ExecutionException, InterruptedException, TimeoutException {
            List<Future<List<R>>> futures = invokeAll(origin.sharding(list).stream()
                    .map(v -> (Callable<List<R>>) () -> function.apply(v)).collect(Collectors.toList()));
            List<R> result = new ArrayList<>(list.size());
            for (Future<List<R>> future : futures) {
                result.addAll(await(future));
            }
            return result;
        }

        public List<R> sharding(int total, ShardingFetcher<R> fetcher) throws ExecutionException, InterruptedException, TimeoutException {
            EachShardCount eachShardCount = origin.computeEachShardCount(total);
            int batch = eachShardCount.batch, begin = 0, step = eachShardCount.basic, end = step;
            List<AsyncShardingFetcher<R>> callables = new ArrayList<>();
            int bc = 1;
            do {
                callables.add(new AsyncShardingFetcher<>(fetcher, begin, end, bc, step));
                begin += step;
                bc++;
                if (bc == batch) {
                    callables.add(new AsyncShardingFetcher<>(fetcher, begin, total, bc, total - begin));
                    break;
                }
                end += step;
            } while (end < total);
            List<Future<List<R>>> futures = invokeAll(callables);
            List<R> result = new ArrayList<>(total);
            for (Future<List<R>> future : futures) {
                result.addAll(await(future));
            }
            return result;
        }

        public <T> FutureCollection<R> lazySharding(List<T> list, Function<List<T>, List<R>> function) throws ExecutionException, InterruptedException {
            return new FutureCollection<>(this, list.size(),
                    invokeAll(origin.sharding(list).stream().map(v -> (Callable<List<R>>) () -> function.apply(v)).collect(Collectors.toList())));
        }

        public <T> List<R> collectionSharding(Collection<T> list, Function<Collection<T>, List<R>> function) throws ExecutionException, InterruptedException, TimeoutException {
            List<Future<List<R>>> futures = invokeAll(origin.collectionSharding(list).stream()
                    .map(v -> (Callable<List<R>>) () -> function.apply(v)).collect(Collectors.toList()));
            List<R> result = new ArrayList<>(list.size());
            for (Future<List<R>> future : futures) {
                result.addAll(await(future));
            }
            return result;
        }

        public <T> FutureCollection<R> lazyCollectionSharding(Collection<T> list, Function<Collection<T>, List<R>> function) throws ExecutionException, InterruptedException {
            return new FutureCollection<>(this, list.size(),
                    invokeAll(origin.collectionSharding(list).stream().map(v -> (Callable<List<R>>) () -> function.apply(v)).collect(Collectors.toList())));
        }

        private <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
            if (this.usingExecutorHelper) {
                return ExecutorHelper.invokeAll(this.executorService, tasks);
            }
            return this.executorService.invokeAll(tasks);
        }

        protected <T> T await(Future<T> future) throws ExecutionException, InterruptedException, TimeoutException {
            if (this.timeout == -1) {
                return future.get();
            }
            return future.get(this.timeout, this.unit);
        }
    }

    public static class FutureCollection<T> {

        private final Async<T> origin;

        private final int initialCapacity;

        private final List<Future<List<T>>> futures;

        public FutureCollection(Async<T> origin, int initialCapacity, List<Future<List<T>>> futures) {
            this.origin = origin;
            this.initialCapacity = initialCapacity;
            this.futures = futures;
        }

        public List<T> collect() throws InterruptedException, ExecutionException, TimeoutException {
            List<T> result = new ArrayList<>(this.initialCapacity);
            for (Future<List<T>> future : futures) {
                result.addAll(origin.await(future));
            }
            return result;
        }
    }

    private static class AsyncShardingFetcher<T> implements Callable<List<T>> {

        private final ShardingFetcher<T> fetcher;

        private final int begin;

        private final int end;

        private final int page;

        private final int size;

        public AsyncShardingFetcher(ShardingFetcher<T> fetcher, int begin, int end, int page, int size) {
            this.fetcher = fetcher;
            this.begin = begin;
            this.end = end;
            this.page = page;
            this.size = size;
        }

        @Override
        public List<T> call() throws Exception {
            return fetcher.apply(begin, end, page, size);
        }
    }
}
