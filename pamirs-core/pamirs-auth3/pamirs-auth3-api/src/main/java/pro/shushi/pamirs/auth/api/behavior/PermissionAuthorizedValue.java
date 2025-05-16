package pro.shushi.pamirs.auth.api.behavior;

/**
 * 权限值枚举
 *
 * @author Adamancy Zhang at 20:44 on 2024-01-08
 */
public interface PermissionAuthorizedValue {

    /**
     * 枚举name
     *
     * @return name
     */
    String name();

    /**
     * 获取权限值
     *
     * @return 权限值
     */
    Long value();
}
