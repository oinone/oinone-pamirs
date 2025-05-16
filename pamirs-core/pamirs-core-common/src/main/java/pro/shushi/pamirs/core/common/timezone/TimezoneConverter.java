package pro.shushi.pamirs.core.common.timezone;

import pro.shushi.pamirs.core.common.directive.Directive;
import pro.shushi.pamirs.core.common.directive.DirectiveHelper;

import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static pro.shushi.pamirs.core.common.FetchUtil.cast;

/**
 * <h>时区转换器</h>
 * <p>
 * 当前时区转换器受支持的时间类型如下:<br>
 * - {@link java.sql.Timestamp}<br>
 * - {@link java.sql.Date}<br>
 * - {@link java.sql.Time}<br>
 * - {@link java.util.Date}<br>
 * - {@link Long}<br>
 * </p>
 *
 * @author Adamancy Zhang at 11:00 on 2021-09-03
 */
public class TimezoneConverter {

    /**
     * 当前时间所在时区
     */
    private final TimeZone from;

    /**
     * 目标时间所在时区
     */
    private final TimeZone to;

    /**
     * 特性指令
     */
    private final int directive;

    private TimezoneConverter(TimeZone from, TimeZone to, Feature... features) {
        this.from = from;
        this.to = to;
        this.directive = DirectiveHelper.enable(features);
    }

    /**
     * 构造从系统默认时区到指定时区的转换器
     *
     * @param to       目标时间所在时区
     * @param features 特性
     * @return 时区转换器
     */
    public static TimezoneConverter newInstance(TimeZone to, Feature... features) {
        return new TimezoneConverter(TimeZone.getDefault(), to, features);
    }

    /**
     * 构造从指定时区到指定时区的转换器
     *
     * @param from     当前时间所在时区
     * @param to       目标时间所在时区
     * @param features 特性
     * @return 时区转换器
     */
    public static TimezoneConverter newInstance(TimeZone from, TimeZone to, Feature... features) {
        return new TimezoneConverter(from, to, features);
    }

    /**
     * 受支持的值类型
     *
     * @param date 日期值
     * @return 是否受支持
     */
    public static boolean isSupported(Object date) {
        return date instanceof java.util.Date || date instanceof Long;
    }

    /**
     * 时间时区转换
     *
     * @param date 任意时间对象。
     * @param <T>  受支持的时间类型，详见下方列表。
     * @return 目标时间
     * @throws IllegalArgumentException 不受支持的时间类型
     */
    public <T> T convert(T date) {
        return convert(date, () -> {
            throw new IllegalArgumentException("Invalid date type. value=" + date.getClass());
        });
    }

    /**
     * 时间时区转换（无法转换时返回空值）
     *
     * @param date 任意时间对象
     * @param <T>  受支持的时间类型
     * @return 目标时间
     */
    public <T> T convertWithoutException(T date) {
        return convert(date, () -> null);
    }

    /**
     * 时间时区转换
     *
     * @param date            任意时间对象
     * @param defaultSupplier 无法进行时区转换的默认值提供者
     * @param <T>             受支持的时间类型
     * @return 目标时间
     */
    public <T> T convert(T date, Supplier<T> defaultSupplier) {
        if (date instanceof java.sql.Timestamp) {
            return cast(convert((java.sql.Timestamp) date, ((java.sql.Timestamp) date).getTime(), (d, t) -> {
                if (DirectiveHelper.isEnabled(directive, Feature.NEW_INSTANCE)) {
                    return new java.sql.Timestamp(t);
                } else {
                    d.setTime(t);
                    return d;
                }
            }));
        } else if (date instanceof java.sql.Date) {
            return cast(convert((java.sql.Date) date, ((java.sql.Date) date).getTime(), (d, t) -> {
                if (DirectiveHelper.isEnabled(directive, Feature.NEW_INSTANCE)) {
                    return new java.sql.Date(t);
                } else {
                    d.setTime(t);
                    return d;
                }
            }));
        } else if (date instanceof java.sql.Time) {
            return cast(convert((java.sql.Time) date, ((java.sql.Time) date).getTime(), (d, t) -> {
                if (DirectiveHelper.isEnabled(directive, Feature.NEW_INSTANCE)) {
                    return new java.sql.Time(t);
                } else {
                    d.setTime(t);
                    return d;
                }
            }));
        } else if (date instanceof java.util.Date) {
            return cast(convert((java.util.Date) date, ((java.util.Date) date).getTime(), (d, t) -> {
                if (DirectiveHelper.isEnabled(directive, Feature.NEW_INSTANCE)) {
                    return new java.util.Date(t);
                } else {
                    d.setTime(t);
                    return d;
                }
            }));
        } else if (date instanceof Long) {
            return cast(compute((Long) date));
        } else {
            return defaultSupplier.get();
        }
    }

    /**
     * 时间时区转换（自定义重置时间函数）
     *
     * @param date       任意时间对象
     * @param timeMillis 时间毫秒数
     * @param resetTime  重置时间函数
     * @param <T>        受支持的时间类型
     * @return 目标时间
     */
    public <T> T convert(T date, long timeMillis, BiFunction<T, Long, T> resetTime) {
        return resetTime.apply(date, compute(timeMillis));
    }

    /**
     * 时间毫秒数时区转换
     *
     * @param timeMillis 时间毫秒数
     * @return 目标时间毫秒数
     */
    public long compute(long timeMillis) {
        return timeMillis - getOffset(timeMillis);
    }

    /**
     * 获取指定时间毫秒数的偏移量
     *
     * @param timeMillis 时间毫秒数
     * @return 指定时间毫秒数的偏移量
     */
    public long getOffset(long timeMillis) {
        return from.getOffset(timeMillis) - to.getOffset(timeMillis);
    }

    /**
     * 特性
     */
    public enum Feature implements Directive {

        /**
         * 构造新对象
         */
        NEW_INSTANCE(1);

        private final int intValue;

        Feature(int intValue) {
            this.intValue = intValue;
        }

        @Override
        public int intValue() {
            return intValue;
        }
    }
}
