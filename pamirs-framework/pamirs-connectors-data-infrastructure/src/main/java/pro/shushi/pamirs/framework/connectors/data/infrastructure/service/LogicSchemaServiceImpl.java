package pro.shushi.pamirs.framework.connectors.data.infrastructure.service;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.DdlResult;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.ModelTableContext;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.TableComputer;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.TableDdl;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModuleIndex;
import pro.shushi.pamirs.framework.connectors.data.api.service.DsService;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.TableComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.constants.SystemTableConstants;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.SchemaUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.LogicSchemaService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.SchemaMetaService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.ScriptDialectService;
import pro.shushi.pamirs.framework.connectors.data.mapper.system.FieldColumnMapper;
import pro.shushi.pamirs.framework.connectors.data.mapper.system.ModelTableMapper;
import pro.shushi.pamirs.framework.connectors.data.mapper.system.ModuleIndexMapper;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsPersistenceConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsPersistenceItemConfiguration;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.DataSourceProtocolEnum;

import jakarta.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.connectors.data.infrastructure.enmu.InfExpEnumerate.BASE_MODEL_CONFIG_IS_NOT_EXIST_DEPENDENCY_ERROR;
import static pro.shushi.pamirs.framework.connectors.data.infrastructure.enmu.InfExpEnumerate.BASE_UPDATE_SCHEMA_STRUCTURE_ERROR;

/**
 * 逻辑基础设施服务实现
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/1/13 2:45 上午
 */
@Slf4j
@Order
@Component
@SPI.Service
public class LogicSchemaServiceImpl implements LogicSchemaService {

    private final Function<String, Boolean> autoCreateTableFunction = autoCreateTable();

    @Resource
    private PamirsFrameworkSystemConfiguration pamirsFrameworkSystemConfiguration;

    @Resource
    private PamirsMapperConfigurationProxy pamirsMapperConfigurationProxy;

    @Resource
    private PamirsPersistenceConfigurationProxy pamirsPersistenceConfigurationProxy;

    @Resource
    private InheritedProcessor inheritedProcessor;

    @Resource
    private ModelTableMapper modelTableMapper;

    @Resource
    private FieldColumnMapper fieldColumnMapper;

    @Resource
    private ModuleIndexMapper moduleIndexMapper;

    @Resource
    protected DsService dsService;

    @Resource
    protected SchemaMetaService schemaMetaService;

    @Autowired
    protected TableComponent tableComponent;

    @Override
    public void initSystemSchema(boolean diffTable) {
        String[] systemModels = pamirsFrameworkSystemConfiguration.getSystemModels();
        String[] initTables;
        if (!diffTable) {
            initTables = systemModels;
        } else {
            if (ArrayUtils.isNotEmpty(systemModels)) {
                initTables = ArrayUtils.addAll(systemModels, SystemTableConstants.tableModels);
            } else {
                initTables = SystemTableConstants.tableModels;
            }
        }
        initSystemSchema(initTables, diffTable);
    }

    @Override
    public void initSystemSchema(String[] models, boolean diffTable) {
        if (null == models) {
            return;
        }
        final String systemDsKey = pamirsFrameworkSystemConfiguration.getSystemDsKey();
        if (autoCreateTableFunction.apply(systemDsKey)) {
            Set<String> modelSet = Sets.newHashSet(SystemTableConstants.tableModels);
            String dsKey = pamirsFrameworkSystemConfiguration.getOriginSystemDsKey();
            List<ModelDefinition> modelDefinitions = Arrays.stream(models).filter(v -> diffTable || !modelSet.contains(v))
                    .map(v -> {
                        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(v);
                        if (null == modelConfig) {
                            throw PamirsException.construct(BASE_MODEL_CONFIG_IS_NOT_EXIST_DEPENDENCY_ERROR)
                                    .appendMsg(MessageFormat.format("model:{0}", v)).errThrow();
                        }
                        return modelConfig;
                    })
                    .map(ModelConfig::getModelDefinition)
                    .filter(Objects::nonNull)
                    .map(v -> v.setModule(ModuleConstants.MODULE_SYSTEM))
                    .map(v -> v.setDsKey(dsKey))
                    .collect(Collectors.toList());
            if (modelDefinitions.isEmpty()) {
                return;
            }
            buildTable(modelDefinitions, true, diffTable);
        }
    }

    @Override
    public Boolean dropTable(ModelDefinition modelDefinition) {
        String model = modelDefinition.getModel();
        //获取模型的dsKey
        String dsKey = PamirsSession.getContext().getModule(modelDefinition.getModule()).getDsKey();
        if (StringUtils.isBlank(dsKey)) {
            ModuleDefinition moduleDef = new ModuleDefinition().setModule(modelDefinition.getModule()).queryOne();
            dsKey = Optional.ofNullable(moduleDef).map(ModuleDefinition::getDsKey).orElse(DsApi.get().defaultDsKey());
        }
        Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> dataSourceMap = dsService.fetchDatabaseMap(dsKey);
        for (DataSourceProtocolEnum protocolEnum : dataSourceMap.keySet()) {
            Map<String/*schema*/, String/*ds*/> schemaMap = dataSourceMap.get(protocolEnum);
            if (!schemaMap.isEmpty()) {
                Map<String/*schema#table*/, ModelTable> modelTableMap = schemaMetaService.fetchModelTableMap(schemaMap, true);
                Map<String/*schema#table*/, LogicTable> logicTableMap = schemaMetaService.fetchLogicTableMap(schemaMap, modelTableMap, true);
                if (null == logicTableMap) {
                    logicTableMap = new HashMap<>();
                }
                LogicTable logicTable = null;
                LogicTable curLogicTable = null;
                for (String key : logicTableMap.keySet()) {
                    logicTable = logicTableMap.get(key);
                    if (model.equals(logicTable.getModel())) {
                        curLogicTable = logicTable;
                        break;
                    }
                }
                if (curLogicTable == null) {
                    log.warn("当前删除的模型不存在物理表: [{}]", model);
                    return Boolean.TRUE;
                }
                log.info("logicTable: [{}]", curLogicTable);
                ModelTable modelTable = curLogicTable.getModelTable();
                if (null != modelTable.getSupportDrop() && modelTable.getSupportDrop()
                        && (null == modelTable.getUsing() || !modelTable.getUsing())) {
                    List<String> dropDdl = tableComponent.dropTable(curLogicTable);
                    for (String ddl : dropDdl) {
                        ScriptDialectService scriptDialectService = Dialects.component(ScriptDialectService.class, dsKey);
                        scriptDialectService.run(dsKey, ddl);
                    }

                }
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Map<String/*module*/, Map<String/*dsKey*/, List<String/*ddl*/>>> buildTable(List<ModelDefinition> modelDefinitions,
                                                                                       Boolean supportDrop,
                                                                                       Boolean diffTable) {
        return buildTable(modelDefinitions, supportDrop, diffTable, autoCreateTableFunction);
    }

    @Override
    public Map<String/*module*/, Map<String/*dsKey*/, List<String/*ddl*/>>> buildTable(List<ModelDefinition> modelDefinitions,
                                                                                       Boolean supportDrop,
                                                                                       Boolean diffTable,
                                                                                       Function<String, Boolean> autoCreate) {
        Map<String/*module*/, List<ModelDefinition>> modelDefinitionsPerModuleMap = new HashMap<>();
        for (ModelDefinition modelDefinition : modelDefinitions) {
            String module = modelDefinition.getModule();
            pro.shushi.pamirs.meta.common.util.MapUtils
                    .computeIfAbsent(modelDefinitionsPerModuleMap, module, k -> new ArrayList<>()).add(modelDefinition);
        }
        TableComputer tableComputer = Spider.getDefaultExtension(TableComputer.class);
        Map<String/*module*/, Map<String/*dsKey*/, List<String/*ddl*/>>> result = new LinkedHashMap<>();
        for (String module : modelDefinitionsPerModuleMap.keySet()) {
            List<ModelDefinition> modelDefinitionsPerModule = modelDefinitionsPerModuleMap.get(module);
            modelDefinitionsPerModule = inheritedProcessor.sortModelByInherited(module, modelDefinitionsPerModule);
            Set<String/*dsKey*/> dsKeys = dsService.fetchDataSourceList(modelDefinitionsPerModule);
            Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> dataSourceMap = dsService.fetchDatabaseMap(dsKeys);
            Map<String/*dsKey*/, List<String/*ddl*/>> dsDDLListMap = new HashMap<>();
            Map<String/*schema#table*/, LogicTable> totalLogicTableMap = new HashMap<>();
            for (DataSourceProtocolEnum protocolEnum : dataSourceMap.keySet()) {
                Map<String/*schema*/, String/*ds*/> schemaMap = dataSourceMap.get(protocolEnum);
                if (!schemaMap.isEmpty()) {
                    Map<String/*schema#table*/, ModelTable> modelTableMap = null;
                    if (diffTable) {
                        modelTableMap = schemaMetaService.fetchModelTableMap(schemaMap, supportDrop);
                    }
                    Map<String/*schema#table*/, LogicTable> logicTableMap = schemaMetaService
                            .fetchLogicTableMap(schemaMap, modelTableMap, supportDrop);

                    if (null == logicTableMap) {
                        logicTableMap = new HashMap<>();
                    }

                    Set<String> models = new HashSet<>();
                    ModelTableContext modelTableContext = SchemaUtils.fetchModelTableContext(logicTableMap, modelDefinitionsPerModule)
                            .setSupportDrop(supportDrop);
                    for (ModelDefinition modelDefinition : modelDefinitionsPerModule) {
                        DdlResult ddlResult = tableComputer.compute(modelTableContext, modelDefinition);
                        models.add(modelDefinition.getModel());
                        // 合并相同数据源ddl
                        mergeDdlPerDs(dsDDLListMap, ddlResult);
                    }
                    for (String key : logicTableMap.keySet()) {
                        LogicTable logicTable = logicTableMap.get(key);
                        if (models.contains(logicTable.getModel())) {
                            totalLogicTableMap.put(key, logicTable);
                        }
                    }
                }
            }
            // 执行ddl
            updateSchemaStructure(dsDDLListMap, true, false, autoCreate);
            // 更新表结构元数据
            if (diffTable) {
                updateMeta(module, ListUtils.toList(totalLogicTableMap.values()), autoCreate);
            }
            result.put(module, dsDDLListMap);
        }
        return result;
    }

    @Override
    public void buildTable(List<Meta> metaList) {
        buildTable(metaList, null);
    }

    @Override
    public void buildTable(List<Meta> metaList, Set<String> bootModules) {
        buildTable(metaList, bootModules, true, true, true, false, k -> true);
    }

    @Override
    public void buildTable(List<Meta> metaList, Set<String> bootModules,
                           boolean rebuildTable, boolean diffTable, boolean updateMeta, boolean printDDL) {
        buildTable(metaList, bootModules, rebuildTable, diffTable, updateMeta, printDDL, autoCreateTableFunction);
    }

    @Override
    public void buildTable(List<Meta> metaList, Set<String> bootModules,
                           boolean rebuildTable, boolean diffTable, boolean updateMeta, boolean printDDL, Function<String, Boolean> autoCreate) {
        if (null == metaList) {
            return;
        }

        TableComputer tableComputer = Spider.getDefaultExtension(TableComputer.class);

        // 计算表结构变更
        Map<String, Map<String, ModelTable>> modelTableCache = new HashMap<>();
        Map<String, Map<String, LogicTable>> logicTableCache = new HashMap<>();

        for (Meta meta : metaList) {
            String module = meta.getModule();
            if (null != bootModules && !bootModules.contains(module)) {
                continue;
            }
            if (null == meta.getCurrentModuleData().getModelList()) {
                continue;
            }
            // 表结构计算
            Map<String/*dsKey*/, List<String/*ddl*/>> dsDDLListMap = new HashMap<>();
            Map<String/*schema#table*/, LogicTable> allLogicTableMap = new HashMap<>();

            Set<String/*dsKey*/> dsKeys = dsService.fetchDataSourceList(meta, bootModules);
            Map<DataSourceProtocolEnum, Map<String/*schema*/, String/*ds*/>> dataSourceMap = dsService.fetchDatabaseMap(dsKeys);
            for (DataSourceProtocolEnum protocolEnum : dataSourceMap.keySet()) {
                Map<String/*schema*/, String/*ds*/> schemaMap = dataSourceMap.get(protocolEnum);
                if (schemaMap.isEmpty()) {
                    continue;
                }
                if (log.isDebugEnabled()) {
                    for (Map.Entry<String, String> entry : schemaMap.entrySet()) {
                        String schema = entry.getKey();
                        String ds = entry.getValue();
                        Map<String, ModelTable> modelTableMap = modelTableCache.computeIfAbsent(schema + CharacterConstants.SEPARATOR_UNDERLINE + ds, k -> {
                            Map<String, String> t = new HashMap<>();
                            t.put(schema, ds);
                            long start = System.currentTimeMillis();
                            Map<String, ModelTable> values = schemaMetaService.fetchModelTableMap(t, diffTable);
                            log.debug("{} fetchModelTableMap cost time: {}ms", module, System.currentTimeMillis() - start);
                            return values;
                        });
                        Map<String, LogicTable> logicTableMap = logicTableCache.computeIfAbsent(schema + CharacterConstants.SEPARATOR_UNDERLINE + ds, k -> {
                            Map<String, String> t = new HashMap<>();
                            t.put(schema, ds);
                            long start = System.currentTimeMillis();
                            Map<String, LogicTable> values = schemaMetaService.fetchLogicTableMap(t, modelTableMap, diffTable);
                            log.debug("{} fetchLogicTableMap cost time: {}ms", module, System.currentTimeMillis() - start);
                            return values;
                        });
                        if (null == logicTableMap) {
                            logicTableMap = new HashMap<>();
                        }
                        allLogicTableMap.putAll(logicTableMap);
                    }
                } else {
                    for (Map.Entry<String, String> entry : schemaMap.entrySet()) {
                        String schema = entry.getKey();
                        String ds = entry.getValue();
                        Map<String, ModelTable> modelTableMap = modelTableCache.computeIfAbsent(schema + CharacterConstants.SEPARATOR_UNDERLINE + ds, k -> {
                            Map<String, String> t = new HashMap<>();
                            t.put(schema, ds);
                            return schemaMetaService.fetchModelTableMap(t, diffTable);
                        });
                        Map<String, LogicTable> logicTableMap = logicTableCache.computeIfAbsent(schema + CharacterConstants.SEPARATOR_UNDERLINE + ds, k -> {
                            Map<String, String> t = new HashMap<>();
                            t.put(schema, ds);
                            return schemaMetaService.fetchLogicTableMap(t, modelTableMap, diffTable);
                        });
                        if (null == logicTableMap) {
                            logicTableMap = new HashMap<>();
                        }
                        allLogicTableMap.putAll(logicTableMap);
                    }
                }
            }

            DdlResult ddlResult = tableComputer.compute(meta, bootModules, allLogicTableMap, diffTable);
            // 合并相同数据源ddl
            mergeDdlPerDs(dsDDLListMap, ddlResult);
            // 执行ddl 与 更新表结构元数据
            if (updateSchemaStructure(dsDDLListMap, rebuildTable, printDDL, autoCreate)) {
                modelTableCache.clear();
                logicTableCache.clear();
            }
            if (diffTable && updateMeta) {
                updateMeta(meta.getModule(), allLogicTableMap.values(), autoCreate);
            }
        }
    }

    private Function<String, Boolean> autoCreateTable() {
        return (dsKey) -> {
            PamirsPersistenceItemConfiguration pamirsPersistenceItemConfiguration
                    = pamirsPersistenceConfigurationProxy.fetchPamirsPersistenceConfiguration(dsKey);
            return pamirsPersistenceItemConfiguration.getAutoCreateTable();
        };
    }

    protected void updateMeta(String module, Collection<LogicTable> logicTableList, Function<String, Boolean> autoCreate) {
        // 更新表结构元数据
        if (null == logicTableList) {
            return;
        }
        List<ModelTable> updateModelTableList = new ArrayList<>();
        List<FieldColumn> updateFieldColumnList = new ArrayList<>();
        List<ModuleIndex> updateModuleIndexList = new ArrayList<>();
        List<ModelTable> deleteModelTableList = new ArrayList<>();
        List<FieldColumn> deleteFieldColumnList = new ArrayList<>();
        List<ModuleIndex> deleteModuleIndexList = new ArrayList<>();
        for (LogicTable logicTable : logicTableList) {
            if (null == logicTable.getModelTable()) {
                continue;
            }
            ModelTable modelTable = logicTable.getModelTable();
            if (!autoCreate.apply(modelTable.getDsKey())) {
                continue;
            }
            if (!modelTable.isEmptyObj() && (null == modelTable.getUsing() || !modelTable.getUsing())) {
                if (!module.equals(logicTable.getModule())) {
                    continue;
                }
                if (null != modelTable.getSupportDrop() && modelTable.getSupportDrop()) {
                    deleteModelTableList.add(modelTable);
                    if (MapUtils.isNotEmpty(modelTable.getFieldColumnMap())) {
                        deleteFieldColumnList.addAll(modelTable.getFieldColumnMap().values());
                    }
                    if (MapUtils.isNotEmpty(modelTable.getModuleIndexMap())) {
                        deleteModuleIndexList.addAll(modelTable.getModuleIndexMap().values());
                    }
                }
            } else {
                if (null != modelTable.getChanged() && modelTable.getChanged()) {
                    updateModelTableList.add(modelTable);
                }
                if (MapUtils.isNotEmpty(modelTable.getFieldColumnMap())) {
                    for (FieldColumn fieldColumn : modelTable.getFieldColumnMap().values()) {
                        if (null != fieldColumn.getChanged() && fieldColumn.getChanged()) {
                            updateFieldColumnList.add(fieldColumn);
                        }
                    }
                }
                if (MapUtils.isNotEmpty(modelTable.getModuleIndexMap())) {
                    for (ModuleIndex moduleIndex : modelTable.getModuleIndexMap().values()) {
                        if (null != moduleIndex.getChanged() && moduleIndex.getChanged()) {
                            updateModuleIndexList.add(moduleIndex);
                        }
                    }
                }
                boolean supportDrop = null != modelTable.getSupportDrop() && modelTable.getSupportDrop();
                if (MapUtils.isNotEmpty(modelTable.getFieldColumnMap())) {
                    for (FieldColumn fieldColumn : modelTable.getFieldColumnMap().values()) {
                        if (!fieldColumn.isEmpty() && (null == fieldColumn.getUsing() || !fieldColumn.getUsing())) {
                            if (supportDrop && module.equals(fieldColumn.getModule())) {
                                deleteFieldColumnList.add(fieldColumn);
                            }
                        }
                    }
                }
                if (MapUtils.isNotEmpty(modelTable.getModuleIndexMap())) {
                    for (ModuleIndex moduleIndex : modelTable.getModuleIndexMap().values()) {
                        if (!moduleIndex.isEmpty() && (null == moduleIndex.getUsing() || !moduleIndex.getUsing())) {
                            if (supportDrop && module.equals(moduleIndex.getModule())) {
                                deleteModuleIndexList.add(moduleIndex);
                            }
                        }
                    }
                }
            }
        }
        Tx.build().executeWithoutResult((status) -> {
            if (CollectionUtils.isNotEmpty(updateModelTableList)) {
                modelTableMapper.insertOrUpdateBatchWithSize(updateModelTableList, pamirsMapperConfigurationProxy.batchOperationForModel(ModelTable.MODEL_MODEL).getWrite());
            }
            if (CollectionUtils.isNotEmpty(updateFieldColumnList)) {
                fieldColumnMapper.insertOrUpdateBatchWithSize(updateFieldColumnList, pamirsMapperConfigurationProxy.batchOperationForModel(FieldColumn.MODEL_MODEL).getWrite());
            }
            if (CollectionUtils.isNotEmpty(updateModuleIndexList)) {
                moduleIndexMapper.insertOrUpdateBatchWithSize(updateModuleIndexList, pamirsMapperConfigurationProxy.batchOperationForModel(ModuleIndex.MODEL_MODEL).getWrite());
            }
            if (CollectionUtils.isNotEmpty(deleteModelTableList)) {
                modelTableMapper.deleteByPks(deleteModelTableList);
            }
            if (CollectionUtils.isNotEmpty(deleteFieldColumnList)) {
                fieldColumnMapper.deleteByPks(deleteFieldColumnList);
            }
            if (CollectionUtils.isNotEmpty(deleteModuleIndexList)) {
                moduleIndexMapper.deleteByPks(deleteModuleIndexList);
            }
        });
    }

    protected boolean updateSchemaStructure(Map<String, List<String>> dsDDLListMap,
                                            boolean rebuildTable,
                                            boolean printDDL,
                                            Function<String, Boolean> autoCreate) {
        boolean isUpdated = false;
        // 更新表结构
        for (String dsKey : dsDDLListMap.keySet()) {
            if (!autoCreate.apply(dsKey)) {
                continue;
            }
            ScriptDialectService scriptDialectService = Dialects.component(ScriptDialectService.class, dsKey);
            List<String> ddlList = dsDDLListMap.get(dsKey);
            if (CollectionUtils.isNotEmpty(ddlList)) {
                try {
                    String script = String.join(CharacterConstants.NEWLINE, ddlList);
                    if (rebuildTable) {
                        scriptDialectService.run(dsKey, script);
                        isUpdated = true;
                    }
                    if (printDDL) {
                        scriptDialectService.ddl(dsKey, script);
                        isUpdated = true;
                    }
                } catch (Exception e) {
                    throw PamirsException.construct(BASE_UPDATE_SCHEMA_STRUCTURE_ERROR, e).errThrow();
                }
            }
        }
        return isUpdated;
    }

    protected void mergeDdlPerDs(Map<String, List<String>> dsDDLListMap, DdlResult ddlResult) {
        List<TableDdl> tableDdlList = ddlResult.getCreateOrAlterList();
        mergeDdlPerDs(dsDDLListMap, tableDdlList);
        List<TableDdl> dropDdlList = ddlResult.getDropList();
        mergeDdlPerDs(dsDDLListMap, dropDdlList);
    }

    protected void mergeDdlPerDs(Map<String, List<String>> dsDDLListMap, List<TableDdl> tableDdlList) {
        for (TableDdl tableDdl : tableDdlList) {
            String dsKey = tableDdl.getDsKey();
            List<String> ddlList = pro.shushi.pamirs.meta.common.util.MapUtils
                    .computeIfAbsent(dsDDLListMap, dsKey, k -> new ArrayList<>());
            if (StringUtils.isNotBlank(tableDdl.getDdl())) {
                ddlList.add(tableDdl.getDdl());
            }
        }
    }

}
