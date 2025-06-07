package pro.shushi.pamirs.framework.connectors.data.configure.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@Configuration
@ConfigurationProperties(prefix = ConfigureConstants.DATASOURCE_CONFIG_PREFIX)
@RefreshScope
public class DataSourceConfiguration extends ConcurrentHashMap<String, Map<String, String>> {

    private static final long serialVersionUID = 256885056170381863L;

}
