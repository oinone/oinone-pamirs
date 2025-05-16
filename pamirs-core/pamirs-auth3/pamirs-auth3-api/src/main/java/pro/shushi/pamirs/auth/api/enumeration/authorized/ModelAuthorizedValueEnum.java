package pro.shushi.pamirs.auth.api.enumeration.authorized;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizedValue;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * 模型权限授权值
 *
 * @author Adamancy Zhang at 15:56 on 2024-01-04
 */
@Base
@Dict(dictionary = ModelAuthorizedValueEnum.DICTIONARY, displayName = "模型权限授权值", summary = "模型权限授权值")
public enum ModelAuthorizedValueEnum implements BitEnum, PermissionAuthorizedValue {

    QUERY(1L, "查询权限", "查询权限"),
    CREATE(1L << 1, "创建权限", "创建权限"),
    UPDATE(1L << 2, "更新权限", "更新权限"),
    DELETE(1L << 3, "删除权限", "删除权限");

    public static final String DICTIONARY = "auth.ModelAuthorizedValueEnum";

    private final Long value;
    private final String displayName;
    private final String help;

    ModelAuthorizedValueEnum(Long value, String displayName, String help) {
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
}
