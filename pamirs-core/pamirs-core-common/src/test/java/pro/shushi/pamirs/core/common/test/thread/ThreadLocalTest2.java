package pro.shushi.pamirs.core.common.test.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Adamancy Zhang at 19:34 on 2025-04-01
 */
@Slf4j
public class ThreadLocalTest2 {

    private static final TransmittableThreadLocal<Map<String, String>> storage = new TransmittableThreadLocal<>();

    private static final ThreadLocal<Boolean> isInit = new ThreadLocal<>();

    @Test
    public void test() throws InterruptedException {
        List<String> keys = Lists.newArrayList("a", "b", "c");
        CountDownLatch latch = new CountDownLatch(keys.size());
        for (String key : keys) {
            TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
                try {
                    Thread.sleep(50);
                    System.out.println(ThreadLocalTest2.get(key));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    private static String get(String key) {
        Map<String, String> map = storage.get();
        if (map == null) {
            if (Boolean.TRUE.equals(isInit.get())) {
                return null;
            }
            synchronized (ThreadLocalTest2.class) {
                map = storage.get();
                if (map == null) {
                    log.info("init");
                    isInit.set(true);
                    try {
                        map = new HashMap<>();
                        map.put("a", "a");
                        map.put("b", "b");
                        map.put("c", "c");
                    } catch (Throwable e) {
                        log.error("resolve error.", e);
                        map = new HashMap<>();
                    }
                    storage.set(map);
                    isInit.remove();
                }
            }
        }
        return map.get(key);
    }
}
