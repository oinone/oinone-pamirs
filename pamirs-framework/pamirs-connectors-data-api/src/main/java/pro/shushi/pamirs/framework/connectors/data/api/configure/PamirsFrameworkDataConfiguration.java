package pro.shushi.pamirs.framework.connectors.data.api.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static pro.shushi.pamirs.framework.common.utils.DataShardingHelper.DEFAULT_EACH_SHARD_MAX;

/**
 * 框架数据配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-10 01:55
 */
@Configuration
@ConfigurationProperties(
        prefix = ConfigureConstants.PAMIRS_FRAMEWORK_DATA_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsFrameworkDataConfiguration {

    @NotBlank
    private int eachShardMax = DEFAULT_EACH_SHARD_MAX;

    @NotBlank
    private String defaultDsKey = ModuleConstants.MODULE_BASE;

    private Map<String/*module*/, String/*dsKey*/> dsMap = new HashMap<>();

    private Map<String/*model*/, String/*dsKey*/> modelDsMap = new HashMap<>();

    public String getDefaultDsKey() {
        return DataPrefixManager.dsPrefix(null, null, this.defaultDsKey);
    }

    public String getOriginDefaultDsKey() {
        return this.defaultDsKey;
    }

}
