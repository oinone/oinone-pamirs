package pro.shushi.pamirs.meta.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

/**
 * 并行流帮助类
 *
 * @author Adamancy Zhang at 14:09 on 2025-02-21
 */
public class ParallelStreamHelper {

    private static final Logger log = LoggerFactory.getLogger(ParallelStreamHelper.class);

    private static final boolean isSupportParallelStream;

    private static final int MAX_CAP = 0x7fff;

    static {
        boolean enabledParallelStream = PropertyHelper.getBooleanProperty("pamirs.parallel.enabled", true);
        if (enabledParallelStream) {
            int minParallelism = PropertyHelper.getIntProperty("pamirs.parallel.min-parallelism", 2);
            int parallelism = getParallelism();
            if (minParallelism <= 0) {
                isSupportParallelStream = true;
            } else {
                isSupportParallelStream = parallelism >= minParallelism;
            }
            if (isSupportParallelStream) {
                log.info("support parallel stream. parallelism: {}", parallelism);
            }
        } else {
            isSupportParallelStream = false;
        }
    }

    private ParallelStreamHelper() {
        // reject create object
    }

    public static boolean isSupported() {
        return isSupportParallelStream;
    }

    public static <T> Stream<T> parallelStream(Collection<T> collection) {
        if (isSupportParallelStream) {
            return collection.parallelStream();
        }
        return collection.stream();
    }

    /**
     * 参考: {@link ForkJoinPool#makeCommonPool}
     *
     * @return get ForkJoinPool parallelism
     */
    private static int getParallelism() {
        int parallelism = -1;
        try {  // ignore exceptions in accessing/parsing properties
            String pp = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
            if (pp != null) {
                parallelism = Integer.parseInt(pp);
            }
        } catch (Throwable ignored) {
        }
        if (parallelism < 0 && (parallelism = Runtime.getRuntime().availableProcessors() - 1) <= 0) {
            // default 1 less than #cores
            parallelism = 1;
        }
        if (parallelism > MAX_CAP) {
            parallelism = MAX_CAP;
        }
        return parallelism;
    }
}
