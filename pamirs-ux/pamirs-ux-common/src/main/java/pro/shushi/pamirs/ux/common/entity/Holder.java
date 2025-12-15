package pro.shushi.pamirs.ux.common.entity;

/**
 * Value holder
 *
 * @author Adamancy Zhang at 12:21 on 2020-12-25
 */
public class Holder<T> {

    private volatile boolean isNull;

    private volatile T value;

    public Holder() {
        this.isNull = true;
    }

    public Holder(T value) {
        this.isNull = false;
        this.value = value;
    }

    public T get() {
        return value;
    }

    public boolean isNotSetValue() {
        return isNull;
    }

    public boolean isSetValue() {
        return !isNull;
    }

    public void set(T value) {
        this.isNull = false;
        this.value = value;
    }
}
