package pro.shushi.pamirs.core.common.entry;

/**
 * 完成标记对象
 *
 * @author Adamancy Zhang at 14:57 on 2021-09-14
 */
public class CompletedBody<T> {

    private T value;

    private boolean completed = false;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void completed() {
        this.completed = true;
    }
}
