package pro.shushi.pamirs.middleware.schedule.eunmeration;

import pro.shushi.pamirs.middleware.schedule.directive.IntValueEnumeration;

import java.util.Collection;

/**
 * task status
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 12:26
 */
public enum TaskStatus implements IntValueEnumeration<TaskStatus> {

    WAITING(0),
    FINISHED(1),
    TRANSFER(2),
    CANCELED(-1),
    ERROR(-2),
    EXECUTE_ERROR(-3);

    private final int value;

    TaskStatus(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public TaskStatus intValueOf(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<TaskStatus> intValuesOf(int value) {
        throw new UnsupportedOperationException();
    }
}
