package pro.shushi.pamirs.auth.api.helper.fetch;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.FetchResourceHelper;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthPermissionGenerator;
import pro.shushi.pamirs.boot.base.model.ClientAction;

import java.util.List;
import java.util.Set;

/**
 * 获取客户端动作资源方法
 *
 * @author Adamancy Zhang at 19:54 on 2024-03-02
 */
public class AuthClientActionResourceFetchMethod extends AuthActionResourceFetchMethod<ClientAction> {

    public AuthClientActionResourceFetchMethod(AuthAccessService authAccessService) {
        super(ResourcePermissionSubtypeEnum.CLIENT_ACTION, authAccessService);
    }

    @Override
    public List<ClientAction> query(Set<Long> resourceIds) {
        return FetchResourceHelper.fetchActions(resourceIds, ClientAction.MODEL_MODEL);
    }

    @Override
    public AuthResourceAuthorization rawGeneratorResourceAuthorization(ClientAction data, String path, Long authorizedValue) {
        return AuthPermissionGenerator.generatorClientActionAuthorization(data, path, authorizedValue)
                .setResourceId(data.getId());
    }
}
