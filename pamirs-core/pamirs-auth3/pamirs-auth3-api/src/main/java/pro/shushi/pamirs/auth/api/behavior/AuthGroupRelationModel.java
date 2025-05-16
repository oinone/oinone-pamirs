package pro.shushi.pamirs.auth.api.behavior;

/**
 * 权限组关联模型
 *
 * @author Adamancy Zhang at 18:40 on 2024-01-08
 */
public interface AuthGroupRelationModel {

    /**
     * 获取权限组ID
     *
     * @return 权限组ID
     */
    Long getGroupId();

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
