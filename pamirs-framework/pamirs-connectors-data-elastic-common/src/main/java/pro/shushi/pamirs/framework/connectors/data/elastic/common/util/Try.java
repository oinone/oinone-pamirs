package pro.shushi.pamirs.framework.connectors.data.elastic.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Try
 *
 * @param <T> 入参
 * @param <R> 出参
 * @param <E> 异常
 * @author yakir
 */
@FunctionalInterface
public interface Try<T, R, E extends Throwable> {

    Logger log = LoggerFactory.getLogger(Try.class);

    R apply(T t) throws E;

    static <T, R, E extends Throwable> Function<T, R> of(final Try<T, R, E> fn) {
        return t -> {
            try {
                return fn.apply(t);
            } catch (Throwable e) {
                log.error("Try Error", e);
                return null;
            }
        };
    }

    static <T, R, E extends Throwable> Function<T, R> of(final Try<T, R, E> fn, final Class<?> cls) {
        return t -> {
            try {
                return fn.apply(t);
            } catch (Throwable e) {
                LoggerFactory.getLogger(cls).error("Try Error", e);
                return null;
            }
        };
    }

    static <T, R, E extends Throwable> Function<T, R> of(final Try<T, R, E> fn, final Class<?> cls, final String msg) {
        return t -> {
            try {
                return fn.apply(t);
            } catch (Throwable e) {
                LoggerFactory.getLogger(cls).error("{}", msg, e);
                return null;
            }
        };
    }

    static <T, R, E extends Throwable> Function<T, R> of(final Try<T, R, E> fn, final Class<?> cls, final String msg, final Object... objs) {
        return t -> {
            try {
                return fn.apply(t);
            } catch (Throwable e) {
                LoggerFactory.getLogger(cls).error(msg, objs, e);
                return null;
            }
        };
    }
}