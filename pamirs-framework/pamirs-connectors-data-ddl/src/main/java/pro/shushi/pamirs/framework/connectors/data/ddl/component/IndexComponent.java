package pro.shushi.pamirs.framework.connectors.data.ddl.component;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicIndex;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.constants.DdlConstants;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.IndexDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.enmu.DdlExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.framework.connectors.data.ddl.processor.FieldProcessor;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.ListUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 索引处理组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class IndexComponent {

    @Resource
    private TableComponent tableComponent;

    @Resource
    private FieldProcessor fieldProcessor;

    public void prepareIndexes(Set<String> logicColumns,
                               Set<String> indexColumnSet, Set<String> uniqueColumnSet, FieldWrapper modelField) {
        String indexColumns;
        if (CollectionUtils.isNotEmpty(logicColumns)) {
            indexColumns = modelField.getColumn() + CharacterConstants.SEPARATOR_COMMA
                    + StringUtils.join(logicColumns, CharacterConstants.SEPARATOR_COMMA);
        } else {
            indexColumns = modelField.getColumn();
        }
        if (modelField.getUnique()) {
            indexColumnSet.remove(modelField.getColumn());
            uniqueColumnSet.add(indexColumns);
        } else if (modelField.getIndex() && !uniqueColumnSet.contains(indexColumns)) {
            indexColumnSet.add(modelField.getColumn());
        }
    }

    public String primaryIndexName(String dsKey, String completedTableName) {
        return Dialects.component(IndexDialectComponent.class, dsKey).primaryIndexName(completedTableName);
    }

    public String generatePrimaryColumn(String dsKey, ModelWrapper modelDefinition, boolean addQuote, LogicIndex logicIndex) {
        return generatePrimaryColumn(dsKey, generatePrimaryColumnList(modelDefinition), addQuote, logicIndex);
    }

    public String generatePrimaryColumn(String dsKey, List<String> pkColumns, boolean addQuote, LogicIndex logicIndex) {
        return Dialects.component(IndexDialectComponent.class, dsKey).generatePrimaryColumn(pkColumns, addQuote, logicIndex);
    }

    public List<String> generatePrimaryColumnList(ModelWrapper modelDefinition) {
        List<String> pks = modelDefinition.getPk();
        List<String> pkColumns = new ArrayList<>();
        for (String pk : pks) {
            FieldWrapper pkField = fieldProcessor.fetchModelField(modelDefinition, pk);
            if (pkField == null) {
                throw PamirsException.construct(DdlExpEnumerate.PK_FIELD_NOT_FOUND_ERROR)
                        .appendMsg("model: ").appendMsg(modelDefinition.getModel())
                        .appendMsg(", pk: ").appendMsg(pk).errThrow();
            }
            pkColumns.add(pkField.getColumn());
        }
        if (pkColumns.size() > 1) {
            Set<String> logicColumns = fieldProcessor.fetchLogicColumns(modelDefinition.getModel());
            if (CollectionUtils.isNotEmpty(logicColumns)) {
                pkColumns.addAll(logicColumns);
            }
        }
        return pkColumns;
    }

    public void createPrimaryKey(DdlContext ddlContext, ModelWrapper modelDefinition, List<String> ddlList) {
        IndexDialectComponent indexDialectComponent = Dialects.component(IndexDialectComponent.class,
                ddlContext.useLogicTable().getDsKey());
        List<String> pks = modelDefinition.getPk();
        if (!CollectionUtils.isEmpty(pks)) {
            LogicTable logicTable = ddlContext.useLogicTable();
            List<String> pkList = generatePrimaryColumnList(modelDefinition);
            String completedTableName = tableComponent.tablePlaceholder(logicTable.getDsKey(), modelDefinition);
            ddlList.add(indexDialectComponent.createPrimaryKey(completedTableName, pkList));
            String indexName = primaryIndexName(logicTable.getDsKey(), completedTableName);
            LogicIndex logicIndex = new LogicIndex().setTableName(logicTable.getTableName())
                    .setIndexName(indexName).setUnique(true).setColumn(pkList);
            logicTable.getIndexMap().put(indexName, logicIndex);
            createIndexMeta(ddlContext, modelDefinition, indexName, logicIndex);
        } else {
            indexDialectComponent.fixCreatePrimaryKey(ddlList);
        }
    }

    public void addPrimaryKey(DdlContext ddlContext, ModelWrapper modelDefinition, List<String> ddlList) {
        LogicTable logicTable = ddlContext.useLogicTable();
        List<String> pkList = generatePrimaryColumnList(modelDefinition);
        String completedTableName = tableComponent.tablePlaceholder(logicTable.getDsKey(), modelDefinition);
        ddlList.add(Dialects.component(IndexDialectComponent.class, logicTable.getDsKey())
                .addPrimaryKey(completedTableName, pkList));
        String indexName = primaryIndexName(logicTable.getDsKey(), completedTableName);
        LogicIndex logicIndex = new LogicIndex().setTableName(logicTable.getTableName())
                .setIndexName(indexName).setUnique(true).setColumn(pkList);
        logicTable.getIndexMap().put(indexName, logicIndex);
        createIndexMeta(ddlContext, modelDefinition, indexName, logicIndex);
    }

    public String autoIncrement(String dsKey, ModelWrapper modelDefinition, FieldWrapper modelField, String columnDefinition) {
        String keyGenerator = fetchKeyGenerator(modelDefinition.getModel());
        return Dialects.component(IndexDialectComponent.class, dsKey).autoIncrement(modelDefinition, modelField, columnDefinition, keyGenerator);
    }

    @SuppressWarnings("unused")
    public String removeAutoIncrement(String dsKey, List<String> deleteColumns, String columnName, String columnDefinition) {
        return Dialects.component(IndexDialectComponent.class, dsKey).removeAutoIncrement(deleteColumns, columnName, columnDefinition);
    }

    public String fetchKeyGenerator(String model) {
        return PamirsTableInfo.fetchPamirsTableInfo(model).getKeyGenerator();
    }

    public void createIndex(DdlContext ddlContext, ModelWrapper modelDefinition, Set<String> indexSet, List<String> ddlList, boolean unique, String logicDeleteColumn) {
        LogicTable logicTable = ddlContext.useLogicTable();
        IndexDialectComponent indexDialectComponent = Dialects.component(IndexDialectComponent.class, logicTable.getDsKey());
        for (String index : indexSet) {
            String[] indexColumns = index.split(CharacterConstants.SEPARATOR_COMMA);
            String indexName = generateIndexName(logicTable, modelDefinition.getTable(), ListUtils.toList(indexColumns), logicDeleteColumn);
            ddlList.add(indexDialectComponent
                    .createIndex(unique, tableComponent.tablePlaceholder(logicTable.getDsKey(), modelDefinition), indexName, indexColumns));
            LogicIndex logicIndex = new LogicIndex().setTableName(logicTable.getTableName())
                    .setIndexName(indexName).setUnique(unique).setColumn(ListUtils.toList(indexColumns));
            logicTable.getIndexMap().put(indexName, logicIndex);
            createIndexMeta(ddlContext, modelDefinition, indexName, logicIndex);
        }
    }

    public String generateIndexName(LogicTable logicTable, String tableName, List<String> indexColumns, String logicDeleteColumn) {
        IndexDialectComponent indexDialectComponent = Dialects.component(IndexDialectComponent.class, logicTable.getDsKey());
        List<String> names = new ArrayList<>();
        for (String indexColumn : indexColumns) {
            if (!indexColumn.equals(logicDeleteColumn)) {
                names.add(indexColumn);
            } else {
                names.add(DdlConstants.NAME_IS_DELETED);
            }
        }
        return indexDialectComponent.indexName(tableName, logicTable.getTableName(), null, names);
    }

    public void createIndexMeta(DdlContext ddlContext, ModelWrapper modelDefinition, String indexName, LogicIndex logicIndex) {
        ddlContext.updateModuleIndexIfChanged(logicIndex, modelDefinition.getModule(), modelDefinition.getModel());
        ddlContext.changeIndex(indexName);
        ddlContext.unDropIndex(indexName);
    }

    public void dropIndex(DdlContext ddlContext, List<String> ddlList, LogicIndex index) {
        if (Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign()) {
            return;
        }
        LogicTable logicTable = ddlContext.useLogicTable();
        ddlList.add(Dialects.component(IndexDialectComponent.class, logicTable.getDsKey())
                .dropIndex(tableComponent.tablePlaceholder(logicTable), index));
        logicTable.getIndexMap().remove(index.getIndexName());
        ddlContext.dropIndex(index.getIndexName());
    }

}
