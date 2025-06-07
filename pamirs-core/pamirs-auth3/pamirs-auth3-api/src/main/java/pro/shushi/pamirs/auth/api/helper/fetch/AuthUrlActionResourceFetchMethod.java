package pro.shushi.pamirs.auth.api.helper.fetch;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.FetchResourceHelper;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthPermissionGenerator;
import pro.shushi.pamirs.boot.base.model.UrlAction;

import java.util.List;
import java.util.Set;

/**
 * 获取链接动作资源方法
 *
 * @author Adamancy Zhang at 19:54 on 2024-03-02
 */
public class AuthUrlActionResourceFetchMethod extends AuthActionResourceFetchMethod<UrlAction> {

    public AuthUrlActionResourceFetchMethod(AuthAccessService authAccessService) {
        super(ResourcePermissionSubtypeEnum.URL_ACTION, authAccessService);
    }

    @Override
    public List<UrlAction> query(Set<Long> resourceIds) {
        return FetchResourceHelper.fetchActions(resourceIds, UrlAction.MODEL_MODEL);
    }

    @Override
    public AuthResourceAuthorization rawGeneratorResourceAuthorization(UrlAction data, String path, Long authorizedValue) {
        return AuthPermissionGenerator.generatorUrlActionAuthorization(data, path, authorizedValue)
                .setResourceId(data.getId());
    }
}
