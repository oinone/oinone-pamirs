package pro.shushi.pamirs.translate.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;


@Slf4j
public class TimeAndMemoryWatcher {

    private TimeAndMemoryWatcher() {}

    public static <T> T watch(Watcher<T> watcher, String... tips) {
        long t = System.currentTimeMillis();

        Runtime r = Runtime.getRuntime();
        long startMem = r.freeMemory(); // 开始Memory

        T result;
        try {
            result = watcher.run();
        } finally {
            long endMem = r.freeMemory(); // 末尾Memory
            t = System.currentTimeMillis() - t;
            log.info("Time consumption: " + t + " ms. " + StringUtils.join(tips, ","));
            log.info("Memory consumption: " + (startMem - endMem) / 1024 + "KB");
        }

        return result;
    }

    public static void watch(Runnable watcher, String... tips) {
        long t = System.currentTimeMillis();

        Runtime r = Runtime.getRuntime();
        long startMem = r.freeMemory(); // 开始Memory

        try {
            watcher.run();
        } finally {
            long endMem = r.freeMemory(); // 末尾Memory
            t = System.currentTimeMillis() - t;
            log.info("Time consumption: " + t + " ms. " + StringUtils.join(tips, ","));
            log.info("Memory consumption: " + (startMem - endMem) / 1024 + "KB");
        }
    }

    public interface Watcher<T> {
        T run();
    }
}