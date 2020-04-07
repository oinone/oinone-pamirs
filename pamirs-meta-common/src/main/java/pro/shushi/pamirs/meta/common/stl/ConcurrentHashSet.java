package pro.shushi.pamirs.meta.common.stl;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = -8672117787651310382L;
    private static final Object PRESENT = new Object();
    private final ConcurrentMap<E, Object> map;

    public ConcurrentHashSet() {
        this.map = new ConcurrentHashMap();
    }

    public ConcurrentHashSet(int initialCapacity) {
        this.map = new ConcurrentHashMap(initialCapacity);
    }

    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    public boolean add(E e) {
        return this.map.put(e, PRESENT) == null;
    }

    public boolean remove(Object o) {
        return this.map.remove(o) == PRESENT;
    }

    public void clear() {
        this.map.clear();
    }
}
