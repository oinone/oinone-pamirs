package pro.shushi.pamirs.core.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 构造帮助类
 *
 * @author Adamancy Zhang
 * @date 2020-11-20 12:04
 */
public class ConstructHelper {

    private ConstructHelper() {
        //reject create object
    }

    public static <T> ConstructBuilder<T> newInstance(T object) {
        return new ConstructBuilder<>(object);
    }

    /**
     * 基本获取
     *
     * @param object 对象
     * @param getter getter方法
     * @param <T>    任意对象类型
     * @param <R>    getter方法的返回值类型
     * @return 返回值结果
     */
    public static <T, R> R getter(T object, Function<T, R> getter) {
        if (object == null) {
            return null;
        }
        return getter.apply(object);
    }

    /**
     * 可迭代列表对象的内容获取（默认使用{@link ArrayList#ArrayList()}作为结果集承载对象）
     *
     * @param list   可迭代列表
     * @param getter 可迭代列表对象的getter方法
     * @param <T>    可迭代列表对象的类型
     * @param <R>    getter方法的返回值类型
     * @return 结果集
     * @see ConstructHelper#collectionGetter(Iterable, Function, Supplier)
     */
    public static <T, R> List<R> collectionGetter(Iterable<T> list, Function<T, R> getter) {
        return collectionGetter(list, getter, ArrayList::new);
    }

    /**
     * 可迭代列表对象的内容获取
     *
     * @param list     可迭代列表
     * @param getter   可迭代列表对象的getter方法
     * @param supplier 结果集承载对象获取
     * @param <T>      可迭代列表对象的类型
     * @param <R>      getter方法的返回值类型
     * @param <C>      结果集承载对象的类型
     * @return 结果集
     */
    public static <T, R, C extends Collection<R>> C collectionGetter(Iterable<T> list, Function<T, R> getter, Supplier<C> supplier) {
        if (list == null) {
            return null;
        }
        C collection = supplier.get();
        for (T item : list) {
            R data = getter(item, getter);
            if (data == null) {
                continue;
            }
            collection.add(data);
        }
        return collection;
    }

    /**
     * 基本设置
     *
     * @param object 对象
     * @param value  值
     * @param setter setter方法
     * @param <V>    任意对象类型
     * @param <T>    设置目标值
     */
    public static <V, T> void setter(V object, T value, BiConsumer<V, T> setter) {
        if (object == null) {
            return;
        }
        setter.accept(object, value);
    }

    /**
     * 安全获取并设置值
     *
     * @param object 对象
     * @param getter getter方法
     * @param setter setter方法
     * @param <V>    任意对象类型
     * @param <T>    设置目标值
     */
    public static <V, T> void getterAndSetter(V object, Function<V, T> getter, BiConsumer<V, T> setter) {
        setter(object, getter(object, getter), setter);
    }

    public static <V, T, R> void getterAndSetter(V object, Function<V, T> getter, Function<T, R> valueGetter, BiConsumer<V, R> setter) {
        setter(object, getter(getter(object, getter), valueGetter), setter);
    }

    public static <V, T, R, C1 extends Iterable<T>> void collectionGetterAndSetter(V object, Function<V, C1> getter, Function<T, R> valueGetter, BiConsumer<V, List<R>> setter) {
        setter(object, collectionGetter(getter(object, getter), valueGetter), setter);
    }

    public static <V, T, R, C1 extends Iterable<T>, C2 extends Collection<R>> void getterAndSetter(V object, Function<V, C1> getter, Function<T, R> valueGetter, BiConsumer<V, C2> setter, Supplier<C2> supplier) {
        setter(object, collectionGetter(getter(object, getter), valueGetter, supplier), setter);
    }

    public static <T, V> void exchange(T origin,
                                       Function<T, V> originGetter, BiConsumer<T, V> originSetter,
                                       Function<T, V> targetGetter, BiConsumer<T, V> targetSetter) {
        exchange(origin, origin, originGetter, originSetter, targetGetter, targetSetter);
    }

    public static <T, R, V> void exchange(T origin, R target,
                                          Function<T, V> originGetter, BiConsumer<T, V> originSetter,
                                          Function<R, V> targetGetter, BiConsumer<R, V> targetSetter) {
        V a = originGetter.apply(origin);
        V b = targetGetter.apply(target);
        originSetter.accept(origin, b);
        targetSetter.accept(target, a);
    }

    public static class ConstructBuilder<T> {

        private final T object;

        private ConstructBuilder(T object) {
            this.object = object;
        }

        public <V> ConstructBuilder<T> set(V value, BiConsumer<T, V> setter) {
            setter.accept(object, value);
            return this;
        }

        public T get() {
            return object;
        }
    }
}
