package pro.shushi.pamirs.framework.faas.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.faas.spi.api.remote.RemoteRegistryStrategyApi;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 泛化范围的配置项
 *
 * @author cpc on 2024-03-26
 * @version 1.0.0
 */
@Configuration
@ConfigurationProperties(
        prefix = ConfigureConstants.PAMIRS_DISTRIBUTION_SERVICE_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsFrameworkRemoteRegistryConfiguration {

    //泛化服务范围，可选值：module、namespace
    private String serviceScope = RemoteRegistryStrategyApi.SCOPE_MODULE;

}
