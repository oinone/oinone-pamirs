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
    String SUPER_ROLE_NAME = "超级管理员";
    String SUPER_ROLE_COMMENT = "系统角色,拥有系统所有的权限";

    Long BUSINESS_ROLE_ID = 10002L;
    String BUSINESS_ROLE_CODE = "R00002";
    String BUSINESS_ROLE_NAME = "系统基础角色";
    String BUSINESS_ROLE_COMMENT = "系统角色,拥有系统基础角色的权限";

    Long BASE_ROLE_ID = 10003L;
    String BASE_ROLE_CODE = "R00003";
    String BASE_ROLE_NAME = "平台基础角色";
    String BASE_ROLE_COMMENT = "系统角色,平台的基础角色";

    Long SYSTEM_ROLE_TYPE_ID = 10000L;
    String SYSTEM_ROLE_TYPE_CODE = "SYSTEM";
    String SYSTEM_ROLE_TYPE_NAME = "系统角色";

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

    String ALL_FLAG_DISPLAY_NAME = "全部";

    String ALL_FLAG_FIELD_DESCRIPTION = "授予当前模型下所有字段权限";

    String ALL_FLAG_PATH_SUFFIX = ResourcePath.PATH_SPLIT + ResourcePath.ALL_FLAG;

    String HOMEPAGE_PATH_SUFFIX = ResourcePath.PATH_SPLIT + HOMEPAGE_TYPE;

    String TABLE_EDITABLE_UPDATE = "行内编辑更新";

    @Deprecated
    String SUPER_ROLE = SUPER_ROLE_NAME;
    @Deprecated
    String SUPER_CODE = SUPER_ROLE_CODE;
    @Deprecated
    String BUSINESS_BASE_ROLE = BUSINESS_ROLE_NAME;
    @Deprecated
    String BUSINESS_BASE_CODE = BUSINESS_ROLE_CODE;
    @Deprecated
    String ROLE_SYSTEM_TYPE_CODE = SYSTEM_ROLE_TYPE_CODE;
    @Deprecated
    String ROLE_SYSTEM_TYPE = SYSTEM_ROLE_TYPE_NAME;
    @Deprecated
    String BASE_ROLE = BASE_ROLE_NAME;
}
