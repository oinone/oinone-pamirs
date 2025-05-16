package pro.shushi.pamirs.core.common.cache;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Adamancy Zhang on 2021-03-09 17:02
 */
public class NullIterator<E> implements Iterator<E> {

    private static final NullIterator<?> INSTANCE = new NullIterator<>();

    private NullIterator() {
        //reject create object
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        return null;
    }

    @Override
    public void remove() {
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
    }

    @SuppressWarnings("unchecked")
    public static <T> NullIterator<T> getInstance() {
        return (NullIterator<T>) INSTANCE;
    }
}
