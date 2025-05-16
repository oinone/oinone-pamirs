package pro.shushi.pamirs.auth.api.utils;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ModelAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.helper.AuthEnumerationHelper;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;

import java.util.List;

/**
 * 权限快捷操作帮助类（快捷操作生成的授权项不可在外部修改）
 *
 * @author Adamancy Zhang at 23:17 on 2024-04-03
 */
public class AuthQuickHelper {

    private AuthQuickHelper() {
        // reject create object
    }

    public static AuthResourceAuthorization generatorServerActionAuthorization(String model, String name, List<ResourceAuthorizedValueEnum> authorizedValues) {
        AuthResourceAuthorization authorization = generatorResourceAuthorization(model, name, ResourcePermissionSubtypeEnum.SERVER_ACTION);
        authorization.setAuthorizedEnumList(authorizedValues);
        return authorization;
    }

    public static AuthResourceAuthorization generatorServerActionAuthorization(String model, String name, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorResourceAuthorization(model, name, ResourcePermissionSubtypeEnum.SERVER_ACTION);
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    public static AuthResourceAuthorization generatorViewActionAuthorization(String model, String name, List<ResourceAuthorizedValueEnum> authorizedValues) {
        AuthResourceAuthorization authorization = generatorResourceAuthorization(model, name, ResourcePermissionSubtypeEnum.VIEW_ACTION);
        authorization.setAuthorizedEnumList(authorizedValues);
        return authorization;
    }

    public static AuthResourceAuthorization generatorViewActionAuthorization(String model, String name, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorResourceAuthorization(model, name, ResourcePermissionSubtypeEnum.VIEW_ACTION);
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    public static AuthResourceAuthorization generatorUrlActionAuthorization(String model, String name, List<ResourceAuthorizedValueEnum> authorizedValues) {
        AuthResourceAuthorization authorization = generatorResourceAuthorization(model, name, ResourcePermissionSubtypeEnum.URL_ACTION);
        authorization.setAuthorizedEnumList(authorizedValues);
        return authorization;
    }

    public static AuthResourceAuthorization generatorUrlActionAuthorization(String model, String name, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorResourceAuthorization(model, name, ResourcePermissionSubtypeEnum.URL_ACTION);
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    public static AuthResourceAuthorization generatorClientActionAuthorization(String model, String name, List<ResourceAuthorizedValueEnum> authorizedValues) {
        AuthResourceAuthorization authorization = generatorResourceAuthorization(model, name, ResourcePermissionSubtypeEnum.CLIENT_ACTION);
        authorization.setAuthorizedEnumList(authorizedValues);
        return authorization;
    }

    public static AuthResourceAuthorization generatorClientActionAuthorization(String model, String name, Long authorizedValue) {
        AuthResourceAuthorization authorization = generatorResourceAuthorization(model, name, ResourcePermissionSubtypeEnum.CLIENT_ACTION);
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    public static AuthResourceAuthorization generatorResourceAuthorization(String model, String name, ResourcePermissionSubtypeEnum subtype) {
        AuthResourceAuthorization authorization = new AuthResourceAuthorization();
        authorization.setCode(AuthResourceAuthorization.generatorCode(model, name));
        authorization.setModel(model);
        authorization.setName(name);
        authorization.setType(AuthEnumerationHelper.getResourceType(subtype));
        authorization.setSubtype(subtype);
        authorization.setSource(AuthorizationSourceEnum.SYSTEM);
        authorization.setActive(Boolean.TRUE);
        return authorization;
    }

    public static AuthModelAuthorization generatorModelAuthorization(String model, List<ModelAuthorizedValueEnum> authorizedValues) {
        AuthModelAuthorization authorization = generatorModelAuthorization(model);
        authorization.setAuthorizedEnumList(authorizedValues);
        return authorization;
    }

    public static AuthModelAuthorization generatorModelAuthorization(String model, Long authorizedValue) {
        AuthModelAuthorization authorization = generatorModelAuthorization(model);
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    public static AuthModelAuthorization generatorModelAuthorization(String model) {
        AuthModelAuthorization authorization = new AuthModelAuthorization();
        authorization.setCode(AuthModelAuthorization.generatorCode(model));
        authorization.setModel(model);
        authorization.setSource(AuthorizationSourceEnum.SYSTEM);
        authorization.setActive(Boolean.TRUE);
        return authorization;
    }

    public static AuthFieldAuthorization generatorFieldAuthorization(String model, String field, List<FieldAuthorizedValueEnum> authorizedValues) {
        AuthFieldAuthorization authorization = generatorFieldAuthorization(model, field);
        authorization.setAuthorizedEnumList(authorizedValues);
        return authorization;
    }

    public static AuthFieldAuthorization generatorFieldAuthorization(String model, String field, Long authorizedValue) {
        AuthFieldAuthorization authorization = generatorFieldAuthorization(model, field);
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    public static AuthFieldAuthorization generatorFieldAuthorization(String model, String field) {
        AuthFieldAuthorization authorization = new AuthFieldAuthorization();
        authorization.setCode(AuthModelAuthorization.generatorCode(model, field));
        authorization.setModel(model);
        authorization.setField(field);
        authorization.setSource(AuthorizationSourceEnum.SYSTEM);
        authorization.setActive(Boolean.TRUE);
        return authorization;
    }

    public static AuthRowAuthorization generatorRowAuthorization(String code, String model, String filter, List<RowAuthorizedValueEnum> authorizedValues) {
        AuthRowAuthorization authorization = generatorRowAuthorization(code, model, filter);
        authorization.setAuthorizedEnumList(authorizedValues);
        return authorization;
    }

    public static AuthRowAuthorization generatorRowAuthorization(String code, String model, String filter, Long authorizedValue) {
        AuthRowAuthorization authorization = generatorRowAuthorization(code, model, filter);
        authorization.setAuthorizedValue(authorizedValue);
        return authorization;
    }

    public static AuthRowAuthorization generatorRowAuthorization(String code, String model, String filter) {
        AuthRowAuthorization authorization = new AuthRowAuthorization();
        authorization.setCode(code);
        authorization.setModel(model);
        authorization.setFilter(filter);
        authorization.setSource(AuthorizationSourceEnum.SYSTEM);
        authorization.setActive(Boolean.TRUE);
        return authorization;
    }
}
