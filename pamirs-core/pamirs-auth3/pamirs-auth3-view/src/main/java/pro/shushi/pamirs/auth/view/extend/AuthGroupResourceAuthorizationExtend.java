package pro.shushi.pamirs.auth.view.extend;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthResourceAuthorizationExtendApi;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;

import java.util.List;
import java.util.Set;

/**
 * 权限组资源权限授权扩展
 *
 * @author Adamancy Zhang at 19:53 on 2024-09-10
 */
@Component
public class AuthGroupResourceAuthorizationExtend extends AbstractAuthGroupAuthorizationExtend implements AuthResourceAuthorizationExtendApi {

    @Override
    public void updates(Set<Long> roleIds, List<AuthResourceAuthorization> permissions) {
        deleteGroupRoles(roleIds);
    }
}
