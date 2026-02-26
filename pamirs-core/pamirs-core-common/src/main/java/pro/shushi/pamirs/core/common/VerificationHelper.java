package pro.shushi.pamirs.core.common;

import jakarta.annotation.Nonnull;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 验证帮助类
 *
 * @author Adamancy Zhang at 22:12 on 2021-08-11
 */
public class VerificationHelper {

    /**
     * 必填判定
     *
     * @param value         值
     * @param exceptionEnum 异常枚举
     * @param <T>           任意枚举类型
     */
    @Nonnull
    public static <T extends Enum<T> & ExpBaseEnum, V> V required(V value, T exceptionEnum) {
        if (ObjectHelper.isBlank(value)) {
            throw PamirsException.construct(exceptionEnum).errThrow();
        }
        return value;
    }

    /**
     * 必填判定
     *
     * @param value        值
     * @param errorMessage 错误信息
     * @see VerificationHelper#required(Object, Enum)
     * @deprecated 2.3.0
     */
    @Deprecated
    @Nonnull
    public static <V> V required(V value, String errorMessage) {
        if (ObjectHelper.isBlank(value)) {
            throw new RuntimeException(errorMessage);
        }
        return value;
    }

    /**
     * 必填判定
     *
     * @param value          值
     * @param throwException 异常
     * @param <V>            任意值类型
     * @param <E>            任意运行时异常
     * @return 存在的值
     */
    @Nonnull
    public static <V, E extends RuntimeException> V requiredOrThrow(V value, Supplier<E> throwException) {
        if (ObjectHelper.isBlank(value)) {
            throw throwException.get();
        }
        return value;
    }

    public static <T, V> void setDefaultValue(T data, Function<T, V> getter, BiConsumer<T, V> setter, V defaultValue) {
        setDefaultValue(data, getter, setter, (d, v) -> v == null, () -> defaultValue);
    }

    public static <T, V> void setDefaultValue(T data, Function<T, V> getter, BiConsumer<T, V> setter, Supplier<V> defaultValueSupplier) {
        setDefaultValue(data, getter, setter, (d, v) -> v == null, defaultValueSupplier);
    }

    public static <T, V> void setDefaultValue(T data, Function<T, V> getter, BiConsumer<T, V> setter, BiPredicate<T, V> predicate, Supplier<V> defaultValueSupplier) {
        if (data == null) {
            return;
        }
        V value = getter.apply(data);
        if (predicate.test(data, value)) {
            setter.accept(data, defaultValueSupplier.get());
        }
    }

    @SafeVarargs
    public static <T> boolean isChange(T origin, T target, Getter<T, ?>... properties) {
        for (Getter<T, ?> property : properties) {
            if (!Objects.equals(property.apply(origin), property.apply(target))) {
                return true;
            }
        }
        return false;
    }
}
