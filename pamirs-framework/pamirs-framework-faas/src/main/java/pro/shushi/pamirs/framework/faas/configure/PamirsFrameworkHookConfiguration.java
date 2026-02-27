package pro.shushi.pamirs.framework.faas.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Hook 拦截器配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-10 01:55
 */
@Configuration
@ConfigurationProperties(
        prefix = ConfigureConstants.PAMIRS_FRAMEWORK_HOOK_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsFrameworkHookConfiguration {

    private boolean ignoreAll;

    private Set<String> excludes = new HashSet<>();

}
