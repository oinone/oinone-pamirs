package pro.shushi.pamirs.auth.api.enumeration.authorized;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizedValue;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * 资源权限授权值
 *
 * @author Adamancy Zhang at 15:15 on 2024-01-04
 */
@Base
@Dict(dictionary = ResourceAuthorizedValueEnum.DICTIONARY, displayName = "资源权限授权值", summary = "资源权限授权值")
public enum ResourceAuthorizedValueEnum implements BitEnum, PermissionAuthorizedValue {

    ACCESS(1L, "访问权限", "访问权限"),
    MANAGEMENT(1L << 1, "管理权限", "管理权限"),
    DESIGN(1L << 2, "设计权限", "设计权限");

    public static final String DICTIONARY = "auth.ResourceAuthorizedValueEnum";

    private final Long value;
    private final String displayName;
    private final String help;

    ResourceAuthorizedValueEnum(Long value, String displayName, String help) {
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

    public static boolean isAccess(long authorizedValue) {
        return (authorizedValue & ACCESS.value) == ACCESS.value;
    }

    public static boolean isManagement(long authorizedValue) {
        return (authorizedValue & MANAGEMENT.value) == MANAGEMENT.value;
    }

    public static boolean isDesign(long authorizedValue) {
        return (authorizedValue & DESIGN.value) == DESIGN.value;
    }
}
