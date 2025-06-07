package pro.shushi.pamirs.framework.connectors.data.infrastructure.service;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DsHintApi;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModuleIndex;
import pro.shushi.pamirs.framework.connectors.data.api.service.DsService;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.SchemaUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.SchemaMetaService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.MetaDataDialectService;
import pro.shushi.pamirs.framework.connectors.data.mapper.system.FieldColumnMapper;
import pro.shushi.pamirs.framework.connectors.data.mapper.system.ModelTableMapper;
import pro.shushi.pamirs.framework.connectors.data.mapper.system.ModuleIndexMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;
import pro.shushi.pamirs.meta.enmu.DataSourceProtocolEnum;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 逻辑基础元数据服务实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/13 2:45 上午
 */
@Slf4j
@Component
public class SchemaMetaServiceImpl implements SchemaMetaService {

    @Resource
    private PamirsFrameworkSystemConfiguration pamirsFrameworkSystemConfiguration;

    @Resource
    private ModelTableMapper modelTableMapper;

    @Resource
    private FieldColumnMapper fieldColumnMapper;

    @Resource
    private ModuleIndexMapper moduleIndexMapper;

    @Resource
    private DsService dsService;

    @Override
    public Map<String, LogicTable> fetchLogicTableMap(Map<String/*schema*/, String/*ds*/> databaseMap,
                                                      Map<String, ModelTable> modelTableMap,
                                                      boolean supportDrop) {
        Map<String, MetaDataDialectService> serviceMap = new HashMap<>(databaseMap.size());
        Map<String/*schema#table*/, LogicTable> logicTableMap = new HashMap<>();
        for (Map.Entry<String, String> entry : databaseMap.entrySet()) {
            String ds = entry.getValue();
            MetaDataDialectService metaDataDialectService = serviceMap.get(ds);
            if (null == metaDataDialectService) {
                metaDataDialectService = Objects.requireNonNull(Dialects.component(MetaDataDialectService.class, ds));
                serviceMap.put(ds, metaDataDialectService);
            }
            Map<String/*schema#table*/, LogicTable> logicTableMapPerDs;
            try (DsHintApi ignored = DsHintApi.use(ds)) {
                Map<String, String> currentDatabaseMap = new HashMap<>();
                currentDatabaseMap.put(entry.getKey(), entry.getValue());
                logicTableMapPerDs = metaDataDialectService.fetchLogicTableMap(modelTableMap, currentDatabaseMap, supportDrop);
            }
            if (MapUtils.isNotEmpty(logicTableMapPerDs)) {
                logicTableMap.putAll(logicTableMapPerDs);
            }
        }
        return logicTableMap;
    }

    @Override
    public Map<String/*schema#table*/, ModelTable> fetchModelTableMap(
            Map<String/*schema*/, String/*ds*/> databaseMap, boolean supportDrop) {
        final String systemDsKey = pamirsFrameworkSystemConfiguration.getSystemDsKey();
        String dsKey = dsService.fetchLeafDataSources(systemDsKey).iterator().next();
        String modelTableTableName = DataPrefixManager.tablePrefix(ModuleConstants.MODULE_SYSTEM, ModelTable.MODEL_MODEL, ModelTable.TABLE_NAME);
        if (!Dialects.component(TableMetaDialectService.class, dsKey).existTable(dsKey, modelTableTableName)) {
            return null;
        }
        List<String> schemas = ListUtils.toList(databaseMap.keySet());
        List<ModelTable> modelTableList = modelTableMapper.selectList(Pops.<ModelTable>lambdaQuery().in(ModelTable::getTableSchema, schemas));
        List<FieldColumn> fieldColumnList = fieldColumnMapper.selectList(Pops.<FieldColumn>lambdaQuery().in(FieldColumn::getTableSchema, schemas));
        List<ModuleIndex> moduleIndexList = moduleIndexMapper.selectList(Pops.<ModuleIndex>lambdaQuery().in(ModuleIndex::getTableSchema, schemas));
        return fetchModelTableMap0(supportDrop, modelTableList, fieldColumnList, moduleIndexList);
    }

    @Override
    public Map<DataSourceProtocolEnum, Map<String/*schema#table*/, LogicTable>> fetchLogicTableMap(
            String dsKey, Map<String/*schema#table*/, ModelTable> modelTableMap, boolean supportDrop) {
        Map<DataSourceProtocolEnum, Map<String/*schema#table*/, LogicTable>> protocolLogicTableMap = new HashMap<>();
        Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> protocolSchemaMap = dsService.fetchDatabaseMap(dsKey);
        for (DataSourceProtocolEnum protocolEnum : protocolSchemaMap.keySet()) {
            Map<String/*schema*/, String/*ds*/> schemaMap = protocolSchemaMap.get(protocolEnum);
            if (MapUtils.isNotEmpty(schemaMap)) {
                String leafDsKey = schemaMap.values().iterator().next();
                MetaDataDialectService metaDataDialectService = Objects.requireNonNull(Dialects.component(MetaDataDialectService.class, leafDsKey));
                try (DsHintApi ignored = DsHintApi.use(dsKey)) {
                    protocolLogicTableMap.putIfAbsent(protocolEnum, metaDataDialectService.fetchLogicTableMap(modelTableMap, schemaMap, supportDrop));
                }
            }
        }
        return protocolLogicTableMap;
    }

    @Override
    public Map<DataSourceProtocolEnum, Map<String/*schema#table*/, ModelTable>> fetchModelTableMap(
            String dsKey, boolean supportDrop) {
        Map<DataSourceProtocolEnum, Map<String/*schema#table*/, ModelTable>> protocolModelTableMap = new HashMap<>();
        Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> protocolSchemaMap = dsService.fetchDatabaseMap(dsKey);
        for (DataSourceProtocolEnum protocolEnum : protocolSchemaMap.keySet()) {
            Map<String/*schema*/, String/*ds*/> schemaMap = protocolSchemaMap.get(protocolEnum);
            if (MapUtils.isNotEmpty(schemaMap)) {
                List<String> schemas = ListUtils.toList(schemaMap.keySet());
                List<ModelTable> modelTableList = modelTableMapper.selectList(Pops.<ModelTable>lambdaQuery().in(ModelTable::getTableSchema, schemas));
                List<FieldColumn> fieldColumnList = fieldColumnMapper.selectList(Pops.<FieldColumn>lambdaQuery().in(FieldColumn::getTableSchema, schemas));
                List<ModuleIndex> moduleIndexList = moduleIndexMapper.selectList(Pops.<ModuleIndex>lambdaQuery().in(ModuleIndex::getTableSchema, schemas));
                protocolModelTableMap.putIfAbsent(protocolEnum, fetchModelTableMap0(supportDrop, modelTableList, fieldColumnList, moduleIndexList));
            }
        }
        return protocolModelTableMap;
    }

    private Map<String/*schema#table*/, ModelTable> fetchModelTableMap0(
            boolean supportDrop, List<ModelTable> modelTableList, List<FieldColumn> fieldColumnList, List<ModuleIndex> moduleIndexList) {
        Map<String/*schema#table*/, ModelTable> modelTableMap = new HashMap<>();
        for (ModelTable modelTable : modelTableList) {
            String key = SchemaUtils.getSchemaTableKey(modelTable.getTableSchema(), modelTable.getTableName());
            modelTableMap.put(key, modelTable.setSupportDrop(supportDrop));
        }
        for (FieldColumn fieldColumn : fieldColumnList) {
            String key = SchemaUtils.getSchemaTableKey(fieldColumn.getTableSchema(), fieldColumn.getTableName());
            ModelTable modelTable = modelTableMap.get(key);
            if (null == modelTable) {
                continue;
            }
            if (null == modelTable.getFieldColumnMap()) {
                modelTable.setFieldColumnMap(new HashMap<>());
            }
            modelTable.getFieldColumnMap().putIfAbsent(fieldColumn.getField(), fieldColumn);
        }
        for (ModuleIndex moduleIndex : moduleIndexList) {
            String key = SchemaUtils.getSchemaTableKey(moduleIndex.getTableSchema(), moduleIndex.getTableName());
            ModelTable modelTable = modelTableMap.get(key);
            if (null == modelTable) {
                continue;
            }
            if (null == modelTable.getModuleIndexMap()) {
                modelTable.setModuleIndexMap(new HashMap<>());
            }
            modelTable.getModuleIndexMap().putIfAbsent(moduleIndex.getIndexName(), moduleIndex);
        }
        return modelTableMap;
    }

}

