package pro.shushi.pamirs.boot.standard.checker.environment;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.checker.helper.DataSourceEnvironmentHelper;
import pro.shushi.pamirs.boot.standard.checker.helper.DataSourceUrlChecker;
import pro.shushi.pamirs.boot.standard.checker.helper.StringArrayChecker;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.datasource.DataSourceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.List;
import java.util.Map;

/**
 * 数据源环境检查
 *
 * @author Adamancy Zhang at 18:18 on 2024-10-12
 */
@Component
public class DataSourceEnvironmentChecker extends AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    private static final String PAMIRS_MAPPER_DS_KEY_PREFIX_FORMAT = ConfigureConstants.PAMIRS_MAPPER_CONFIG_PREFIX + ".ds[%s].";

    private static final String PAMIRS_DATASOURCE_URL_KEY_FORMAT = "pamirs.datasource[%s].url";

    @Autowired
    private PamirsFrameworkSystemConfiguration pamirsFrameworkSystemConfiguration;

    @Autowired
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    @Autowired
    private PamirsMapperConfiguration pamirsMapperConfiguration;

    @Autowired
    private DataSourceConfiguration dataSourceConfiguration;

    @Autowired
    private BootConfiguration bootConfiguration;

    @Override
    protected EnvironmentKeySet propertyKeys() {
        PamirsDataConfiguration globalDataConfiguration = getGlobalDataConfiguration();

        EnvironmentKeySet keys = newEnvironmentKeySet(EnvironmentKey.Level.IMMUTABLE,
                "pamirs.framework.system.isolation-key",
                EnvironmentKey.immutable("pamirs.framework.system.system-ds-key", ModuleConstants.MODULE_BASE),
                EnvironmentKey.immutable("pamirs.meta.relation.pk-id", Boolean.FALSE.toString()),
                generatorMapperConfigurationKeySet("pamirs.mapper.global.", globalDataConfiguration)
        );

        for (String dsKey : dataSourceConfiguration.keySet()) {
            String keyPrefix = String.format(PAMIRS_MAPPER_DS_KEY_PREFIX_FORMAT, dsKey);
            keys.addAll(generatorMapperConfigurationKeySet(keyPrefix, globalDataConfiguration));
        }

        String systemDsKey = pamirsFrameworkSystemConfiguration.getOriginSystemDsKey();
        keys.add(EnvironmentKey.immutable(String.format(PAMIRS_DATASOURCE_URL_KEY_FORMAT, systemDsKey), new DataSourceUrlChecker(true)));

        String defaultDsKey = pamirsFrameworkDataConfiguration.getOriginDefaultDsKey();
        keys.add(EnvironmentKey.immutable(String.format(PAMIRS_DATASOURCE_URL_KEY_FORMAT, defaultDsKey), new DataSourceUrlChecker(true)));

        for (String dsKey : dataSourceConfiguration.keySet()) {
            if (!systemDsKey.equals(dsKey) && !defaultDsKey.equals(dsKey)) {
                keys.add(EnvironmentKey.immutable(String.format(PAMIRS_DATASOURCE_URL_KEY_FORMAT, dsKey), new DataSourceUrlChecker(false)));
            }
        }

        return keys;
    }

    private EnvironmentKeySet generatorMapperConfigurationKeySet(String keyPrefix, PamirsDataConfiguration globalDataConfiguration) {
        PamirsTableInfo globalTableInfo = globalDataConfiguration.getTableInfo();
        return newEnvironmentKeySet(EnvironmentKey.Level.IMMUTABLE,
                EnvironmentKey.immutable(keyPrefix + "database-format", globalDataConfiguration.getDatabaseFormat()),
                EnvironmentKey.addOrDelete(keyPrefix + "table-format", globalDataConfiguration.getTableFormat()),
                EnvironmentKey.immutable(keyPrefix + "table-pattern", globalDataConfiguration.getTablePattern()),
                EnvironmentKey.immutable(keyPrefix + "column-pattern", globalDataConfiguration.getColumnPattern()),
                EnvironmentKey.immutable(keyPrefix + "table-info.logic-delete", String.valueOf(globalTableInfo.getLogicDelete())),
                EnvironmentKey.immutable(keyPrefix + "table-info.logic-delete-column", globalTableInfo.getLogicDeleteColumn()),
                EnvironmentKey.immutable(keyPrefix + "table-info.logic-delete-value", globalTableInfo.getLogicDeleteValue()),
                EnvironmentKey.immutable(keyPrefix + "table-info.logic-not-delete-value", globalTableInfo.getLogicNotDeleteValue()),
                EnvironmentKey.immutable(keyPrefix + "table-info.key-generator", globalTableInfo.getKeyGenerator()),
                EnvironmentKey.immutable(keyPrefix + "table-info.under-camel", String.valueOf(globalTableInfo.getUnderCamel())),
                EnvironmentKey.immutable(keyPrefix + "table-info.capital-mode", String.valueOf(globalTableInfo.getCapitalMode())),
                EnvironmentKey.immutable(keyPrefix + "table-info.column-format", globalTableInfo.getColumnFormat()),
                EnvironmentKey.addOrDelete(keyPrefix + "table-info.table-format", globalTableInfo.getTableFormat()),
                EnvironmentKey.immutable(keyPrefix + "table-info.alias-format", globalTableInfo.getAliasFormat()),
                EnvironmentKey.immutable(keyPrefix + "table-info.charset", globalTableInfo.getCharset()),
                EnvironmentKey.immutable(keyPrefix + "table-info.collate", globalTableInfo.getCollate()));
    }

    @Override
    protected EnvironmentKeySet errorKeys() {
        EnvironmentKeySet keys = newEnvironmentKeySet(EnvironmentKey.Level.ERROR,
                EnvironmentKey.error("pamirs.mapper.global.table-name-case-sensitive")
        );
        for (String dsKey : dataSourceConfiguration.keySet()) {
            String keyPrefix = String.format(PAMIRS_MAPPER_DS_KEY_PREFIX_FORMAT, dsKey);
            keys.add(EnvironmentKey.error(keyPrefix + "table-name-case-sensitive"));
        }
        return keys;
    }

    @Override
    protected EnvironmentKeySet deprecatedKeys() {
        EnvironmentKeySet keys = newEnvironmentKeySet(EnvironmentKey.Level.DEPRECATED,
                EnvironmentKey.deprecated("pamirs.mapper.global.table-name-case-insensitive")
        );
        for (String dsKey : dataSourceConfiguration.keySet()) {
            String keyPrefix = String.format(PAMIRS_MAPPER_DS_KEY_PREFIX_FORMAT, dsKey);
            keys.add(EnvironmentKey.error(keyPrefix + "table-name-case-insensitive"));
        }
        return keys;
    }

    @Override
    public List<PlatformEnvironment> collection() {
        List<PlatformEnvironment> environments = super.collection();

        appendImmutableEnvironments(environments);

        return environments;
    }

    @Override
    public EnvironmentKey getKey(PlatformEnvironment environment) {
        String key = environment.getKey();
        if (DataSourceEnvironmentHelper.getEnvironmentDsKey(key) != null) {
            return EnvironmentKey.immutable(key, new DataSourceUrlChecker(false));
        }
        return super.getKey(environment);
    }

    private void appendImmutableEnvironments(List<PlatformEnvironment> environments) {
        appendSystemConfigurationEnvironments(environments);
        appendDsMapEnvironments(environments);
    }

    private void appendSystemConfigurationEnvironments(List<PlatformEnvironment> environments) {
        environments.add(generatorImmutableEnvironmentProperty(
                EnvironmentKey.immutable("pamirs.framework.system.systemModels", StringArrayChecker.ONLY_ADD).getKey(),
                pamirsFrameworkSystemConfiguration.getSystemModels())
        );
        environments.add(generatorImmutableEnvironmentProperty(
                EnvironmentKey.immutable("pamirs.framework.system.staticModelConfigLocations", StringArrayChecker.ONLY_ADD).getKey(),
                pamirsFrameworkSystemConfiguration.getStaticModelConfigLocations())
        );
    }

    private void appendDsMapEnvironments(List<PlatformEnvironment> environments) {
        final String modelDsMapKeyFormat = "pamirs.framework.data.modelDsMap[%s]";
        Map<String, String> modelDsMap = pamirsFrameworkDataConfiguration.getModelDsMap();
        if (MapUtils.isNotEmpty(modelDsMap)) {
            for (Map.Entry<String, String> entry : modelDsMap.entrySet()) {
                String model = entry.getKey();
                String dsKey = entry.getValue();
                environments.add(generatorImmutableEnvironmentProperty(String.format(modelDsMapKeyFormat, model), dsKey));
            }
        }

        final String dsMapKeyFormat = "pamirs.framework.data.dsMap[%s]";

        Map<String, String> dsMap = pamirsFrameworkDataConfiguration.getDsMap();
        String defaultDsKey = pamirsFrameworkDataConfiguration.getOriginDefaultDsKey();

        for (String module : bootConfiguration.getModules()) {
            String dsKey = dsMap.get(module);
            if (StringUtils.isBlank(dsKey)) {
                dsKey = defaultDsKey;
            }
            environments.add(generatorImmutableEnvironmentProperty(String.format(dsMapKeyFormat, module), dsKey));
        }
    }

    private PamirsDataConfiguration getGlobalDataConfiguration() {
        PamirsDataConfiguration dataConfiguration = pamirsMapperConfiguration.getGlobal();
        if (dataConfiguration == null) {
            dataConfiguration = new PamirsDataConfiguration();
        } else {
            dataConfiguration = ObjectUtils.clone(dataConfiguration);
        }
        PamirsTableInfo tableInfo = dataConfiguration.getTableInfo();
        if (tableInfo == null) {
            tableInfo = new PamirsTableInfo();
        } else {
            tableInfo = ObjectUtils.clone(tableInfo);
        }
        tableInfo.defaultValue(pamirsFrameworkSystemConfiguration.getOriginSystemDsKey());
        dataConfiguration.setTableInfo(tableInfo);
        return dataConfiguration;
    }
}
