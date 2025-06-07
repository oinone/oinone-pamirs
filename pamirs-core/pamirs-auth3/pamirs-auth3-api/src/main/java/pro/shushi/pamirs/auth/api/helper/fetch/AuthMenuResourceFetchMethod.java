package pro.shushi.pamirs.auth.api.helper.fetch;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.FetchResourceHelper;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthPermissionGenerator;
import pro.shushi.pamirs.boot.base.model.Menu;

import java.util.List;
import java.util.Set;

/**
 * 获取菜单资源方法
 *
 * @author Adamancy Zhang at 19:49 on 2024-03-02
 */
public class AuthMenuResourceFetchMethod extends AuthResourceFetchMethod<Menu> {

    public AuthMenuResourceFetchMethod(AuthAccessService authAccessService) {
        super(ResourcePermissionSubtypeEnum.MENU, authAccessService);
    }

    @Override
    public List<Menu> query(Set<Long> resourceIds) {
        return FetchResourceHelper.fetchMenusAndFillParent(resourceIds);
    }

    @Override
    public boolean isManagement(Menu data, String path) {
        if (CollectionUtils.isNotEmpty(data.getChildren())) {
            return false;
        }
        return authAccessService.canManagementModule(data.getModule()).getSuccess() ||
                authAccessService.canManagementMenu(data.getModule(), data.getName()).getSuccess();
    }

    @Override
    public boolean isManagement(AuthResourcePermission resourcePermission) {
        String module = resourcePermission.getModule();
        return authAccessService.canManagementModule(module).getSuccess() ||
                authAccessService.canManagementMenu(module, resourcePermission.getName()).getSuccess();
    }

    @Override
    public AuthResourceAuthorization rawGeneratorResourceAuthorization(Menu data, String path, Long authorizedValue) {
        return AuthPermissionGenerator.generatorMenuAuthorization(data, path, authorizedValue)
                .setResourceId(data.getId());
    }
}
