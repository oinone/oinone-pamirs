package pro.shushi.pamirs.framework.connectors.data.configure.sharding;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingSpecificConfiguration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * sharding规则配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 17:07
 */
@Configuration
@ConfigurationProperties(prefix = ConfigureConstants.SHARDING_RULE_CONFIG_PREFIX)
@RefreshScope
public class ShardingRuleConfiguration extends ConcurrentHashMap<String, ShardingSpecificConfiguration> {

    private static final long serialVersionUID = -8440146417228488953L;

}
