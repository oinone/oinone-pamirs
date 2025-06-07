package pro.shushi.pamirs.middleware.schedule.eunmeration;

import pro.shushi.pamirs.middleware.schedule.directive.IntValueEnumeration;

import java.util.Collection;

/**
 * time anchor
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 12:03
 */
public enum TimeAnchor implements IntValueEnumeration<TimeAnchor> {

    /**
     * execute before
     */
    BEFORE(-1),

    /**
     * execute after
     */
    AFTER(1);

    private final int value;

    TimeAnchor(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public Collection<TimeAnchor> intValuesOf(int value) {
        throw new UnsupportedOperationException();
    }
}
