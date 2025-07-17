package pro.shushi.pamirs.auth.api.utils;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

/**
 * 权限生成器
 *
 * @author Adamancy Zhang at 13:46 on 2024-01-23
 */
public class AuthPermissionGenerator {

    private AuthPermissionGenerator() {
        // reject create object
    }

    public static AuthResourcePermission generatorModulePermission(ModuleDefinition module, String path) {
        return generatorModulePermission(new AuthResourcePermission(), module, path);
    }

    public static AuthResourceAuthorization generatorModuleAuthorization(ModuleDefinition module, String path, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorModulePermission(new AuthResourceAuthorization(), module, path);
        authorization.setResourceCode(AuthResourceCodeGenerator.generatorModuleResourceCode(module));
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    private static <T extends AuthResourcePermission> T generatorModulePermission(T permission, ModuleDefinition module, String path) {
        String moduleModule = module.getModule();
        String model = ModuleDefinition.MODEL_MODEL;
        String name = module.getName();
        permission.setCode(AuthResourcePermission.generatorCode(moduleModule, model, name, path));
        permission.setPath(path);
        permission.setModule(moduleModule);
        permission.setModel(model);
        permission.setName(name);
        permission.setType(ResourcePermissionTypeEnum.MODULE);
        permission.setSubtype(ResourcePermissionSubtypeEnum.MODULE);
        permission.setSource(AuthorizationSourceEnum.SYSTEM);
        permission.setActive(Boolean.TRUE);
        return permission;
    }

    public static AuthResourcePermission generatorHomepagePermission(ModuleDefinition module, ViewAction action, String path) {
        return generatorHomepagePermission(new AuthResourcePermission(), module, action, path);
    }

    public static AuthResourceAuthorization generatorHomepageAuthorization(ModuleDefinition module, ViewAction action, String path, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorHomepagePermission(new AuthResourceAuthorization(), module, action, path);
        authorization.setResourceCode(AuthResourceCodeGenerator.generatorHomepageResourceCode(module, action));
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    private static <T extends AuthResourcePermission> T generatorHomepagePermission(T permission, ModuleDefinition module, ViewAction action, String path) {
        String moduleModule = module.getModule();
        String model = action.getModel();
        String name = action.getName();
        permission.setCode(AuthResourcePermission.generatorCode(moduleModule, model, name, path));
        permission.setPath(path);
        permission.setModule(moduleModule);
        permission.setModel(model);
        permission.setName(name);
        permission.setType(ResourcePermissionTypeEnum.MODULE);
        permission.setSubtype(ResourcePermissionSubtypeEnum.HOMEPAGE);
        permission.setSource(AuthorizationSourceEnum.SYSTEM);
        permission.setActive(Boolean.TRUE);
        return permission;
    }

    public static AuthResourcePermission generatorMenuPermission(Menu menu, String path) {
        return generatorMenuPermission(new AuthResourcePermission(), menu, path);
    }

    public static AuthResourceAuthorization generatorMenuAuthorization(Menu menu, String path, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorMenuPermission(new AuthResourceAuthorization(), menu, path);
        authorization.setResourceCode(AuthResourceCodeGenerator.generatorMenuResourceCode(menu));
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    private static <T extends AuthResourcePermission> T generatorMenuPermission(T permission, Menu menu, String path) {
        String moduleModule = menu.getModule();
        String model = Menu.MODEL_MODEL;
        String name = menu.getName();
        permission.setCode(AuthResourcePermission.generatorCode(moduleModule, model, name, path));
        permission.setPath(path);
        permission.setModule(moduleModule);
        permission.setModel(model);
        permission.setName(name);
        permission.setType(ResourcePermissionTypeEnum.MENU);
        permission.setSubtype(ResourcePermissionSubtypeEnum.MENU);
        permission.setSource(AuthorizationSourceEnum.SYSTEM);
        permission.setActive(Boolean.TRUE);
        return permission;
    }

    public static AuthResourcePermission generatorServerActionPermission(ServerAction action, String path) {
        AuthResourcePermission resourcePermission = generatorActionPermission(action, path);
        resourcePermission.setSubtype(ResourcePermissionSubtypeEnum.SERVER_ACTION);
        return resourcePermission;
    }

    public static AuthResourcePermission generatorViewActionPermission(ViewAction action, String path) {
        AuthResourcePermission resourcePermission = generatorActionPermission(action, path);
        resourcePermission.setSubtype(ResourcePermissionSubtypeEnum.VIEW_ACTION);
        return resourcePermission;
    }

    public static AuthResourcePermission generatorUrlActionPermission(UrlAction action, String path) {
        AuthResourcePermission resourcePermission = generatorActionPermission(action, path);
        resourcePermission.setSubtype(ResourcePermissionSubtypeEnum.URL_ACTION);
        return resourcePermission;
    }

    public static AuthResourcePermission generatorClientActionPermission(ClientAction action, String path) {
        AuthResourcePermission resourcePermission = generatorActionPermission(action, path);
        resourcePermission.setSubtype(ResourcePermissionSubtypeEnum.CLIENT_ACTION);
        return resourcePermission;
    }

    private static AuthResourcePermission generatorActionPermission(Action action, String path) {
        return generatorActionPermission(new AuthResourcePermission(), action, path);
    }

    public static AuthResourceAuthorization generatorServerActionAuthorization(ServerAction action, String path, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorActionAuthorization(action, path, authorizedValue);
        authorization.setResourceCode(AuthResourceCodeGenerator.generatorServerActionResourceCode(action));
        authorization.setSubtype(ResourcePermissionSubtypeEnum.SERVER_ACTION);
        return authorization;
    }

    public static AuthResourceAuthorization generatorViewActionAuthorization(ViewAction action, String path, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorActionAuthorization(action, path, authorizedValue);
        authorization.setResourceCode(AuthResourceCodeGenerator.generatorViewActionResourceCode(action));
        authorization.setSubtype(ResourcePermissionSubtypeEnum.VIEW_ACTION);
        return authorization;
    }

    public static AuthResourceAuthorization generatorUrlActionAuthorization(UrlAction action, String path, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorActionAuthorization(action, path, authorizedValue);
        authorization.setResourceCode(AuthResourceCodeGenerator.generatorUrlActionResourceCode(action));
        authorization.setSubtype(ResourcePermissionSubtypeEnum.URL_ACTION);
        return authorization;
    }

    public static AuthResourceAuthorization generatorClientActionAuthorization(ClientAction action, String path, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorActionAuthorization(action, path, authorizedValue);
        authorization.setResourceCode(AuthResourceCodeGenerator.generatorClientActionResourceCode(action));
        authorization.setSubtype(ResourcePermissionSubtypeEnum.CLIENT_ACTION);
        return authorization;
    }

    private static AuthResourceAuthorization generatorActionAuthorization(Action action, String path, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorActionPermission(new AuthResourceAuthorization(), action, path);
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    private static <T extends AuthResourcePermission> T generatorActionPermission(T permission, Action action, String path) {
        String model = action.getModel();
        String name = action.getName();
        permission.setCode(AuthResourcePermission.generatorCode(model, name, path));
        permission.setPath(path);
        permission.setModel(model);
        permission.setName(name);
        permission.setType(ResourcePermissionTypeEnum.ACTION);
        permission.setSource(AuthorizationSourceEnum.SYSTEM);
        permission.setActive(Boolean.TRUE);
        return permission;
    }
}
