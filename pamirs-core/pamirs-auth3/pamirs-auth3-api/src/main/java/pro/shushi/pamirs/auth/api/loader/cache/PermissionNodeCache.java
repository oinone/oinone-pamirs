package pro.shushi.pamirs.auth.api.loader.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.core.common.ExecutorHelper;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.common.config.ScheduledTaskExecutorConfiguration;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.HealthCheckController;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 动作权限节点缓存
 *
 * @author Adamancy Zhang at 10:33 on 2024-09-13
 */
@Slf4j
@Component
public class PermissionNodeCache {

    private static final Cache<String, CacheResult<?>> cache;

    private static AtomicBoolean usingLoadCache;

    @Autowired(required = false)
    @Qualifier(ScheduledTaskExecutorConfiguration.SCHEDULED_THREAD_POOL_EXECUTOR)
    private ScheduledExecutorService globalScheduledThreadPoolExecutor;

    static {
        cache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    @PostConstruct
    public void postConstruct() {
        if (globalScheduledThreadPoolExecutor == null) {
            log.warn("flush permission node cache is disabled.");
            return;
        }
        ExecutorHelper.scheduleWithFixedDelay(globalScheduledThreadPoolExecutor, () -> {
            if (HealthCheckController.imok()) {
                PermissionNodeCache.flushAll();
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    public static boolean isUsingLoadCache() {
        if (usingLoadCache == null) {
            synchronized (PermissionNodeCache.class) {
                if (usingLoadCache == null) {
                    usingLoadCache = new AtomicBoolean(BeanDefinitionUtils.getBean(PermissionNodeLoader.class).isUsingLoadCache());
                }
            }
        }
        return usingLoadCache.get();
    }

    @SuppressWarnings("unchecked")
    public static <R extends PermissionNode, T extends R> List<R> get(String key, Supplier<List<T>> supplier) {
        if (SceneAnalysisDebugTraceApi.isDebug()) {
            return (List<R>) supplier.get();
        }
        if (isUsingLoadCache()) {
            CacheResult<R> result = (CacheResult<R>) cache.get(key, (k) -> new CacheResult<>(supplier.get()));
            if (result == null) {
                return new ArrayList<>();
            }
            return ObjectUtils.clone(result.getNodes());
        }
        return (List<R>) supplier.get();
    }

    public static <T extends PermissionNode> void put(String key, Supplier<List<T>> supplier) {
        cache.put(key, new CacheResult<>(supplier.get()));
    }

    public static void flushAll() {
        Boolean isAdmin = PamirsSession.isAdmin();
        PamirsSession.setIsAdmin(Boolean.TRUE);
        try {
            if (log.isDebugEnabled()) {
                long start = System.currentTimeMillis();
                BeanDefinitionUtils.getBean(PermissionNodeLoader.class).getManagementCacheLoader().buildAllPermissions(Collections.emptySet());
                log.debug("flush permission node cache cost time: {}", System.currentTimeMillis() - start);
            } else {
                BeanDefinitionUtils.getBean(PermissionNodeLoader.class).getManagementCacheLoader().buildAllPermissions(Collections.emptySet());
            }
        } catch (Throwable e) {
            log.error("flush permission node cache error.", e);
        } finally {
            PamirsSession.setIsAdmin(isAdmin);
        }
    }

    private static class CacheResult<T extends PermissionNode> {

        private final List<T> nodes;

        public CacheResult(List<T> nodes) {
            for (T node : ObjectUtils.clone(nodes)) {
                clear(node);
            }
            this.nodes = nodes;
        }

        private void clear(PermissionNode node) {
            node.setCanAccess(null);
            node.setCanManagement(null);
            node.setCanAllot(null);
            List<PermissionNode> children = node.getNodes();
            if (CollectionUtils.isNotEmpty(children)) {
                for (PermissionNode child : children) {
                    clear(child);
                }
            }
        }

        public List<T> getNodes() {
            return nodes;
        }
    }
}
