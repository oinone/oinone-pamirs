package pro.shushi.pamirs.framework.connectors.data.xa.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 框架数据配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-10 01:55
 */
@Configuration
@ConfigurationProperties(
        prefix = PamirsXaConfiguration.PREFIX
)
@RefreshScope
@Data
public class PamirsXaConfiguration {

    public static final String PREFIX = "pamirs.xa";

    private boolean forceShutdown = Boolean.TRUE;

    private int timeout = 5000;

}
