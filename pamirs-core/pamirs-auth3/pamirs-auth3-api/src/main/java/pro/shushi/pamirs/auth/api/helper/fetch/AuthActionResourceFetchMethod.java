package pro.shushi.pamirs.auth.api.helper.fetch;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

/**
 * 获取动作资源方法
 *
 * @author Adamancy Zhang at 11:59 on 2024-10-15
 */
@Slf4j
public abstract class AuthActionResourceFetchMethod<T extends Action> extends AuthResourceFetchMethod<T> {

    protected AuthActionResourceFetchMethod(ResourcePermissionSubtypeEnum nodeType, AuthAccessService authAccessService) {
        super(nodeType, authAccessService);
    }

    @Override
    public boolean isManagement(T data, String path) {
        return isCanAllotAction(path) || authAccessService.canManagementAction(data.getModel(), data.getName()).getSuccess();
    }

    @Override
    public boolean isManagement(AuthResourcePermission resourcePermission) {
        return isCanAllotAction(resourcePermission.getPath()) || authAccessService.canManagementAction(resourcePermission.getModel(), resourcePermission.getName()).getSuccess();
    }

    protected boolean isCanAllotAction(String path) {
        if (authAccessService.canManagementAction(path).getSuccess()) {
            return true;
        }
        AccessResourceInfo info = resourcePathParser.parseAccessInfo(path);
        if (info == null) {
            log.debug("parse access info error. path: {}", path);
            return false;
        }
        String module = info.getModule();
        if (StringUtils.isNotBlank(module)) {
            if (authAccessService.canManagementModule(module).getSuccess()) {
                return true;
            }
            String menu = info.getMenu();
            if (StringUtils.isNotBlank(menu)) {
                if (authAccessService.canManagementMenu(module, menu).getSuccess()) {
                    return true;
                }
            }
        }
        String model = info.getModel();
        String actionName = info.getActionName();
        if (StringUtils.isNoneBlank(model, actionName)) {
            if (authAccessService.canManagementAction(model, actionName).getSuccess()) {
                return true;
            }
        }
        return false;
    }
}
