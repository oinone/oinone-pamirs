package pro.shushi.pamirs.auth.api.helper.fetch;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.FetchResourceHelper;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthPermissionGenerator;
import pro.shushi.pamirs.boot.base.model.ServerAction;

import java.util.List;
import java.util.Set;

/**
 * 获取提交动作资源方法
 *
 * @author Adamancy Zhang at 19:54 on 2024-03-02
 */
public class AuthServerActionResourceFetchMethod extends AuthActionResourceFetchMethod<ServerAction> {

    public AuthServerActionResourceFetchMethod(AuthAccessService authAccessService) {
        super(ResourcePermissionSubtypeEnum.SERVER_ACTION, authAccessService);
    }

    @Override
    public List<ServerAction> query(Set<Long> resourceIds) {
        return FetchResourceHelper.fetchActions(resourceIds, ServerAction.MODEL_MODEL);
    }

    @Override
    public AuthResourceAuthorization rawGeneratorResourceAuthorization(ServerAction data, String path, Long authorizedValue) {
        return AuthPermissionGenerator.generatorServerActionAuthorization(data, path, authorizedValue)
                .setResourceId(data.getId());
    }
}
