package pro.shushi.pamirs.auth.view.helper;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.helper.fetch.AuthClientActionResourceFetchMethod;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.view.utils.AuthGroupGenerator;
import pro.shushi.pamirs.boot.base.model.ClientAction;

/**
 * 获取客户端动作资源方法
 *
 * @author Adamancy Zhang at 19:54 on 2024-03-02
 */
public class AuthClientActionGroupResourceFetchMethod extends AuthGroupResourceFetchMethod<ClientAction> {

    public AuthClientActionGroupResourceFetchMethod(AuthAccessService authAccessService) {
        super(new AuthClientActionResourceFetchMethod(authAccessService));
    }

    @Override
    public AuthGroup createAuthGroup(AuthResourceAuthorization authorization, AuthGroupTypeEnum type) {
        return AuthGroupGenerator.buildActionAuthGroup(authorization.getModel(), authorization.getName(), type);
    }
}
