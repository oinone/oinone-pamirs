package pro.shushi.pamirs.auth.api.constants;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;

/**
 * 常量
 *
 * @author Adamancy Zhang at 19:29 on 2024-01-04
 */
public interface AuthConstants {

    Long SUPER_ROLE_ID = 10001L;
    String SUPER_ROLE_CODE = "R00001";
    String SUPER_ROLE_NAME = "SystemRole.super_role_name";
    String SUPER_ROLE_COMMENT = "SystemRole.super_role_comment";

    Long BUSINESS_ROLE_ID = 10002L;
    String BUSINESS_ROLE_CODE = "R00002";
    String BUSINESS_ROLE_NAME = "SystemRole.business_role_name";
    String BUSINESS_ROLE_COMMENT = "SystemRole.business_role_comment";

    Long BASE_ROLE_ID = 10003L;
    String BASE_ROLE_CODE = "R00003";
    String BASE_ROLE_NAME = "SystemRole.base_role_name";
    String BASE_ROLE_COMMENT = "SystemRole.base_role_comment";

    Long SYSTEM_ROLE_TYPE_ID = 10000L;
    String SYSTEM_ROLE_TYPE_CODE = "SYSTEM";
    String SYSTEM_ROLE_TYPE_NAME = "SystemRoleType.system_role_type_name";

    String REDIS_TEMPLATE_BEAN_NAME = "authRedisTemplate";

    String AUTH_CACHE_KEY_PREFIX = "pamirs:auth:";

    String CURRENT_ROLES_CACHE_KEY_PREFIX = AUTH_CACHE_KEY_PREFIX + "role:";

    String SHARED_CODE_CACHE_KEY_PREFIX = AUTH_CACHE_KEY_PREFIX + "shared:";

    String ANONYMOUS_USER_CACHE_KEY_PREFIX = AUTH_CACHE_KEY_PREFIX + "anonymous:";

    String AUTH_PATH_MAPPING_CACHE_KEY_PREFIX = AUTH_CACHE_KEY_PREFIX + "path:";

    String RESOURCE_PERMISSION_KEY = "resource";

    String DATA_PERMISSION_KEY = "data";

    String MODULE_TYPE = ResourcePermissionTypeEnum.MODULE.value().toLowerCase();

    String HOMEPAGE_TYPE = ResourcePermissionSubtypeEnum.HOMEPAGE.value().toLowerCase();

    String MENU_TYPE = ResourcePermissionTypeEnum.MENU.value().toLowerCase();

    String VIEW_TYPE = ResourcePermissionTypeEnum.VIEW.value().toLowerCase();

    String ACTION_TYPE = ResourcePermissionTypeEnum.ACTION.value().toLowerCase();

    String MODEL_TYPE = "model";

    String FIELD_TYPE = "field";

    String ROW_TYPE = "row";

    String ACCESS_LOADER_BEAN_NAME = "pamirsAccessResourcePermissionLoader";

    String MANAGEMENT_LOADER_BEAN_NAME = "pamirsManagementResourcePermissionLoader";

    String MANAGEMENT_CACHE_LOADER_BEAN_NAME = "pamirsManagementResourcePermissionCacheLoader";

    String RESOURCE_PERMISSION_NODE_CONVERTER_BEAN_NAME = "pamirsResourcePermissionNodeConverter";

    String RESOURCE_PERMISSION_PATH_GENERATOR_BEAN_NAME = "pamirsResourcePermissionPathGenerator";

    String ALL_FLAG_STRING = ResourcePath.ALL_FLAG;

    Long ALL_FLAG_LONG = -1L;

    String ALL_FLAG_DISPLAY_NAME = "AuthConstants.AllFlag.DisplayName";

    String ALL_FLAG_FIELD_DESCRIPTION = "AuthConstants.AllFlag.FieldDescription";

    String ALL_FLAG_PATH_SUFFIX = ResourcePath.PATH_SPLIT + ResourcePath.ALL_FLAG;

    String HOMEPAGE_PATH_SUFFIX = ResourcePath.PATH_SPLIT + HOMEPAGE_TYPE;

    String TABLE_EDITABLE_UPDATE = "AuthConstants.TableEditable.Update";
}
