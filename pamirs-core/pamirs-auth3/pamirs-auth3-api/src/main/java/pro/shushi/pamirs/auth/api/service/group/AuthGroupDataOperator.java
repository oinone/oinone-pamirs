package pro.shushi.pamirs.auth.api.service.group;

import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;

import java.util.List;
import java.util.Set;

/**
 * 权限组数据操作服务
 *
 * @author Adamancy Zhang at 10:45 on 2024-01-19
 */
public interface AuthGroupDataOperator {

//    List<AuthGroupResourcePermission> fetchAllResourcePermissions(Long groupId);
//
//    List<AuthGroupResourcePermission> fetchResourcePermissions(Long groupId);

    List<AuthRole> fetchRoles(Long groupId);

    Set<Long> fetchRoleIds(Long groupId);

    List<AuthGroupResourcePermission> fetchActionPermissions(Long groupId);

    List<AuthGroupResourcePermission> fetchValidActionPermissions(Set<Long> groupIds);

    List<AuthGroupResourcePermission> createActionPermissions(List<AuthGroupResourcePermission> data);

    Integer updateActionPermissions(List<AuthGroupResourcePermission> data);

    Integer deleteActionPermissionsByGroupId(Long groupId, List<AuthGroupResourcePermission> data);

    List<AuthGroupFieldPermission> fetchFieldPermissions(Long groupId);

    List<AuthGroupFieldPermission> fetchValidFieldPermissions(Set<Long> groupIds);

    List<AuthGroupFieldPermission> createFieldPermissions(List<AuthGroupFieldPermission> data);

    Integer updateFieldPermissions(List<AuthGroupFieldPermission> data);

    Integer deleteFieldPermissionsByGroupId(Long groupId, List<AuthGroupFieldPermission> data);

    List<AuthGroupRowPermission> fetchRowPermissions(Long groupId);

    List<AuthGroupRowPermission> fetchValidRowPermissions(Set<Long> groupIds);

    void fillPermissions(List<AuthGroupResourcePermission> actionPermissions, List<AuthGroupFieldPermission> fieldPermissions, List<AuthGroupRowPermission> rowPermissions);
}
