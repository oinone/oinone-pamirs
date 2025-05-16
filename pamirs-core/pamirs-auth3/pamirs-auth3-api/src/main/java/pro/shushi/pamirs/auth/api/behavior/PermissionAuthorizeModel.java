package pro.shushi.pamirs.auth.api.behavior;

import java.util.List;

/**
 * 权限项授权模型
 *
 * @author Adamancy Zhang at 20:41 on 2024-01-08
 */
public interface PermissionAuthorizeModel<E extends Enum<E> & PermissionAuthorizedValue> extends AuthPermission {

    /**
     * 获取权限枚举值
     *
     * @return 权限枚举值
     */
    List<E> getAuthorizedEnumList();

    /**
     * 获取权限值
     *
     * @return 权限值
     */
    Long getAuthorizedValue();
}
