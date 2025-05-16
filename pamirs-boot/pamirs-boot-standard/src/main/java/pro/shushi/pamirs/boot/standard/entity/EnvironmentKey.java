package pro.shushi.pamirs.boot.standard.entity;

import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 环境Key
 *
 * @author Adamancy Zhang at 18:48 on 2024-10-12
 */
public class EnvironmentKey {

    private static final Map<String, EnvironmentKey> cache = new HashMap<>();

    private final String key;

    private final String defaultValue;

    private final String message;

    private final Level level;

    private final Checker checker;

    private EnvironmentKey(String key, String defaultValue, String message, Level level, Checker checker) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.message = message;
        this.level = level;
        this.checker = checker;
    }

    private static EnvironmentKey of(Level level, String key, String defaultValue, String message, Checker checker) {
        EnvironmentKey cached = EnvironmentKey.cache.get(key);
        if (cached == null || (!Level.NONE.equals(level) && Level.NONE.equals(cached.getLevel()))) {
            cached = new EnvironmentKey(key, defaultValue, message, level, checker);
            EnvironmentKey.cache.put(key, cached);
        }
        return cached;
    }

    public static EnvironmentKey of(String key) {
        return of(Level.NONE, key, null, null, null);
    }

    public static EnvironmentKey immutable(String key) {
        return of(Level.IMMUTABLE, key, null, Level.IMMUTABLE.getMessage(), null);
    }

    public static EnvironmentKey immutable(String key, Checker checker) {
        return of(Level.IMMUTABLE, key, null, Level.IMMUTABLE.getMessage(), checker);
    }

    public static EnvironmentKey immutable(String key, String defaultValue) {
        return of(Level.IMMUTABLE, key, defaultValue, Level.IMMUTABLE.getMessage(), null);
    }

    public static EnvironmentKey immutable(String key, String defaultValue, String message) {
        return of(Level.IMMUTABLE, key, defaultValue, message, null);
    }

    public static EnvironmentKey immutable(String key, String defaultValue, String message, Checker checker) {
        return of(Level.IMMUTABLE, key, defaultValue, message, checker);
    }

    public static EnvironmentKey add(String key) {
        return of(Level.ADD, key, null, Level.ADD.getMessage(), null);
    }

    public static EnvironmentKey add(String key, Checker checker) {
        return of(Level.ADD, key, null, Level.ADD.getMessage(), checker);
    }

    public static EnvironmentKey add(String key, String defaultValue) {
        return of(Level.ADD, key, defaultValue, Level.ADD.getMessage(), null);
    }

    public static EnvironmentKey add(String key, String defaultValue, String message) {
        return of(Level.ADD, key, defaultValue, message, null);
    }

    public static EnvironmentKey add(String key, String defaultValue, String message, Checker checker) {
        return of(Level.ADD, key, defaultValue, message, checker);
    }

    public static EnvironmentKey addOrDelete(String key) {
        return of(Level.ADD_OR_DELETE, key, null, Level.ADD_OR_DELETE.getMessage(), null);
    }

    public static EnvironmentKey addOrDelete(String key, Checker checker) {
        return of(Level.ADD_OR_DELETE, key, null, Level.ADD_OR_DELETE.getMessage(), checker);
    }

    public static EnvironmentKey addOrDelete(String key, String defaultValue) {
        return of(Level.ADD_OR_DELETE, key, defaultValue, Level.ADD_OR_DELETE.getMessage(), null);
    }

    public static EnvironmentKey addOrDelete(String key, String defaultValue, String message) {
        return of(Level.ADD_OR_DELETE, key, defaultValue, message, null);
    }

    public static EnvironmentKey addOrDelete(String key, String defaultValue, String message, Checker checker) {
        return of(Level.ADD_OR_DELETE, key, defaultValue, message, checker);
    }

    public static EnvironmentKey error(String key) {
        return of(Level.ERROR, key, null, Level.ERROR.getMessage(), null);
    }

    public static EnvironmentKey error(String key, Checker checker) {
        return of(Level.ERROR, key, null, Level.ERROR.getMessage(), checker);
    }

    public static EnvironmentKey error(String key, String defaultValue) {
        return of(Level.ERROR, key, defaultValue, Level.ERROR.getMessage(), null);
    }

    public static EnvironmentKey error(String key, String defaultValue, String message) {
        return of(Level.ERROR, key, defaultValue, message, null);
    }

    public static EnvironmentKey error(String key, String defaultValue, String message, Checker checker) {
        return of(Level.ERROR, key, defaultValue, message, checker);
    }

    public static EnvironmentKey warning(String key) {
        return of(Level.WARNING, key, null, Level.WARNING.getMessage(), null);
    }

    public static EnvironmentKey warning(String key, Checker checker) {
        return of(Level.WARNING, key, null, Level.WARNING.getMessage(), checker);
    }

    public static EnvironmentKey warning(String key, String defaultValue) {
        return of(Level.WARNING, key, defaultValue, Level.WARNING.getMessage(), null);
    }

    public static EnvironmentKey warning(String key, String defaultValue, String message) {
        return of(Level.WARNING, key, defaultValue, message, null);
    }

    public static EnvironmentKey warning(String key, String defaultValue, String message, Checker checker) {
        return of(Level.WARNING, key, defaultValue, message, checker);
    }

    public static EnvironmentKey deprecated(String key) {
        return of(Level.DEPRECATED, key, null, Level.DEPRECATED.getMessage(), null);
    }

    public static EnvironmentKey deprecated(String key, Checker checker) {
        return of(Level.DEPRECATED, key, null, Level.DEPRECATED.getMessage(), checker);
    }

    public static EnvironmentKey deprecated(String key, String defaultValue) {
        return of(Level.DEPRECATED, key, defaultValue, Level.DEPRECATED.getMessage(), null);
    }

    public static EnvironmentKey deprecated(String key, String defaultValue, String message) {
        return of(Level.DEPRECATED, key, defaultValue, message, null);
    }

    public static EnvironmentKey deprecated(String key, String defaultValue, String message, Checker checker) {
        return of(Level.DEPRECATED, key, defaultValue, message, checker);
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getMessage() {
        return message;
    }

    public Level getLevel() {
        return level;
    }

    public Checker getChecker() {
        return checker;
    }

    public static void clear() {
        cache.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentKey)) {
            return false;
        }
        EnvironmentKey that = (EnvironmentKey) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    /**
     * 环境信息检查
     */
    @FunctionalInterface
    public interface Checker {

        /**
         * 环境信息变更检查
         *
         * @param context            环境检查上下文
         * @param currentEnvironment 当前环境信息
         * @param historyEnvironment 历史环境信息
         * @return 检查通过后最终的环境信息
         */
        PlatformEnvironment check(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment);

        /**
         * 环境信息转换
         *
         * @param currentEnvironment 当前环境信息
         * @return 转换后的环境信息
         */
        default PlatformEnvironment convert(PlatformEnvironment currentEnvironment) {
            return currentEnvironment;
        }

        /**
         * 新增环境信息检查
         *
         * @param context            环境检查上下文
         * @param currentEnvironment 当前环境信息
         * @return 检查通过后最终的环境信息
         */
        default PlatformEnvironment checkNewEnvironment(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
            return currentEnvironment;
        }

        /**
         * 删除环境信息检查
         *
         * @param context            环境检查上下文
         * @param historyEnvironment 历史环境信息
         * @return 检查通过后最终的环境信息
         */
        default PlatformEnvironment checkDeleteEnvironment(EnvironmentCheckContext context, PlatformEnvironment historyEnvironment) {
            return historyEnvironment;
        }
    }

    public enum Level {
        /**
         * 无任何含义
         */
        NONE(null),
        /**
         * 不可变
         * <p>
         * 1. null -> not null: false<br>
         * 2. not null -> null: false<br>
         * 3. a -> b: false
         * </p>
         */
        IMMUTABLE(null),
        /**
         * 可新增
         * <p>
         * 1. null -> not null: true<br>
         * 2. not null -> null: false<br>
         * 3. a -> b: false
         * </p>
         */
        ADD(null),
        /**
         * 可新增或删除
         * <p>
         * 1. null -> not null: true<br>
         * 2. not null -> null: true<br>
         * 3. a -> b: false
         * </p>
         */
        ADD_OR_DELETE(null),
        /**
         * 不可配置
         */
        ERROR("不允许配置该参数"),
        /**
         * 警告提示
         */
        WARNING("参数发生变化，可能会造成无法预知的异常"),
        /**
         * 废弃提示
         */
        DEPRECATED("该参数已被废弃，请移除配置");

        private final String message;

        Level(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public static boolean isAllowSaveEnvironment(Level level) {
            return Level.IMMUTABLE.equals(level) ||
                    Level.WARNING.equals(level) ||
                    Level.ADD.equals(level) ||
                    Level.ADD_OR_DELETE.equals(level);
        }

        public static boolean isNotAllowDeleteEnvironment(Level level) {
            return Level.IMMUTABLE.equals(level) || Level.ADD.equals(level);
        }
    }
}
