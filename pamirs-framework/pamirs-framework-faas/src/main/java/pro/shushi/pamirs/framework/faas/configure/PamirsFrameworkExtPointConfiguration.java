package pro.shushi.pamirs.framework.faas.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.Set;

/**
 * ExtPoint 拦截器配置
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2023-10-10
 */
@Configuration
@ConfigurationProperties(
        prefix = ConfigureConstants.PAMIRS_FRAMEWORK_EXTPOINT_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsFrameworkExtPointConfiguration {

    private Set<String> excludes;

    private boolean supportRemote = true;
}
