package pro.shushi.pamirs.auth.api.helper.fetch;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.FetchResourceHelper;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthPermissionGenerator;
import pro.shushi.pamirs.boot.base.model.ViewAction;

import java.util.List;
import java.util.Set;

/**
 * 获取跳转动作资源方法
 *
 * @author Adamancy Zhang at 19:54 on 2024-03-02
 */
public class AuthViewActionResourceFetchMethod extends AuthActionResourceFetchMethod<ViewAction> {

    public AuthViewActionResourceFetchMethod(AuthAccessService authAccessService) {
        super(ResourcePermissionSubtypeEnum.VIEW_ACTION, authAccessService);
    }

    @Override
    public List<ViewAction> query(Set<Long> resourceIds) {
        return FetchResourceHelper.fetchActions(resourceIds, ViewAction.MODEL_MODEL);
    }

    @Override
    public AuthResourceAuthorization rawGeneratorResourceAuthorization(ViewAction data, String path, Long authorizedValue) {
        return AuthPermissionGenerator.generatorViewActionAuthorization(data, path, authorizedValue)
                .setResourceId(data.getId());
    }
}
