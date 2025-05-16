package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessResourceInfoPrepareApi;
import pro.shushi.pamirs.auth.api.spi.AuthFilterService;
import pro.shushi.pamirs.auth.api.utils.AuthFetchPermissionHelper;
import pro.shushi.pamirs.auth.api.utils.AuthVerificationHelper;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;

import java.util.List;

/**
 * 差量更新权限服务
 *
 * @author Adamancy Zhang at 18:46 on 2024-07-03
 */
@Order(88)
@Component
public class UpdateOneWithRelationsPermissionService implements AccessResourceInfoPrepareApi, AuthFilterService {

    private static final String UPDATE_ONE_WITH_RELATIONS_FUN = "updateOneWithRelations";

    @Override
    public void prepareAccessInfo(Function function, String namespace, String fun, FunctionSourceEnum source) {
        if (!UPDATE_ONE_WITH_RELATIONS_FUN.equals(fun) || !FunctionSourceEnum.FUNCTION.equals(source)) {
            return;
        }
        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        ResourcePath lastPath = info.getLastPath();
        if (lastPath == null) {
            return;
        }
        if (namespace.equals(lastPath.getModel()) && fun.equals(lastPath.getName())) {
            AccessResourceInfo newInfo = info.transfer(new AccessResourceInfo(info.getOriginPath()));
            List<ResourcePath> paths = info.getPaths();
            for (int i = 0; i < paths.size() - 1; i++) {
                newInfo.addPath(paths.get(i));
            }
            newInfo.addActionPath(lastPath.getModel(), FunctionConstants.update);
            AccessResourceInfoSession.setInfo(newInfo);
        }
    }

    @Override
    public Boolean isAccessAction(String model, String name) {
        if (!UPDATE_ONE_WITH_RELATIONS_FUN.equals(name)) {
            return null;
        }
        AuthVerificationHelper.checkLogin();
        AuthResult<Boolean> result = AuthFetchPermissionHelper.fetchActionPermissions().transfer((accessActions) -> {
            if (AuthVerificationHelper.isAccessAction(accessActions, model, name)) {
                return true;
            }
            return AuthVerificationHelper.isAccessAction(accessActions, model, FunctionConstants.update);
        });
        if (AuthVerificationHelper.isAccessResource(result)) {
            return true;
        }
        return null;
    }
}
