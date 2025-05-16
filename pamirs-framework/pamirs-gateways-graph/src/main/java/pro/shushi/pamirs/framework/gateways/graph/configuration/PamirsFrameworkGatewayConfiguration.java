package pro.shushi.pamirs.framework.gateways.graph.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 框架网关配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-10 01:55
 */
@Configuration
@ConfigurationProperties(
        prefix = ConfigureConstants.PAMIRS_FRAMEWORK_GATEWAY_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsFrameworkGatewayConfiguration {

    private boolean statistics;

    private boolean showDoc;

    private boolean async;

    private boolean buildAll;

}
