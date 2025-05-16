package pro.shushi.pamirs.framework.connectors.data.elastic.common.util;

import java.io.Serializable;

/**
 * Triple
 *
 * @author yakir on 2019/05/20 09:41.
 */
public class Triple<T, K, V> implements Serializable {

    private static final long serialVersionUID = 2144327472662100517L;

    private T left;
    private K mid;
    private V right;

    public Triple(T left) {
        this.left = left;
    }

    public Triple(T left, K mid, V right) {
        this.left  = left;
        this.mid   = mid;
        this.right = right;
    }

    public static <T, K, V> Triple<T, K, V> of(T t, K key, V value) {
        return new Triple<>(t, key, value);
    }

    public T left() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public K mid() {
        return mid;
    }

    public void setMid(K mid) {
        this.mid = mid;
    }

    public V right() {
        return right;
    }

    public void setRight(V right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "{ "
                .concat(null == left ? "null" : left.toString())
                .concat(", ")
                .concat(null == mid ? "null" : mid.toString())
                .concat(", ")
                .concat(null == right ? "null" : right.toString())
                .concat(" }");
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((mid == null) ? 0 : mid.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof Triple) {
            Triple triple = (Triple) o;
            if (left != null ? !left.equals(triple.left) : triple.left != null)
                return false;
            if (mid != null ? !mid.equals(triple.mid) : triple.mid != null)
                return false;
            return right != null ? right.equals(triple.right) : triple.right == null;
        }
        return false;
    }
}