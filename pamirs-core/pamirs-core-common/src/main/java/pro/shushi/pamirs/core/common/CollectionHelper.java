package pro.shushi.pamirs.core.common;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.iterators.CollatingIterator;
import pro.shushi.pamirs.core.common.function.lambda.PamirsSupplier;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Adamancy Zhang
 * @date 2020-11-06 16:16
 */
public class CollectionHelper {

    private CollectionHelper() {
        //reject create object
    }

    public static <E> CollectionBuilder<E, List<E>> newInstance() {
        return newInstance(16);
    }

    public static <E> CollectionBuilder<E, List<E>> newInstance(int initialCapacity) {
        return newInstance(new ArrayList<>(initialCapacity));
    }

    public static <E, T extends Collection<E>> CollectionBuilder<E, T> newInstance(T collection) {
        return new CollectionBuilder<>(collection);
    }

    public static <E, T extends Collection<E>> CollectionOptions<E, T> collectionOptions(T collection) {
        return new CollectionOptions<>(collection);
    }

    public static <E, T extends List<E>> ListOptions<E, T> listOptions(T list) {
        return new ListOptions<>(list);
    }

    public static <E, T extends List<E>> E getAndAddNewInstance(T list, int index, Supplier<E> newInstanceSupplier) {
        boolean isNeedNewInstance = true;
        E newInstance = null;
        while (list.size() <= index) {
            if (isNeedNewInstance) {
                if (newInstanceSupplier != null) {
                    newInstance = newInstanceSupplier.get();
                }
                isNeedNewInstance = false;
            }
            list.add(newInstance);
        }
        return list.get(index);
    }

    public static class CollectionBuilder<E, T extends Collection<E>> {

        private final T collection;

        private CollectionBuilder(T collection) {
            this.collection = collection;
        }

        public CollectionBuilder<E, T> add(E element) {
            this.collection.add(element);
            return this;
        }

        public CollectionBuilder<E, T> addAll(Collection<E> collection) {
            this.collection.addAll(collection);
            return this;
        }

        public CollectionBuilder<E, T> addBySupplier(PamirsSupplier<E> elementSupplier) {
            this.collection.add(elementSupplier.get());
            return this;
        }

        public CollectionBuilder<E, T> addAllBySupplier(PamirsSupplier<Collection<E>> elementSupplier) {
            this.collection.addAll(elementSupplier.get());
            return this;
        }

        public CollectionOptions<E, T> collectionOptions() {
            return new CollectionOptions<>(collection);
        }

        public T build() {
            return this.collection;
        }
    }

    public static class CollectionOptions<E, T extends Collection<E>> {

        private final T collection;

        private CollectionOptions(T collection) {
            this.collection = collection;
        }

        public T getCollection() {
            return collection;
        }
    }

    public static class ListOptions<E, T extends List<E>> extends CollectionOptions<E, T> {

        private Supplier<E> newInstanceSupplier;

        private ListOptions(T list) {
            super(list);
        }

        public Supplier<E> getNewInstanceSupplier() {
            return newInstanceSupplier;
        }

        public void setNewInstanceSupplier(Supplier<E> newInstanceSupplier) {
            this.newInstanceSupplier = newInstanceSupplier;
        }

        public E get(int index) {
            return getAndAddNewInstance(getCollection(), index, newInstanceSupplier);
        }
    }

    /**
     * 如果集合中不包含该元素，则将该元素添加到指定集合
     *
     * @param collection 指定集合
     * @param element    元素
     * @param <E>        任意类型
     * @return 是否添加成功
     */
    public static <E> boolean addIfAbsent(Collection<E> collection, E element) {
        if (collection.contains(element)) {
            return false;
        }
        collection.add(element);
        return true;
    }

    /**
     * 将不在指定集合中的全部元素添加到指定集合
     *
     * @param collection 指定集合
     * @param iterator   需要添加到指定集合的迭代器
     * @param <E>        任意类型
     * @return 未添加到集合中的元素
     */
    public static <E> List<E> addAllIfAbsent(Collection<E> collection, Iterator<? extends E> iterator) {
        List<E> nonAddCollection = new ArrayList<>();
        while (iterator.hasNext()) {
            E element = iterator.next();
            if (!addIfAbsent(collection, element)) {
                nonAddCollection.add(element);
            }
        }
        return nonAddCollection;
    }

    public static <E, C extends Collection<E>, CC extends Collection<C>> List<E> flat(CC collection) {
        return flat(collection, ArrayList::new);
    }

    public static <T, R, C extends Collection<T>, CC extends Collection<C>> List<R> flat(CC list, Function<C, List<R>> converter) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        List<R> results = new ArrayList<>(32);
        for (C item : list) {
            List<R> target = converter.apply(item);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(target)) {
                results.addAll(target);
            }
        }
        return results;
    }

    public static <T, C extends Collection<T>, M extends Map<?, C>> List<T> flat(M map) {
        return flat(map, ArrayList::new);
    }

    public static <T, R, C extends Collection<T>, M extends Map<?, C>> List<R> flat(M map, Function<C, List<R>> converter) {
        if (MapUtils.isEmpty(map)) {
            return new ArrayList<>(0);
        }
        List<R> results = new ArrayList<>(32);
        for (C value : map.values()) {
            List<R> target = converter.apply(value);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(target)) {
                results.addAll(target);
            }
        }
        return results;
    }

    @SafeVarargs
    public static <T> List<T> connect(List<T>... lists) {
        CollatingIterator<T> iterators = new CollatingIterator<>((a, b) -> 0, lists.length);
        int total = 0;
        for (List<T> list : lists) {
            int size = list.size();
            if (size >= 1) {
                iterators.addIterator(list.iterator());
                total += size;
            }
        }
        if (total == 0) {
            return new ArrayList<>(0);
        }
        return IteratorUtils.toList(iterators, total);
    }

    public static <T> void swap(List<T> list, int index1, int index2) {
        if (index1 > index2) {
            int temp = index1;
            index1 = index2;
            index2 = temp;
        }
        T item2 = list.get(index2);
        T item1 = list.remove(index1);
        list.add(index1, item2);
        list.remove(index2);
        list.add(index2, item1);
    }
}
