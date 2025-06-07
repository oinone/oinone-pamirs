package pro.shushi.pamirs.auth.api.enumeration.authorized;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizedValue;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * 字段权限授权值
 *
 * @author Adamancy Zhang at 15:56 on 2024-01-04
 */
@Base
@Dict(dictionary = FieldAuthorizedValueEnum.DICTIONARY, displayName = "字段权限授权值", summary = "字段权限授权值")
public enum FieldAuthorizedValueEnum implements BitEnum, PermissionAuthorizedValue {

    READ(1L, "读权限", "读权限"),
    WRITE(1L << 1, "写权限", "写权限"),
    NON_NULL(1L << 2, "非空权限", "若启用，则不允许指定字段置空");

    public static final String DICTIONARY = "auth.FieldAuthorizedValueEnum";

    private final Long value;
    private final String displayName;
    private final String help;

    FieldAuthorizedValueEnum(Long value, String displayName, String help) {
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

    public static boolean readable(Long authorizedValue) {
        return (authorizedValue & READ.value) == READ.value;
    }

    public static boolean writable(Long authorizedValue) {
        return (authorizedValue & WRITE.value) == WRITE.value;
    }
}
