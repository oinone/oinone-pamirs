package pro.shushi.pamirs.framework.connectors.data.infrastructure.api;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.meta.enmu.DataSourceProtocolEnum;

import java.util.Map;

/**
 * 基础设施元数据接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
public interface SchemaMetaService {

    /**
     * 查询表结构，不支持异构数据源
     *
     * @param databaseMap   数据源map
     * @param modelTableMap 表结构元数据
     * @param supportDrop   是否支持drop操作
     * @return 表结构
     */
    Map<String/*schema#table*/, LogicTable> fetchLogicTableMap(Map<String/*schema*/, String/*ds*/> databaseMap,
                                                               Map<String/*schema#table*/, ModelTable> modelTableMap,
                                                               boolean supportDrop);

    /**
     * 查询表元数据，不支持异构数据源
     *
     * @param databaseMap 数据源map
     * @param supportDrop 支持drop操作
     * @return 表元数据
     */
    Map<String/*schema#table*/, ModelTable> fetchModelTableMap(Map<String/*schema*/, String/*ds*/> databaseMap, boolean supportDrop);

    /**
     * 查询表结构，支持异构数据源
     *
     * @param dsKey         数据源
     * @param modelTableMap 表结构元数据
     * @param supportDrop   是否支持drop操作
     * @return 表结构
     */
    Map<DataSourceProtocolEnum, Map<String/*schema#table*/, LogicTable>> fetchLogicTableMap(String dsKey,
                                                                                            Map<String/*schema#table*/, ModelTable> modelTableMap,
                                                                                            boolean supportDrop);

    /**
     * 查询表元数据，支持异构数据源
     *
     * @param dsKey       数据源
     * @param supportDrop 是否支持drop操作
     * @return 表元数据
     */
    Map<DataSourceProtocolEnum, Map<String/*schema#table*/, ModelTable>> fetchModelTableMap(String dsKey, boolean supportDrop);

}
