package pro.shushi.pamirs.boot.common.util;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MetaBootCountDown {

    private volatile static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void increment() {
        atomicInteger.incrementAndGet();
    }

    public static void decrement() {
        atomicInteger.decrementAndGet();
    }

    public static void clear() {
        atomicInteger = null;
    }

    public static void judge() {
        while (true) {
            boolean aRs = atomicInteger.compareAndSet(0, 0);
            if (aRs) {
                log.info("All sub-thread tasks execution finished");
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
