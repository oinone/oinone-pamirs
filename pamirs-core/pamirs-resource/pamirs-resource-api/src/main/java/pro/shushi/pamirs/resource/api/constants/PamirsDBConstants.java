package pro.shushi.pamirs.resource.api.constants;

public interface PamirsDBConstants {

    String PAMIRS = "pamirs";

    String pamirsSplitor = "$";

    String springSplitor = "-";

    String FIELD_TENANCY = "tenancy";

    String FIELD_TENANCY_ID = "tenancyId";

    String FIELD_CODE = "code";

    String FIELD_MODULE_NAME = "moduleName";

    String FIELD_MODEL = "model";

    String FIELD_MODULE = "module";

    String FIELD_RES_ID = "resId";

    String FIELD_DEPENDENCY_MODULE_NAME = "dependencyModuleName";

    String FIELD_EXCLUDE_MODULE_NAME = "excludeModuleName";

    String FIELD_TYPE = "type";

    String FIELD_OPERATION = "operation";

    String FIELD_DATA_SOURCE_ID = "dataSourcesId";

    String FIELD_DATA_SOURCES = "dataSources";

    String FIELD_SUB_TYPE = "subType";

    String FIELD_DRIVER = "driver";

    String FIELD_IP = "ip";

    String FIELD_PORT = "port";

    String FIELD_URL = "url";

    String FIELD_PROTOCOL = "protocol";

    String FIELD_DATABASE = "database";

    String FIELD_USERNAME = "username";

    String FIELD_PASSWORD = "password";

    String FIELD_INITIAL_POOL_SIZE = "initialPoolSize";

    String FIELD_MIN_POOL_SIZE = "minPoolSize";

    String FIELD_MAX_POOL_SIZE = "maxPoolSize";

    String FIELD_MAX_WAIT_MILLS = "maxWaitMills";

    String FIELD_NAME = "name";

    String FIELD_DISPLAY_NAME = "displayName";

    String FIELD_MODULES = "modules";

    String COLUMN_WRITE_DATE = "write_date";

    String COLUMN_MODULE = "module";

    String POOL_DRIVER = "driverClassName";

    String POOL_URL = "url";

    String POOL_USERNAME = "username";

    String POOL_PASSWORD = "password";

    String POOL_INITIAL_POOL_SIZE = "initialSize";

    String POOL_MIN_POOL_SIZE = "minIdle";

    String POOL_MAX_POOL_SIZE = "maxActive";

    String POOL_MAX_WAIT_MILLS = "maxWait";

    String JDBC_PROTOCAL = "jdbc:mysql://";

    /**
     * 表名
     */
    String MACHINE_GROUP_REL_TAB = "machine_instance_pamirs_module_group_rel";

    String MODULE_GROUP_REL_TAB = "module_pamirs_module_group_rel";

    String TENANCY_GROUP_REL_TAB = "module_group_pamirs_tenancy_rel";

    String MACHINE_ALLOCATION_PAMIRS_TENANCY_REL = "machine_allocation_pamirs_tenancy_rel";

    String defaultTxPrefix = "pamirsTransactionManager";

    String xaTxManagerName = "pamirsTransactionManager";

    String FIELD_IS_DEAL = "isDeal";

    String FIELD_LIFECYCLE_DATE = "lifecycleDate";

    String FIELD_VERSION = "version";

    String FIELD_MACHINE_ALLOCATION_ID = "machineAllocationId";

    String FIELD_MIDDLE_WARES_GROUP = "middleWaresGroup";

    String FIELD_MIDDLE_WARES = "middleWares";

    String FIELD_MIDDLE_WARES_GROUP_ID = "middleWaresGroupId";

    String FIELD_GROUP_NAME = "groupName";

    String FIELD_CATEGORY_NAME = "categoryName";

    String FIELD_CATEGORY_ID = "categoryId";

    String FIELD_CATEGORY = "category";
}
