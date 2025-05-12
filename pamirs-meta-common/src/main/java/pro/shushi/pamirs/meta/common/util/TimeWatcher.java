package pro.shushi.pamirs.meta.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 时间观察器
 * <p>
 * 2020/4/17 12:44 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class TimeWatcher {

    private final static Logger logger = LoggerFactory.getLogger(TimeWatcher.class);

    public static <T> T watch(Watcher<T> watcher, String... tips) {
        long t = System.currentTimeMillis();
        try {
            return watcher.run();
        } finally {
            t = System.currentTimeMillis() - t;
            logger.info("request time:" + t + " ms." + StringUtils.join(tips, CharacterConstants.SEPARATOR_COMMA));
        }
    }

    public static void watch(WatcherWithoutResult watcher, String... tips) {
        long t = System.currentTimeMillis();
        try {
            watcher.run();
        } finally {
            t = System.currentTimeMillis() - t;
            logger.info("request time:" + t + " ms." + StringUtils.join(tips, CharacterConstants.SEPARATOR_COMMA));
        }
    }

    @SuppressWarnings("unused")
    public static <T> T watchNano(Watcher<T> watcher, String... tips) {
        long t = System.nanoTime();
        try {
            return watcher.run();
        } finally {
            t = System.nanoTime() - t;
            logger.info("request time:" + t + " ns." + StringUtils.join(tips, CharacterConstants.SEPARATOR_COMMA));
        }
    }

    @SuppressWarnings("unused")
    public static void watchNano(WatcherWithoutResult watcher, String... tips) {
        long t = System.nanoTime();
        try {
            watcher.run();
        } finally {
            t = System.nanoTime() - t;
            logger.info("request time:" + t + " ns." + StringUtils.join(tips, CharacterConstants.SEPARATOR_COMMA));
        }
    }

    public interface Watcher<T> {
        T run();
    }

    public interface WatcherWithoutResult {
        void run();
    }

}
