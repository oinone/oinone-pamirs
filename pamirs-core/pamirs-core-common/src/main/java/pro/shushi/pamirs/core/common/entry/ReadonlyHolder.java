package pro.shushi.pamirs.core.common.entry;

import java.io.Serializable;

/**
 * @author Adamancy Zhang on 2021-02-27 14:59
 */
public class ReadonlyHolder<T> implements Serializable {
    private static final long serialVersionUID = 4892446132584511824L;
    
    private final T value;

    public ReadonlyHolder(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
