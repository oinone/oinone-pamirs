package pro.shushi.pamirs.framework.connectors.data.configure.persistence;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsPersistenceConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsPersistenceItemConfiguration;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 持久层配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-10 01:55
 */
@Configuration
@ConfigurationProperties(
        prefix = ConfigureConstants.PAMIRS_PERSISTENCE_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsPersistenceConfiguration implements PamirsPersistenceConfigurationProxy {

    private PamirsPersistenceItemConfiguration global;

    private ConcurrentHashMap<String/*dsKey*/, PamirsPersistenceItemConfiguration> ds;

    @Override
    public PamirsPersistenceItemConfiguration fetchPamirsPersistenceConfiguration(String dsKey) {
        return Optional.ofNullable(ds).map(v -> StringUtils.isBlank(dsKey) ? null : v.get(dsKey))
                .orElse(Optional.ofNullable(global).orElse(new PamirsPersistenceItemConfiguration()));
    }

    @Override
    public boolean isSharding(String module, String model) {
        ShardingDefineConfiguration shardingDefineConfiguration = CommonApiFactory.getApi(ShardingDefineConfiguration.class);
        if (null == shardingDefineConfiguration) {
            return false;
        }
        return shardingDefineConfiguration.isSharding(module, model);
    }

}
