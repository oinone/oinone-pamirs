package pro.shushi.pamirs.auth.view.helper;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.helper.fetch.AuthModuleResourceFetchMethod;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.view.utils.AuthGroupGenerator;
import pro.shushi.pamirs.boot.base.model.UeModule;

/**
 * 获取模块资源方法
 *
 * @author Adamancy Zhang at 20:26 on 2024-03-02
 */
public class AuthModuleGroupResourceFetchMethod extends AuthGroupResourceFetchMethod<UeModule> {

    public AuthModuleGroupResourceFetchMethod(AuthAccessService authAccessService) {
        super(new AuthModuleResourceFetchMethod(authAccessService));
    }

    @Override
    public AuthGroup createAuthGroup(AuthResourceAuthorization authorization, AuthGroupTypeEnum type) {
        return AuthGroupGenerator.buildModuleAuthGroup(authorization.getModule(), type);
    }
}
