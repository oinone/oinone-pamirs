package pro.shushi.pamirs.auth.api.helper.fetch;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthPermissionGenerator;
import pro.shushi.pamirs.boot.base.model.UeModule;

import java.util.List;
import java.util.Set;

/**
 * 获取模块资源方法
 *
 * @author Adamancy Zhang at 22:02 on 2024-09-10
 */
public class AuthModuleResourceFetchMethod extends AuthResourceFetchMethod<UeModule> {

    public AuthModuleResourceFetchMethod(AuthAccessService authAccessService) {
        super(ResourcePermissionSubtypeEnum.MODULE, authAccessService);
    }

    @Override
    public List<UeModule> query(Set<Long> resourceIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isManagement(UeModule data, String path) {
        return authAccessService.canManagementModule(data.getModule()).getSuccess();
    }

    @Override
    public boolean isManagement(AuthResourcePermission resourcePermission) {
        return authAccessService.canManagementModule(resourcePermission.getModule()).getSuccess();
    }

    @Override
    public AuthResourceAuthorization rawGeneratorResourceAuthorization(UeModule data, String path, Long authorizedValue) {
        return AuthPermissionGenerator.generatorModuleAuthorization(data, path, authorizedValue)
                .setResourceId(data.getId());
    }
}
