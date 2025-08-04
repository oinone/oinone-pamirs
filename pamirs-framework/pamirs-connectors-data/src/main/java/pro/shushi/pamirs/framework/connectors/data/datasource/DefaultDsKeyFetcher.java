package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DsKeyFetcher;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 数据表配置计算抽象类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Component
public class DefaultDsKeyFetcher implements DsKeyFetcher {

    @Resource
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    @Resource
    private ShardingDefineConfiguration shardingDefineConfiguration;

    @Override
    public String fetchLogicDsKey(String dsKey) {
        return StringUtils.isBlank(dsKey) ? pamirsFrameworkDataConfiguration.getDefaultDsKey() : dsKey;
    }

    @Override
    public String fetchLogicDsKeyForDDL(String dsKey) {
        if (StringUtils.isBlank(dsKey)) {
            return pamirsFrameworkDataConfiguration.getDefaultDsKey();
        } else {
            Map<String, List<String>> dataSources = shardingDefineConfiguration.getDataSources();
            if (!CollectionUtils.isEmpty(dataSources)) {
                List<String> dsKeys = dataSources.get(dsKey);
                if (null != dsKeys && dsKeys.size() == 1) {
                    return dsKeys.get(0);
                }
            }
            return dsKey;
        }
    }

}
