package pro.shushi.pamirs.middleware.schedule.eunmeration;

import pro.shushi.pamirs.middleware.schedule.directive.DirectiveEnumeration;

/**
 * task execute status
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 12:16
 */
public enum TaskExecuteStatus implements DirectiveEnumeration<TaskExecuteStatus> {

    /**
     * first execute
     */
    FIRST_EXECUTE(1),

    /**
     * last execute
     */
    LAST_EXECUTE(2),

    /**
     * retry execute
     */
    RETRY_EXECUTE(4),

    /**
     * loop execute
     */
    LOOP_EXECUTE(8),

    /**
     * retry loop execute
     */
    RETRY_LOOP_EXECUTE(16);

    private final int value;

    TaskExecuteStatus(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }
}
