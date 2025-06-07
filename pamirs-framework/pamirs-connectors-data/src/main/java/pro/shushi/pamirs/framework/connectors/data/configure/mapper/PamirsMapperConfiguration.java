package pro.shushi.pamirs.framework.connectors.data.configure.mapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.connectors.data.api.service.DataSourceRouteService;
import pro.shushi.pamirs.framework.connectors.data.api.service.ModuleDsService;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableInfoDialectService;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.DynamicDsKeyComputer;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.LogicColumnFetcher;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.TableNameComputer;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.BatchOperation;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableConfig;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ORM配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-10 01:55
 */
@Configuration
@ConfigurationProperties(
        prefix = ConfigureConstants.PAMIRS_MAPPER_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsMapperConfiguration implements PamirsMapperConfigurationProxy {

    private static final BatchOperation DEFAULT_BATCH_OPERATION = new BatchOperation();

    private BatchCommitTypeEnum batch = BatchCommitTypeEnum.collectionCommit;

    private BatchOperation defaultBatchConfig;

    private Map<String/*model*/, BatchOperation> batchConfig;

    private PamirsDataConfiguration global;

    private ConcurrentHashMap<String/*dsKey*/, PamirsDataConfiguration> ds;

    private LogicColumnFetcher logicColumnFetcher = Spider.getDefaultExtension(LogicColumnFetcher.class);

    private DataSourceRouteService dataSourceRouteService = Spider.getDefaultExtension(DataSourceRouteService.class);

    private ModuleDsService moduleDsService = Spider.getDefaultExtension(ModuleDsService.class);

    private TableNameComputer tableNameComputer = Spider.getDefaultExtension(TableNameComputer.class);

    private DynamicDsKeyComputer dynamicDsKeyComputer = Spider.getDefaultExtension(DynamicDsKeyComputer.class);

    @Override
    public PamirsDataConfiguration fetchPamirsDataConfiguration(String dsKey) {
        return Optional.ofNullable(ds).map(v -> StringUtils.isBlank(dsKey) ? null : v.get(dsKey))
                .orElse(Optional.ofNullable(global).orElse(new PamirsDataConfiguration()));
    }

    @Override
    public PamirsTableConfig fetchPamirsTableConfig(String dsKey) {
        PamirsDataConfiguration pamirsDataConfiguration = fetchPamirsDataConfiguration(dsKey);
        PamirsTableInfo pamirsTableInfo = Optional.ofNullable(pamirsDataConfiguration).map(PamirsDataConfiguration::getTableInfo).orElse(null);
        pamirsTableInfo = Optional.ofNullable(pamirsTableInfo).orElse(new PamirsTableInfo());
        Boolean tableNameCaseSensitive = Optional.ofNullable(pamirsDataConfiguration)
                .map(PamirsDataConfiguration::isTableNameCaseSensitive).orElse(null);
        return pamirsTableInfo.generateConfig().setTableNameCaseSensitive(tableNameCaseSensitive).defaultValue(dsKey);
    }

    @Override
    public TableNameComputer fetchTableNameComputer() {
        return tableNameComputer;
    }

    @Override
    public DynamicDsKeyComputer fetchDynamicDsKeyComputer() {
        return dynamicDsKeyComputer;
    }

    @Override
    public BatchCommitTypeEnum batch() {
        return batch;
    }

    public BatchOperation defaultBatchConfig() {
        BatchOperation defaultBatchConfig = this.defaultBatchConfig;
        if (defaultBatchConfig == null) {
            return DEFAULT_BATCH_OPERATION;
        }
        return defaultBatchConfig;
    }

    @Override
    public Map<String/*model*/, BatchOperation> batchConfig() {
        return batchConfig;
    }

    @Override
    public BatchOperation batchOperationForModel(String model) {
        return Optional.ofNullable(batchConfig).map(v -> v.get(model)).orElse(defaultBatchConfig());
    }

    @Override
    public void fillDefaultConfig(String dsKey, PamirsTableInfo pamirsTableInfo) {
        Dialects.component(TableInfoDialectService.class, dsKey).fillDefaultConfig(dsKey, pamirsTableInfo);
    }

}
