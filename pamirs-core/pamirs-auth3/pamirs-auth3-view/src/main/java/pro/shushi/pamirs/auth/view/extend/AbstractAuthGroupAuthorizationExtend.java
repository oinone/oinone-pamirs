package pro.shushi.pamirs.auth.view.extend;

import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthAuthorizationSceneApi;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRole;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;

import java.util.Collections;
import java.util.Set;

/**
 * 抽象权限组权限授权扩展
 *
 * @author Adamancy Zhang at 10:35 on 2024-09-12
 */
public abstract class AbstractAuthGroupAuthorizationExtend implements AuthAuthorizationSceneApi {

    @Autowired
    protected AuthGroupExtendExecutionStatus authGroupExtendExecutionStatus;

    @Override
    public String scene() {
        return AuthAuthorizationSceneApi.GROUP_SCENE;
    }

    public void deleteGroupRoles(Set<Long> roleIds) {
        if (authGroupExtendExecutionStatus.isUpdates()) {
            return;
        }
        DataShardingHelper.build().collectionSharding(roleIds, sublist -> {
            Models.origin().deleteByWrapper(Pops.<AuthGroupRole>lambdaQuery()
                    .from(AuthGroupRole.MODEL_MODEL)
                    .in(AuthGroupRole::getRoleId, sublist));
            return Collections.emptyList();
        });
        authGroupExtendExecutionStatus.updates();
    }
}
