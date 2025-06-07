package pro.shushi.pamirs.auth.view.utils;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * AuthGroup生成器
 *
 * @author Adamancy Zhang at 20:12 on 2023-12-07
 */
public class AuthGroupGenerator {

    private static final String MODULE_AUTH_GROUP_NAME = "访问应用";
    private static final String MODULE_AUTH_GROUP_COMMENT = "拥有该权限组的角色，可访问应用";
    private static final String MODULE_MANAGEMENT_GROUP_NAME = "管理应用";
    private static final String MODULE_MANAGEMENT_GROUP_COMMENT = "拥有当前应用完整权限，含向下分配权限、应用配置管理等";

    private static final String HOMEPAGE_AUTH_GROUP_NAME = "访问首页";
    private static final String HOMEPAGE_AUTH_GROUP_COMMENT = "拥有该权限组的角色，可访问首页";
    private static final String HOMEPAGE_MANAGEMENT_GROUP_NAME = "管理首页";
    private static final String HOMEPAGE_MANAGEMENT_GROUP_COMMENT = "拥有当前资源的管理权限，支持向下分配权限";

    private static final String MENU_AUTH_GROUP_NAME = "管理所有数据";
    private static final String MENU_AUTH_GROUP_COMMENT = "拥有当前菜单下所有管理权限";
    private static final String MENU_MANAGEMENT_GROUP_NAME = "管理资源";
    private static final String MENU_MANAGEMENT_GROUP_COMMENT = "拥有当前资源的管理权限，支持向下分配权限";

    private static final String ACTION_AUTH_GROUP_NAME = "管理所有数据";
    private static final String ACTION_AUTH_GROUP_COMMENT = "拥有当前动作下所有管理权限";
    private static final String ACTION_MANAGEMENT_GROUP_NAME = "管理资源";
    private static final String ACTION_MANAGEMENT_GROUP_COMMENT = "拥有当前资源的管理权限，支持向下分配权限";

    private static final String MODULE_PREFIX = ResourcePermissionTypeEnum.MODULE.name() + CharacterConstants.SEPARATOR_OCTOTHORPE;

    private static final String HOMEPAGE_PREFIX = ResourcePermissionSubtypeEnum.HOMEPAGE.name() + CharacterConstants.SEPARATOR_OCTOTHORPE;

    private static final String MENU_PREFIX = ResourcePermissionTypeEnum.MENU.name() + CharacterConstants.SEPARATOR_OCTOTHORPE;

    private static final String ACTION_PREFIX = ResourcePermissionTypeEnum.ACTION.name() + CharacterConstants.SEPARATOR_OCTOTHORPE;

    public static String buildModuleAuthGroupName(String module, AuthGroupTypeEnum type) {
        return MODULE_PREFIX +
                type.name() +
                CharacterConstants.SEPARATOR_OCTOTHORPE +
                module;
    }

    public static AuthGroup buildModuleAuthGroup(String module, AuthGroupTypeEnum type) {
        AuthGroup data = new AuthGroup();
        data.setName(buildModuleAuthGroupName(module, type));
        data.setType(type);
        if (AuthGroupTypeEnum.MANAGEMENT.equals(type)) {
            data.setDisplayName(MODULE_MANAGEMENT_GROUP_NAME);
            data.setComment(MODULE_MANAGEMENT_GROUP_COMMENT);
        } else {
            data.setDisplayName(MODULE_AUTH_GROUP_NAME);
            data.setComment(MODULE_AUTH_GROUP_COMMENT);
        }
        data.setActive(Boolean.TRUE);
        data.setSource(AuthorizationSourceEnum.SYSTEM);
        return data;
    }

    public static String buildHomepageAuthGroupName(String module, AuthGroupTypeEnum type) {
        return HOMEPAGE_PREFIX +
                type.name() +
                CharacterConstants.SEPARATOR_OCTOTHORPE +
                module;
    }

    public static AuthGroup buildHomepageAuthGroup(String module, AuthGroupTypeEnum type) {
        AuthGroup data = new AuthGroup();
        data.setName(buildHomepageAuthGroupName(module, type));
        data.setType(type);
        if (AuthGroupTypeEnum.MANAGEMENT.equals(type)) {
            data.setDisplayName(HOMEPAGE_MANAGEMENT_GROUP_NAME);
            data.setComment(HOMEPAGE_MANAGEMENT_GROUP_COMMENT);
        } else {
            data.setDisplayName(HOMEPAGE_AUTH_GROUP_NAME);
            data.setComment(HOMEPAGE_AUTH_GROUP_COMMENT);
        }
        data.setActive(Boolean.TRUE);
        data.setSource(AuthorizationSourceEnum.SYSTEM);
        return data;
    }

    public static String buildMenuAuthGroupName(String module, String menuName, AuthGroupTypeEnum type) {
        return MENU_PREFIX +
                type.name() +
                CharacterConstants.SEPARATOR_OCTOTHORPE +
                module +
                CharacterConstants.SEPARATOR_OCTOTHORPE +
                menuName;
    }

    public static AuthGroup buildMenuAuthGroup(String module, String menuName, AuthGroupTypeEnum type) {
        AuthGroup data = new AuthGroup();
        data.setName(buildMenuAuthGroupName(module, menuName, type));
        data.setType(type);
        if (AuthGroupTypeEnum.MANAGEMENT.equals(type)) {
            data.setDisplayName(MENU_MANAGEMENT_GROUP_NAME);
            data.setComment(MENU_MANAGEMENT_GROUP_COMMENT);
        } else {
            data.setDisplayName(MENU_AUTH_GROUP_NAME);
            data.setComment(MENU_AUTH_GROUP_COMMENT);
        }
        data.setActive(Boolean.TRUE);
        data.setSource(AuthorizationSourceEnum.SYSTEM);
        data.setMenuName(menuName);
        return data;
    }

    public static String buildActionAuthGroupName(String model, String actionName, AuthGroupTypeEnum type) {
        return ACTION_PREFIX +
                type.name() +
                CharacterConstants.SEPARATOR_OCTOTHORPE +
                model +
                CharacterConstants.SEPARATOR_OCTOTHORPE +
                actionName;
    }

    public static AuthGroup buildActionAuthGroup(String model, String actionName, AuthGroupTypeEnum type) {
        AuthGroup data = new AuthGroup();
        data.setName(buildActionAuthGroupName(model, actionName, type));
        data.setType(type);
        if (AuthGroupTypeEnum.MANAGEMENT.equals(type)) {
            data.setDisplayName(ACTION_MANAGEMENT_GROUP_NAME);
            data.setComment(ACTION_MANAGEMENT_GROUP_COMMENT);
        } else {
            data.setDisplayName(ACTION_AUTH_GROUP_NAME);
            data.setComment(ACTION_AUTH_GROUP_COMMENT);
        }
        data.setActive(Boolean.TRUE);
        data.setSource(AuthorizationSourceEnum.SYSTEM);
        return data;
    }
}
