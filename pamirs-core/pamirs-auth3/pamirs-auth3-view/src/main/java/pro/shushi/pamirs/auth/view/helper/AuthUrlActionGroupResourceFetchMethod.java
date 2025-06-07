package pro.shushi.pamirs.auth.view.helper;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.helper.fetch.AuthUrlActionResourceFetchMethod;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.view.utils.AuthGroupGenerator;
import pro.shushi.pamirs.boot.base.model.UrlAction;

/**
 * 获取链接动作资源方法
 *
 * @author Adamancy Zhang at 19:54 on 2024-03-02
 */
public class AuthUrlActionGroupResourceFetchMethod extends AuthGroupResourceFetchMethod<UrlAction> {

    public AuthUrlActionGroupResourceFetchMethod(AuthAccessService authAccessService) {
        super(new AuthUrlActionResourceFetchMethod(authAccessService));
    }

    @Override
    public AuthGroup createAuthGroup(AuthResourceAuthorization authorization, AuthGroupTypeEnum type) {
        return AuthGroupGenerator.buildActionAuthGroup(authorization.getModel(), authorization.getName(), type);
    }
}
