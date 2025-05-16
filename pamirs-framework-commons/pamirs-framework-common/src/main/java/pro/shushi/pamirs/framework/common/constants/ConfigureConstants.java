package pro.shushi.pamirs.framework.common.constants;

/**
 * spring yml配置文件常量
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 17:08
 */
public interface ConfigureConstants {

    String PAMIRS_BOOT_CONFIG_PREFIX = "pamirs.boot";

    String PAMIRS_MOCK_CONFIG_PREFIX = "pamirs.mock";

    String PAMIRS_META_CONFIG_PREFIX = "pamirs.meta";

    String PAMIRS_FRAMEWORK_GATEWAY_CONFIG_PREFIX = "pamirs.framework.gateway";

    String PAMIRS_FRAMEWORK_HOOK_CONFIG_PREFIX = "pamirs.framework.hook";

    String PAMIRS_FRAMEWORK_EXTPOINT_CONFIG_PREFIX = "pamirs.framework.extpoint";

    String PAMIRS_FRAMEWORK_DATA_CONFIG_PREFIX = "pamirs.framework.data";

    String DIALECT_DATASOURCE_PREFIX = "pamirs.dialect.ds";

    String PAMIRS_PERSISTENCE_CONFIG_PREFIX = "pamirs.persistence";

    String DATASOURCE_CONFIG_PREFIX = "pamirs.datasource";

    String SHARDING_CONFIG_PREFIX = "pamirs.sharding";

    String SHARDING_RULE_CONFIG_PREFIX = SHARDING_CONFIG_PREFIX + ".rule";

    String SHARDING_DEFINE_CONFIG_PREFIX = SHARDING_CONFIG_PREFIX + ".define";

    String MY_BATIS_PLUS_ENHANCE_CONFIG_PREFIX = "pamirs.plus";

    String PAMIRS_MAPPER_CONFIG_PREFIX = "pamirs.mapper";

    String PAMIRS_EVENT_CONFIG_PREFIX = "pamirs.event";

    String PAMIRS_AUTH_CONFIG_PREFIX = "pamirs.auth";

    String PAMIRS_DISTRIBUTION_CONFIG_PREFIX = "pamirs.distribution";

    String PAMIRS_DISTRIBUTION_SERVICE_CONFIG_PREFIX = PAMIRS_DISTRIBUTION_CONFIG_PREFIX + ".service";

    String PAMIRS_DISTRIBUTION_SESSION_CONFIG_PREFIX = PAMIRS_DISTRIBUTION_CONFIG_PREFIX + ".session";

}
