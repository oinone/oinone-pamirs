package pro.shushi.pamirs.auth.api.service.group;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.core.common.diff.DiffList;

import java.util.List;
import java.util.Set;

/**
 * 权限组差量保存服务
 *
 * @author Adamancy Zhang at 09:56 on 2024-01-19
 */
public interface AuthGroupDataDiffService {

    DiffList<AuthRole> saveRoles(Long groupId, Set<Long> roleIds, boolean isUpdate);

    DiffList<AuthGroupResourcePermission> saveActionPermissions(Long groupId, List<AuthResourceAuthorization> actionPermissions);

    DiffList<AuthGroupResourcePermission> onlyCreateActionPermissions(Long groupId, List<AuthResourceAuthorization> actionPermissions);

    DiffList<AuthGroupFieldPermission> saveFieldPermissions(Long groupId, List<AuthFieldAuthorization> fieldAuthorization);

    DiffList<AuthGroupRowPermission> saveRowPermissionBySystemPermission(Long groupId, AuthRowAuthorization rowAuthorization);

    DiffList<AuthGroupRowPermission> saveRowPermissionsByDataPermission(Long groupId, List<AuthRowAuthorization> rowAuthorizations);
}
