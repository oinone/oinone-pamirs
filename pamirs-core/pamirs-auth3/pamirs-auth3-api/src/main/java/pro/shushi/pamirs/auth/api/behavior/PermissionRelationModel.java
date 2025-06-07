package pro.shushi.pamirs.auth.api.behavior;

/**
 * 权限项关联模型
 *
 * @author Adamancy Zhang at 18:40 on 2024-01-08
 */
public interface PermissionRelationModel {

    /**
     * 获取角色ID
     *
     * @return 角色ID
     */
    Long getRoleId();

    /**
     * 获取权限项ID
     *
     * @return 权限项ID
     */
    Long getPermissionId();

    /**
     * 获取权限值
     *
     * @return 权限值
     */
    Long getAuthorizedValue();
}
