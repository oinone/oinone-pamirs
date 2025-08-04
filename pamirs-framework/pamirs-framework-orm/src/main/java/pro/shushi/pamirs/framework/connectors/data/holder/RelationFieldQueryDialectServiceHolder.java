package pro.shushi.pamirs.framework.connectors.data.holder;

import com.alibaba.ttl.threadpool.TtlExecutors;
import pro.shushi.pamirs.framework.common.config.PamirsGlobalThreadConfig;
import pro.shushi.pamirs.framework.common.config.PamirsThreadFactory;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.RelationFieldQueryDialectService;
import pro.shushi.pamirs.meta.common.spi.Spider;

import jakarta.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 关联字段查询方言服务持有者
 *
 * @author Adamancy Zhang at 19:06 on 2024-10-18
 */
public class RelationFieldQueryDialectServiceHolder {

    private static final Map<String, RelationFieldQueryDialectService> CACHE = new ConcurrentHashMap<>();

    private static RelationFieldQueryDialectService DEFAULT_RELATION_FIELD_QUERY_SERVICE;

    public static RelationFieldQueryDialectService get(String dsKey) {
        return CACHE.computeIfAbsent(dsKey, (k) -> {
            RelationFieldQueryDialectService executor = Dialects.component(RelationFieldQueryDialectService.class, k);
            if (executor == null) {
                return getDefaultService();
            }
            return executor;
        });
    }

    public static RelationFieldQueryDialectService getDefaultService() {
        if (DEFAULT_RELATION_FIELD_QUERY_SERVICE == null) {
            synchronized (RelationFieldQueryDialectServiceHolder.class) {
                if (DEFAULT_RELATION_FIELD_QUERY_SERVICE == null) {
                    DEFAULT_RELATION_FIELD_QUERY_SERVICE = Spider.getDefaultExtension(RelationFieldQueryDialectService.class);
                }
            }
        }
        return DEFAULT_RELATION_FIELD_QUERY_SERVICE;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    private static final ExecutorService executorService;

    static {
        int nThreads = nThreads();
        executorService = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new RelationQueryThreadFactory()));
    }

    public static int nThreads() {
        return Math.max(PamirsThreadFactory.getAvailableProcessors() * 2, PamirsGlobalThreadConfig.GLOBAL_RELATION_QUERY_ASYNC_EXECUTOR_THREAD_COUNT);
    }

    private static class RelationQueryThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(@Nonnull Runnable r) {
            Thread t = new Thread(r);
            t.setName("p.s.p.f.o.d.RelationFieldQueryDialectService");
            return t;
        }
    }
}
