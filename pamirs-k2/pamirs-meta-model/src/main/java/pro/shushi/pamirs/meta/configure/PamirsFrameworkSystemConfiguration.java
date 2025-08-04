package pro.shushi.pamirs.meta.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;

/**
 * 框架系统配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-10 01:55
 */
@Configuration
@ConfigurationProperties(
        prefix = PamirsFrameworkSystemConfiguration.PAMIRS_FRAMEWORK_SYSTEM_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsFrameworkSystemConfiguration {

    public final static String PAMIRS_FRAMEWORK_SYSTEM_CONFIG_PREFIX = "pamirs.framework.system";

    private String systemDsKey = ModuleConstants.MODULE_BASE;

    private String[] systemModels;

    private String[] staticModelConfigLocations = new String[]{PackageConstants.PACKAGE_PAMIRS};

    private String isolationKey;

    public String getSystemDsKey() {
        return DataPrefixManager.dsPrefix(ModuleConstants.MODULE_SYSTEM, null, this.systemDsKey);
    }

    public String getOriginSystemDsKey() {
        return this.systemDsKey;
    }

}
