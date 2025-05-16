package pro.shushi.pamirs.auth.api.enumeration.authorized;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizedValue;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * 行权限授权值
 *
 * @author Adamancy Zhang at 15:56 on 2024-01-04
 */
@Base
@Dict(dictionary = RowAuthorizedValueEnum.DICTIONARY, displayName = "行权限授权值", summary = "行权限授权值")
public enum RowAuthorizedValueEnum implements BitEnum, PermissionAuthorizedValue {

    READ(1L, "读权限", "读权限"),
    WRITE(1L << 1, "写权限", "写权限"),
    DELETE(1L << 2, "删除权限", "删除权限");

    public static final String DICTIONARY = "auth.RowAuthorizedValueEnum";

    private static long FULL_VALUE;

    static {
        long fullValue = 0L;
        for (RowAuthorizedValueEnum authorizedValue : RowAuthorizedValueEnum.values()) {
            fullValue |= authorizedValue.value;
        }
        RowAuthorizedValueEnum.FULL_VALUE = fullValue;
    }

    private final Long value;
    private final String displayName;
    private final String help;

    RowAuthorizedValueEnum(Long value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public Long value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

    public static long fullValue() {
        return FULL_VALUE;
    }

    public boolean enabled(Long authorizedValue) {
        return (authorizedValue & this.value) == this.value;
    }

    public static boolean readable(Long authorizedValue) {
        return READ.enabled(authorizedValue);
    }

    public static boolean writable(Long authorizedValue) {
        return WRITE.enabled(authorizedValue);
    }

    public static boolean deletable(Long authorizedValue) {
        return DELETE.enabled(authorizedValue);
    }
}
