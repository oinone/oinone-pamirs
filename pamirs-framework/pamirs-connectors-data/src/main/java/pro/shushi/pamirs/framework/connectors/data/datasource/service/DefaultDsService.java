package pro.shushi.pamirs.framework.connectors.data.datasource.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.api.service.DsService;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.DataSourceProtocolEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import javax.annotation.Resource;
import java.util.*;

/**
 * 数据源组件
 * <p>
 * 2020/9/3 11:30 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class DefaultDsService implements DsService {

    @Resource
    private ShardingDefineConfiguration shardingDefineConfiguration;

    @Resource
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    @Override
    public DataSourceProtocolEnum fetchProtocolFromDsKey(String dsKey) {
        String leafDsKey = fetchLeafDataSources(dsKey).iterator().next();
        DsDialectComponent dsDialectComponent = Objects.requireNonNull(Dialects.component(DsDialectComponent.class, leafDsKey));
        String protocol = dsDialectComponent.getProtocol(leafDsKey);
        if (StringUtils.isBlank(protocol)) {
            return null;
        }
        return Enums.getEnumByValue(DataSourceProtocolEnum.class, protocol);
    }

    @Override
    public Set<String> fetchLeafDataSources(String dsKey) {
        return fetchLeafDataSources(shardingDefineConfiguration.getDataSources(), dsKey);
    }

    @Override
    public Set<String> fetchLeafDataSources(Map<String, List<String>> shardingDataSourceMap, String dsKey) {
        Set<String> dsSet = new HashSet<>();
        if (null == shardingDataSourceMap || !shardingDataSourceMap.containsKey(dsKey)) {
            dsSet.add(dsKey);
        } else {
            List<String> dataSources = shardingDataSourceMap.get(dsKey);
            for (String ds : dataSources) {
                dsSet.addAll(fetchLeafDataSources(shardingDataSourceMap, ds));
            }
        }
        return dsSet;
    }

    @Override
    public Set<String/*dsKey*/> fetchDataSourceList(Meta meta) {
        return fetchDataSourceList(meta, null);
    }

    @Override
    public Set<String/*dsKey*/> fetchDataSourceList(Meta meta, Set<String> includeModules) {
        List<ModelDefinition> modelDefinitionList = meta.getCurrentModuleData().getModelList();
        return fetchDataSourceList(modelDefinitionList);
    }

    @Override
    public Set<String/*dsKey*/> fetchDataSourceList(List<ModelDefinition> modelDefinitionList) {
        return fetchDataSourceList(modelDefinitionList, null);
    }

    @Override
    public Set<String/*dsKey*/> fetchDataSourceList(List<ModelDefinition> modelDefinitionList, Set<String> includeModules) {
        Set<String> dsSet = new HashSet<>();
        if (null == modelDefinitionList) {
            return dsSet;
        }
        for (ModelDefinition modelDefinition : modelDefinitionList) {
            if (!ModelTypeEnum.STORE.equals(modelDefinition.getType())) {
                continue;
            }
            if (null != includeModules && !includeModules.contains(modelDefinition.getDsModule())) {
                continue;
            }
            String dsKey = Optional.ofNullable(modelDefinition.getCompletedDsKey()).orElse(pamirsFrameworkDataConfiguration.getDefaultDsKey());
            dsSet.addAll(fetchLeafDataSources(dsKey));
        }
        return dsSet;
    }

    @Override
    public Map<String/*schema*/, String/*ds*/> fetchSystemDatabaseMap(String dsKey) {
        return fetchSystemDatabaseMap(fetchLeafDataSources(dsKey));
    }

    @Override
    public Map<String/*schema*/, String/*ds*/> fetchSystemDatabaseMap(Set<String> leafDsKeys) {
        Map<String/*schema*/, String/*ds*/> schemaMap = new HashMap<>();
        for (String dsKey : leafDsKeys) {
            DsDialectComponent dsDialectComponent = Objects.requireNonNull(Dialects.component(DsDialectComponent.class, dsKey));
            String database = dsDialectComponent.getDatabase(dsKey);
            if (StringUtils.isBlank(database)) {
                continue;
            }
            schemaMap.put(database, dsKey);
        }
        return schemaMap;
    }

    @Override
    public Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> fetchDatabaseMap(String dsKey) {
        return fetchDatabaseMap(fetchLeafDataSources(dsKey));
    }

    @Override
    public Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> fetchDatabaseMap(Set<String> leafDsKeys) {
        Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> databaseMap = new HashMap<>();
        for (String dsKey : leafDsKeys) {
            DsDialectComponent dsDialectComponent = Objects.requireNonNull(Dialects.component(DsDialectComponent.class, dsKey));
            DataSourceProtocolEnum protocolEnum = fetchProtocolFromDsKey(dsKey);
            if (null == protocolEnum) {
                continue;
            }
            String database = dsDialectComponent.getDatabase(dsKey);
            Map<String/*schema*/, String/*ds*/> schemaMap = databaseMap.get(protocolEnum);
            if (null == schemaMap) {
                databaseMap.put(protocolEnum, new HashMap<>());
                schemaMap = databaseMap.get(protocolEnum);
            }
            if (StringUtils.isNotBlank(database)) {
                schemaMap.put(database, dsKey);
            }
        }
        return databaseMap;
    }

}
