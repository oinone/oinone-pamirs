package pro.shushi.pamirs.framework.connectors.data.ddl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DsKeyFetcher;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.*;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.TableComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.constants.DdlConstants;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.TableDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.TableDiff;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.SchemaUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.util.ParserUtil;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 数据表配置计算抽象类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public abstract class AbstractTableComputer implements TableComputer {

    @Resource
    protected InheritedProcessor inheritedProcessor;

    @Resource
    protected ShardingTableComputer shardingTableComputer;

    @Resource
    protected TableComponent tableComponent;

    @Resource
    protected DsKeyFetcher dsKeyFetcher;

    @Override
    public DdlResult compute(Meta meta, Map<String/*schema#table*/, LogicTable> logicTableMap, boolean supportDrop) {
        return compute(meta, null, logicTableMap, supportDrop);
    }

    @Override
    public DdlResult compute(Meta meta, Set<String> includeModules, Map<String/*schema#table*/, LogicTable> logicTableMap, boolean supportDrop) {
        DdlResult result = new DdlResult();
        List<ModelDefinition> modelDefinitionList = meta.getCurrentModuleData().getModelList();
        if (CollectionUtils.isEmpty(modelDefinitionList)) {
            return result;
        }
        return compute(result, meta, includeModules, modelDefinitionList, logicTableMap, supportDrop);
    }

    protected DdlResult compute(DdlResult result,
                                Meta meta,
                                Set<String> includeModules,
                                List<ModelDefinition> modelDefinitionList,
                                Map<String/*schema#table*/, LogicTable> logicTableMap,
                                boolean supportDrop) {
        modelDefinitionList = inheritedProcessor.sortModelByInherited(meta.getModule(), modelDefinitionList);
        // 生成创建或更新表DDL
        String module = meta.getModule();
        ModelTableContext modelTableContext = SchemaUtils.fetchModelTableContext(logicTableMap, modelDefinitionList)
                .setSupportDrop(supportDrop);
        for (ModelDefinition modelDefinition : modelDefinitionList) {
            if (null != includeModules && !includeModules.contains(modelDefinition.getDsModule())) {
                continue;
            }
            compute(result, module, modelTableContext, modelDefinition);
        }
        // 处理需要删除的表
        for (LogicTable logicTable : logicTableMap.values()) {
            if (null == logicTable.getModelTable()) {
                continue;
            }
            if (!meta.getModule().equals(logicTable.getModule())) {
                continue;
            }
            ModelTable modelTable = logicTable.getModelTable();
            if (null != modelTable.getSupportDrop() && modelTable.getSupportDrop()
                    && (null == modelTable.getUsing() || !modelTable.getUsing())) {
                List<String> dropDdl = tableComponent.dropTable(logicTable);
                result.addDropDdl(logicTable.getDsKey(), logicTable.getModule(), logicTable.getModel(),
                        logicTable.getTableName(), StringUtils.join(dropDdl, CharacterConstants.SEPARATOR_EMPTY));
            }
        }
        return result;
    }

    @Override
    public DdlResult compute(ModelTableContext modelTableContext, ModelDefinition modelDefinition) {
        DdlResult result = new DdlResult();
        return compute(result, modelDefinition.getModule(), modelTableContext, modelDefinition);
    }

    protected DdlResult compute(DdlResult result, String module, ModelTableContext modelTableContext, ModelDefinition modelDefinition) {
        String model = modelDefinition.getModel();
        String logicDsKey = dsKeyFetcher.fetchLogicDsKeyForDDL(modelDefinition.getCompletedDsKey());
        final String logicTableName = DataPrefixManager.tablePrefix(module, model, modelDefinition.getTable());
        final DsDialectComponent dsDialectComponent = Dialects.component(DsDialectComponent.class, logicDsKey);
        // 计算分库分表
        shardingTableComputer.computeShardingTable(module, model, (dataNode, dsSeparator, tableNode, tableSeparator) -> {
            // 分库分表
            dataNode = shardingTableComputer.completedDataNode(logicDsKey, dsSeparator, dataNode);
            String tableSchema = dsDialectComponent.getDatabase(dataNode);
            String tableName = shardingTableComputer.completedDataNode(logicTableName, tableSeparator, tableNode);
            LogicTable logicTable = fetchLogicTable(modelTableContext, model, tableSchema, tableName, PStringUtils.valueOfObj(tableNode));
            diff(new TableDiff().setResult(result).setLogicTable(logicTable)
                    .setModelDefinition(modelDefinition).setModel(model).setLeafDsKey(dataNode)
                    .setTableNode(tableNode).setTableSchema(tableSchema).setTableName(tableName));
        }, (dataNode, dsSeparator, tableNode, tableSeparator) -> {
            // 分库单表
            dataNode = shardingTableComputer.completedDataNode(logicDsKey, dsSeparator, dataNode);
            String tableSchema = dsDialectComponent.getDatabase(dataNode);
            LogicTable logicTable = fetchLogicTable(modelTableContext, model, tableSchema, logicTableName, null);
            diff(new TableDiff().setResult(result).setLogicTable(logicTable)
                    .setModelDefinition(modelDefinition).setModel(model).setLeafDsKey(dataNode)
                    .setTableNode(tableNode).setTableSchema(tableSchema).setTableName(logicTableName));
        }, (dataNode, dsSeparator, tableNode, tableSeparator) -> {
            // 单库单表
            String tableSchema = dsDialectComponent.getDatabase(logicDsKey);
            LogicTable logicTable = fetchLogicTable(modelTableContext, model, tableSchema, logicTableName, null);
            diff(new TableDiff().setResult(result).setLogicTable(logicTable)
                    .setModelDefinition(modelDefinition).setModel(model).setLeafDsKey(logicDsKey)
                    .setTableNode(tableNode).setTableSchema(tableSchema).setTableName(logicTableName));
        });
        return result;
    }

    private LogicTable fetchLogicTable(ModelTableContext modelTableContext,
                                       String model, String tableSchema, String tableName,
                                       String tableSharding) {
        if (null == modelTableContext.getLogicTableMap()) {
            return null;
        }
        String schemaTableKey = SchemaUtils.getSchemaTableKey(tableSchema, tableName);
        LogicTable logicTable = modelTableContext.getLogicTableMap().get(schemaTableKey);
        String modelKey = SchemaUtils.getShardingModel(model, tableSharding);
        if ((null == logicTable || null == logicTable.getModel()) && modelTableContext.getModelMap().containsKey(modelKey)) {
            String twinSchemaTableKey = modelTableContext.getModelMap().get(modelKey);
            logicTable = modelTableContext.getLogicTableMap().get(twinSchemaTableKey);
        }
        if ((null == logicTable || null == logicTable.getModel())
                && modelTableContext.getMappingLogicTableMap().containsKey(schemaTableKey)) {
            logicTable = modelTableContext.getMappingLogicTableMap().get(schemaTableKey);
        }
        if ((null == logicTable || null == logicTable.getModel())
                && modelTableContext.getExistTableModelMap().containsKey(model)) {
            String existTableModel = modelTableContext.getExistTableModelMap().get(model);
            modelKey = SchemaUtils.getShardingModel(existTableModel, tableSharding);
            String key = modelTableContext.getModelMap().get(modelKey);
            logicTable = modelTableContext.getLogicTableMap().get(key);
        }
        if (null == logicTable) {
            logicTable = new LogicTable();
            modelTableContext.getLogicTableMap().putIfAbsent(schemaTableKey, logicTable);
        }
        modelTableContext.getMappingLogicTableMap().putIfAbsent(schemaTableKey, logicTable);
        return logicTable.setSupportDrop(modelTableContext.isSupportDrop());
    }

    private void diff(TableDiff tableDiff) {
        // 计算DDL
        TableResult tableResult = compute(new SchemaTableKey().setDsKey(tableDiff.getLeafDsKey())
                        .setTableSchema(tableDiff.getTableSchema()).setTableName(tableDiff.getTableName())
                        .setTableShardingNode(tableDiff.getTableNode()),
                tableDiff.getModelDefinition(), tableDiff.getLogicTable());
        if (null == tableResult) {
            return;
        }
        List<String> tableDdl = tableResult.getDdl();
        String ddlWithPlaceholder = null;
        if (!CollectionUtils.isEmpty(tableDdl)) {
            ddlWithPlaceholder = StringUtils.join(tableDdl, CharacterConstants.SEPARATOR_EMPTY);
        }
        Map<String, Object> context = new HashMap<>();
        context.put(DdlConstants.SHARDING_PLACEHOLDER_NAME, tableDiff.getTableNode());
        tableDiff.getLogicTable().useModelTable()
                .setLogicTableName(tableDiff.getModelDefinition().getTable())
                .setSharding(PStringUtils.valueOfObj(tableDiff.getTableNode()));
        String completedDdl = ParserUtil.replaceWithMap(ddlWithPlaceholder, context);
        tableDiff.getResult().addCreateOrAlterDdl(
                new TableDdl()
                        .setDsKey(tableDiff.getLeafDsKey())
                        .setModule(tableDiff.getModelDefinition().getModule())
                        .setModel(tableDiff.getModelDefinition().getModel())
                        .setTable(tableDiff.getModelDefinition().getTable())
                        .setDdl(completedDdl)
        );
    }

    @Override
    public TableResult compute(SchemaTableKey schemaTableKey, ModelDefinition modelDefinition, LogicTable logicTable) {
        return compute(schemaTableKey, ModelWrapper.wrap(modelDefinition), logicTable);
    }

    @Override
    public TableResult compute(String module, SchemaTableKey schemaTableKey, ModelConfig modelDefinition, LogicTable logicTable) {
        return compute(schemaTableKey, ModelWrapper.wrap(module, modelDefinition), logicTable);
    }

    @Override
    public String lockTableCommandPrefix(String dsKey) {
        return Objects.requireNonNull(Dialects.component(TableDialectComponent.class, dsKey)).lockTableCommandPrefix();
    }

    @Override
    public String unlockTableCommandPrefix(String dsKey) {
        return Objects.requireNonNull(Dialects.component(TableDialectComponent.class, dsKey)).unlockTableCommandPrefix();
    }

}
