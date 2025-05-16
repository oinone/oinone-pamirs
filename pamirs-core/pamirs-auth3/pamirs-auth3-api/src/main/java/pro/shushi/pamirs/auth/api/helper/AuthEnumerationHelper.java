package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;

/**
 * 权限动作帮助类
 *
 * @author Adamancy Zhang at 12:30 on 2024-01-16
 */
public class AuthEnumerationHelper {

    private AuthEnumerationHelper() {
        // reject create object
    }

    public static ActionTypeEnum getActionType(PermissionMateDataEnum metadataType) {
        switch (metadataType) {
            case SERVER_ACTION:
                return ActionTypeEnum.SERVER;
            case VIEW_ACTION:
                return ActionTypeEnum.VIEW;
            case URL_ACTION:
                return ActionTypeEnum.URL;
            case CLIENT_ACTION:
                return ActionTypeEnum.CLIENT;
//            case COMPOSITION_ACTION:
//                return ActionTypeEnum.COMPOSITION;
            default:
                return null;
        }
    }

    public static ActionTypeEnum getActionType(ResourcePermissionSubtypeEnum metadataType) {
        switch (metadataType) {
            case SERVER_ACTION:
                return ActionTypeEnum.SERVER;
            case VIEW_ACTION:
                return ActionTypeEnum.VIEW;
            case URL_ACTION:
                return ActionTypeEnum.URL;
            case CLIENT_ACTION:
                return ActionTypeEnum.CLIENT;
//            case COMPOSITION_ACTION:
//                return ActionTypeEnum.COMPOSITION;
            default:
                return null;
        }
    }

    public static ResourcePermissionSubtypeEnum getActionResourceSubtype(ActionTypeEnum actionType) {
        switch (actionType) {
            case SERVER:
                return ResourcePermissionSubtypeEnum.SERVER_ACTION;
            case VIEW:
                return ResourcePermissionSubtypeEnum.VIEW_ACTION;
            case URL:
                return ResourcePermissionSubtypeEnum.URL_ACTION;
            case CLIENT:
                return ResourcePermissionSubtypeEnum.CLIENT_ACTION;
//            case COMPOSITION_ACTION:
//                return ActionTypeEnum.COMPOSITION;
            default:
                return null;
        }
    }

    public static PermissionMateDataEnum getResourcePermissionType(ResourcePermissionSubtypeEnum metadataType) {
        switch (metadataType) {
            case MODULE:
                return PermissionMateDataEnum.MODULE;
            case HOMEPAGE:
                return PermissionMateDataEnum.HOMEPAGE;
            case MENU:
                return PermissionMateDataEnum.MENU;
            case SERVER_ACTION:
                return PermissionMateDataEnum.SERVER_ACTION;
            case VIEW_ACTION:
                return PermissionMateDataEnum.VIEW_ACTION;
            case URL_ACTION:
                return PermissionMateDataEnum.URL_ACTION;
            case CLIENT_ACTION:
                return PermissionMateDataEnum.CLIENT_ACTION;
            default:
                return null;
        }
    }

    public static ResourcePermissionSubtypeEnum getNodeType(PermissionMateDataEnum metadataType) {
        switch (metadataType) {
            case MODULE:
                return ResourcePermissionSubtypeEnum.MODULE;
            case HOMEPAGE:
                return ResourcePermissionSubtypeEnum.HOMEPAGE;
            case MENU:
                return ResourcePermissionSubtypeEnum.MENU;
            case SERVER_ACTION:
                return ResourcePermissionSubtypeEnum.SERVER_ACTION;
            case VIEW_ACTION:
                return ResourcePermissionSubtypeEnum.VIEW_ACTION;
            case URL_ACTION:
                return ResourcePermissionSubtypeEnum.URL_ACTION;
            case CLIENT_ACTION:
                return ResourcePermissionSubtypeEnum.CLIENT_ACTION;
            default:
                return null;
        }
    }

    public static ResourcePermissionTypeEnum getResourceType(ResourcePermissionSubtypeEnum subtype) {
        switch (subtype) {
            case MODULE:
            case HOMEPAGE:
                return ResourcePermissionTypeEnum.MODULE;
            case MENU:
                return ResourcePermissionTypeEnum.MENU;
            case VIEW:
                return ResourcePermissionTypeEnum.VIEW;
            case SERVER_ACTION:
            case VIEW_ACTION:
            case URL_ACTION:
            case CLIENT_ACTION:
                return ResourcePermissionTypeEnum.ACTION;
            default:
                return null;
        }
    }
}
