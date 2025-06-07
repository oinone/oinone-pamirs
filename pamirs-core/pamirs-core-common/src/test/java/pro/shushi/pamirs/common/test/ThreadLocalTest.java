package pro.shushi.pamirs.common.test;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Adamancy Zhang at 22:50 on 2024-08-28
 */
@DisplayName("Thread Local Test")
public class ThreadLocalTest {

    private static final TransmittableThreadLocal<Map<String, Object>> ttl = new TransmittableThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> tl = new ThreadLocal<>();

    @Test
    public void test1() {
        ThreadPoolExecutor executor = itemAsyncExecutor();

        ttl.set(new HashMap<>());

        executor.submit(() -> {
            System.out.println(ttl.get().size());
        });

        executor.shutdown();
    }

    private ThreadPoolExecutor itemAsyncExecutor() {
        return new ThreadPoolExecutor(20, 50,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("itemAsyncExecutor"),
                (r, executor) -> {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void test2() throws InterruptedException {
        init();

        print("main thread");

        CountDownLatch countDown = new CountDownLatch(1);

        TtlAsyncTaskExecutor.getExecutorService().submit(() -> {
            print("sub thread remove before");
            remove();
            print("sub thread remove after");
            countDown.countDown();
        });

        countDown.await();

        TtlAsyncTaskExecutor.getExecutorService().shutdown();

        print("main thread");
    }

    @Test
    public void test3() throws InterruptedException {

        CountDownLatch countDown = new CountDownLatch(1);

        TtlAsyncTaskExecutor.getExecutorService().submit(() -> {
            init();
            print("sub thread");
            countDown.countDown();
        });

        countDown.await();

        print("main thread");

        remove();

        TtlAsyncTaskExecutor.getExecutorService().shutdown();

        print("main thread");
    }

    private void init() {
        if (ttl.get() == null) {
            Map<String, Object> ttlCache = new HashMap<>();
            ttlCache.put("a", "1");
            ttl.set(ttlCache);
        }

        if (tl.get() == null) {
            Map<String, Object> tlCache = new HashMap<>();
            tlCache.put("b", "1");
            tl.set(tlCache);
        }
    }

    private void remove() {
        Optional.ofNullable(ttl.get()).ifPresent(v -> v.remove("a"));
        Optional.ofNullable(tl.get()).ifPresent(v -> v.remove("b"));
    }

    private void print(String title) {
        System.out.println(title + " - ttl: " + Optional.ofNullable(ttl.get()).map(v -> v.get("a")).orElse(null));
        System.out.println(title + " - tl: " + Optional.ofNullable(tl.get()).map(v -> v.get("b")).orElse(null));
    }
}
