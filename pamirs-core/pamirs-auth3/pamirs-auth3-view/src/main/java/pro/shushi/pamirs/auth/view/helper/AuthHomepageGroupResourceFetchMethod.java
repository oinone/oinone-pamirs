package pro.shushi.pamirs.auth.view.helper;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.helper.fetch.AuthHomepageResourceFetchMethod;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.view.utils.AuthGroupGenerator;
import pro.shushi.pamirs.boot.base.model.UeModule;

/**
 * 获取首页资源方法
 *
 * @author Adamancy Zhang at 20:26 on 2024-03-02
 */
public class AuthHomepageGroupResourceFetchMethod extends AuthGroupResourceFetchMethod<UeModule> {

    public AuthHomepageGroupResourceFetchMethod(AuthAccessService authAccessService) {
        super(new AuthHomepageResourceFetchMethod(authAccessService));
    }

    @Override
    public AuthGroup createAuthGroup(AuthResourceAuthorization authorization, AuthGroupTypeEnum type) {
        return AuthGroupGenerator.buildHomepageAuthGroup(authorization.getModule(), type);
    }
}
