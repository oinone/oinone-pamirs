package pro.shushi.pamirs.auth.view.helper;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.helper.fetch.AuthMenuResourceFetchMethod;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.view.utils.AuthGroupGenerator;
import pro.shushi.pamirs.boot.base.model.Menu;

/**
 * 获取菜单资源方法
 *
 * @author Adamancy Zhang at 19:49 on 2024-03-02
 */
public class AuthMenuGroupResourceFetchMethod extends AuthGroupResourceFetchMethod<Menu> {

    public AuthMenuGroupResourceFetchMethod(AuthAccessService authAccessService) {
        super(new AuthMenuResourceFetchMethod(authAccessService));
    }

    @Override
    public AuthGroup createAuthGroup(AuthResourceAuthorization authorization, AuthGroupTypeEnum type) {
        return AuthGroupGenerator.buildMenuAuthGroup(authorization.getModule(), authorization.getName(), type);
    }
}
