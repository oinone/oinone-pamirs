package pro.shushi.pamirs.framework.connectors.data.ddl.model;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicIndex;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModuleIndex;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.ColumnDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.common.util.MapUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * ddl上下文
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Data
public class DdlContext {

    // 当前叶子节点数据源
    private String dsKey;

    // 当前模块
    private String module;

    // 分表节点
    private Object tableShardingNode;

    // 已存在数据表
    private LogicTable existLogicTable;

    // 准备模型字段map
    Map<String/*field*/, FieldWrapper> modelFieldMap;
    Map<String/*columnName*/, FieldWrapper> columnFieldMap;

    private String logicDeleteColumn;
    private Set<String> indexColumnSet;
    private Set<String> uniqueColumnSet;

    // 包含auto_increment的主键字段定义
    private String pkColumnDefinition;

    // 逻辑字段
    private Set<String> logicColumns;// 包含逻辑删除字段

    // 废弃逻辑字段
    private Set<String> deprecatedColumns;

    public LogicTable useLogicTable() {
        if (null == existLogicTable) {
            this.existLogicTable = new LogicTable();
        }
        return existLogicTable;
    }

    public void refreshColumn(FieldWrapper modelField, String columnDefinition) {
        Column column = Dialects.component(ColumnDialectComponent.class, dsKey).fillColumn(new Column()
                        .setTableSchema(existLogicTable.getTableSchema())
                        .setTableName(existLogicTable.getTableName())
                        .setColumnName(modelField.getColumn())
                        .setColumnComment(modelField.getSummary())
                        .setOrdinalPosition(modelField.getPriority()), columnDefinition)
                .setCharacterSetName(modelField.getCharset())
                .setCollationName(modelField.getCollation());
        this.useLogicTable().getColumnMap().put(modelField.getField(), column);
    }

    public void refreshLogicColumn(String columnName, String columnDefinition, String comment) {
        Column column = Dialects.component(ColumnDialectComponent.class, dsKey).fillColumn(new Column()
                .setTableSchema(existLogicTable.getTableSchema())
                .setTableName(existLogicTable.getTableName())
                .setColumnName(columnName)
                .setColumnComment(comment)
                .setOrdinalPosition(null), columnDefinition);
        this.useLogicTable().getColumnMap().put(columnName, column);
    }

    public ModelTable updateModelTable(String module, String model) {
        this.useLogicTable().setModule(module);
        this.useLogicTable().setModel(model);
        return this.useLogicTable().useModelTable().setModule(module).setModel(model);
    }

    public void updateFieldColumn(FieldWrapper modelField, String module) {
        Column column = this.useLogicTable().getColumnMap().get(modelField.getField());
        if (null == column) {
            return;
        }
        ModelTable changeModelTable = this.useLogicTable().useModelTable();
        Long existId = Optional.ofNullable(this.useLogicTable().useModelTable().getFieldColumnMap().get(modelField.getField()))
                .map(FieldColumn::getId).orElse(null);
        FieldColumn fieldColumn = this.fetchColumn(modelField.getField());
        if (!column.getColumnName().equals(fieldColumn.getColumnName())
                || !modelField.getField().equals(fieldColumn.getField())
                || !module.equals(fieldColumn.getModule())
                || !modelField.getModel().equals(fieldColumn.getModel())
                || !column.getTableSchema().equals(fieldColumn.getTableSchema())
                || !column.getTableName().equals(fieldColumn.getTableName())
                || !getDsKey().equals(fieldColumn.getDsKey())
        ) {
            fieldColumn.setChanged(true);
        }
        changeModelTable.getFieldColumnMap().put(modelField.getField(), (FieldColumn) fieldColumn
                .setColumnName(column.getColumnName())
                .setField(modelField.getField())
                .setModule(module)
                .setModel(modelField.getModel())
                .setTableSchema(column.getTableSchema())
                .setTableName(column.getTableName())
                .setDsKey(getDsKey())
                .setId(existId));
    }

    public void updateModuleIndexIfChanged(LogicIndex index, String module, String model) {
        ModelTable changeModelTable = this.useLogicTable().useModelTable();
        Long existId = Optional.ofNullable(this.useLogicTable().useModelTable()
                .getModuleIndexMap().get(index.getIndexName())).map(ModuleIndex::getId).orElse(null);
        changeModelTable.getModuleIndexMap().put(index.getIndexName(), (ModuleIndex) this.fetchIndex(index.getIndexName())
                .setIndexName(index.getIndexName())
                .setModule(module)
                .setModel(model)
                .setTableSchema(this.useLogicTable().getTableSchema())
                .setTableName(this.useLogicTable().getTableName())
                .setDsKey(getDsKey())
                .setId(existId));
    }

    public void updateModuleIndexIfChanged(LogicIndex logicIndex) {
        ModuleIndex moduleIndex = Optional.ofNullable(useLogicTable().useModelTable().getModuleIndexMap())
                .map(v -> v.get(logicIndex.getIndexName())).orElse(null);
        if (null == moduleIndex) {
            return;
        }
        String tableSchema = this.useLogicTable().getTableSchema();
        String tableName = this.useLogicTable().getTableName();
        if (!moduleIndex.getTableSchema().equals(tableSchema)
                || !moduleIndex.getTableName().equals(tableName)) {
            moduleIndex.setTableSchema(tableSchema)
                    .setTableName(tableName)
                    .setUsing(true)
                    .setChanged(true);
        }
    }

    public ModelTable fetchTable() {
        return this.useLogicTable().useModelTable();
    }

    public FieldColumn fetchColumn(String field) {
        Map<String, FieldColumn> fieldColumnMap = this.useLogicTable().useModelTable().getFieldColumnMap();
        return MapUtils.computeIfAbsent(fieldColumnMap, field, k -> new FieldColumn());
    }

    public ModuleIndex fetchIndex(String indexName) {
        Map<String, ModuleIndex> moduleIndexMap = this.useLogicTable().useModelTable().getModuleIndexMap();
        return MapUtils.computeIfAbsent(moduleIndexMap, indexName, k -> new ModuleIndex());
    }

    public void changeTable() {
        this.fetchTable().setChanged(true);
    }

    public void changeColumn(String field) {
        this.fetchColumn(field).setChanged(true);
    }

    public void changeIndex(String indexName) {
        this.fetchIndex(indexName).setChanged(true);
    }

    public void unDropTable() {
        this.fetchTable().setUsing(true);
    }

    public void unDropColumn(String field) {
        FieldColumn column = fetchColumn(field);
        if (null == column) {
            return;
        }
        column.setUsing(true);
    }

    public void unDropIndex(String indexName) {
        ModuleIndex index = fetchIndex(indexName);
        if (null == index) {
            return;
        }
        index.setUsing(true);
    }

    public void dropColumn(String field) {
        FieldColumn column = fetchColumn(field);
        if (null == column) {
            return;
        }
        if (null != column.getUsing() && column.getUsing()) {
            return;
        }
        column.setUsing(false);
    }

    public void dropIndex(String indexName) {
        ModuleIndex index = fetchIndex(indexName);
        if (null == index) {
            return;
        }
        if (null != index.getUsing() && index.getUsing()) {
            return;
        }
        index.setUsing(false);
    }

}
