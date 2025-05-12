package pro.shushi.pamirs.meta.api.core.orm.systems.directive;

/**
 * 系统指令枚举
 * <p>
 * 2020/7/20 3:22 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public enum SystemDirectiveEnum {

    REENTRY(2),
    ORM_REENTRY(2 << 1),
    CLEAN(2 << 2),
    META_COMPLETED(2 << 3),
    META_DIFFING(2 << 4),
    META_CROSSING(2 << 5),
    META_REFRESH(2 << 6),

    BUILT_ACTION(2 << 19),
    UNLOCK(2 << 20),
    CHECK(2 << 21),
    DEFAULT_VALUE(2 << 22),
    EXT_POINT(2 << 23),
    HOOK(2 << 24),
    AUTHENTICATE(2 << 25),
    ORM_COLUMN(2 << 26),
    USE_PK_STRATEGY(2 << 27),
    FROM_CLIENT(2 << 28),
    SYNC(2 << 29),
    IGNORE_FUN_MANAGEMENT(2L << 30),

    REMOTE_META(2L << 31),

    ;

    private Long value;

    SystemDirectiveEnum(long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public final static Long INIT_VALUE = CHECK.value + DEFAULT_VALUE.value + EXT_POINT.value + HOOK.value + AUTHENTICATE.value
            + USE_PK_STRATEGY.value + FROM_CLIENT.value;

    public static Long getInitValue() {
        return INIT_VALUE;
    }

}
