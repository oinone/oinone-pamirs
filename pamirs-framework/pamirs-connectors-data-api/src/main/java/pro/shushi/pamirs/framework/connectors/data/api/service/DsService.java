package pro.shushi.pamirs.framework.connectors.data.api.service;

import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.DataSourceProtocolEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据源组件接口
 * <p>
 * 2020/9/3 11:30 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface DsService {

    DataSourceProtocolEnum fetchProtocolFromDsKey(String dsKey);

    Set<String> fetchLeafDataSources(String dsKey);

    Set<String> fetchLeafDataSources(Map<String, List<String>> shardingDataSourceMap, String dsKey);

    Set<String/*dsKey*/> fetchDataSourceList(Meta meta);

    Set<String/*dsKey*/> fetchDataSourceList(Meta meta, Set<String> includeModules);

    Set<String/*dsKey*/> fetchDataSourceList(List<ModelDefinition> modelDefinitionList);

    Set<String/*dsKey*/> fetchDataSourceList(List<ModelDefinition> modelDefinitionList, Set<String> includeModules);

    Map<String/*schema*/, String/*ds*/> fetchSystemDatabaseMap(String dsKey);

    Map<String/*schema*/, String/*ds*/> fetchSystemDatabaseMap(Set<String> leafDsKeys);

    Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> fetchDatabaseMap(String dsKey);

    Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> fetchDatabaseMap(Set<String> leafDsKeys);

}
