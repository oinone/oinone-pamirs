package pro.shushi.pamirs.framework.connectors.data.ddl.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.check.ColumnChecker;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.ColumnComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.IndexComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.TableComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.DdlUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改字段
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class ChangeColumnProcessor {

    @Resource
    private FieldProcessor fieldProcessor;

    @Resource
    private TableComponent tableComponent;

    @Resource
    private ColumnComponent columnComponent;

    @Resource
    private IndexComponent indexComponent;

    @Resource
    private ColumnChecker columnChecker;

    public void change(DdlContext ddlContext, List<String> ddlList, ModelWrapper modelDefinition, LogicTable table) {
        String tableName = tableComponent.tablePlaceholder(table.getDsKey(), modelDefinition);
        // 修改字段
        List<String> newStoreColumns = new ArrayList<>();
        // 字段排序
        modelDefinition.getModelFields().sort((o1, o2) -> (int) (o1.getPriority() - o2.getPriority()));
        Map<String, String> dealEdMap = new HashMap<>();
        for (FieldWrapper modelField : modelDefinition.getModelFields()) {
            String fieldColumn = modelField.getColumn();
            if (fieldColumn != null) {
                fieldColumn = columnComponent.columnPlaceholder(table.getDsKey(), fieldColumn);
                modelField.setColumn(fieldColumn);
            }
            if (dealEdMap.get(fieldColumn) != null) {
                //已经处理过的不处理
                continue;
            }
            dealEdMap.put(fieldColumn, fieldColumn);

            // 字段校验
            boolean validateResult = columnChecker.change(ddlContext, modelField);
            if (!validateResult) {
                continue;
            }
            ddlContext.unDropColumn(modelField.getField());
            // 忽略扩展继承字段和被重写字段
            if (columnChecker.extend(modelField) && !modelDefinition.getIsChangeTable()
                    || columnChecker.override(modelField.getModel(), ddlContext.fetchColumn(modelField.getField()))) {
                continue;
            }
            // 预处理索引
            indexComponent.prepareIndexes(ddlContext.getLogicColumns(),
                    ddlContext.getIndexColumnSet(), ddlContext.getUniqueColumnSet(), modelField);
            // 处理字段
            String fieldColumnDefinition = fieldProcessor.prepareModelFields(table.getDsKey(), modelDefinition, modelField);
            String fieldField = modelField.getField();
            Column column = table.getColumnMap().get(fieldField);
            if (null == column) {
                // 处理字段先生成，但还没有生成对应元数据的情况
                column = table.getColumnMap().get(fieldColumn);
                String s = fieldField;
                if (null == column) {
                    // 处理字段先生成，但还没有生成对应元数据的情况
                    x:
                    for (Map.Entry<String, Column> columnEntry : table.getColumnMap().entrySet()) {
                        if (columnEntry.getValue().getColumnName().equals(fieldColumn)) {
                            s = columnEntry.getKey();
                            column = columnEntry.getValue();
                            break x;
                        }
                    }
                    if (!s.equals(fieldField)) {
                        //纠正，列名一样，但是字段名称不一样
                        table.getColumnMap().remove(s);
                        table.getColumnMap().put(fieldField, column);
                    }
                }
            }

            if (null != column) {
                boolean changeCharset = columnComponent.isCharsetChange(table.getDsKey(), modelField, column);
                // alter column
                if (!fieldColumn.equals(column.getColumnName())
                        || !fieldColumnDefinition.trim().equals(columnComponent.columnDefinition(table.getDsKey(), column, changeCharset).trim())
                        || DdlUtils.notEqualsIgnoreNull(column.getColumnComment(), modelField.getSummary())
                        || changeCharset
                ) {
                    columnComponent.modifyColumn(ddlList, table.getDsKey(), tableName, column.getColumnName(), fieldColumn,
                            fieldColumnDefinition, modelField.getSummary(), null, column);
                    ddlContext.refreshColumn(modelField, fieldColumnDefinition);
                    ddlContext.changeColumn(modelField.getField());
                } else if (!StringUtils.equals(ddlContext.useLogicTable().getTableSchema(), column.getTableSchema())
                        || !StringUtils.equals(ddlContext.useLogicTable().getTableName(), column.getTableName())) {
                    ddlContext.refreshColumn(modelField, fieldColumnDefinition);
                    ddlContext.changeColumn(modelField.getField());
                }
            } else {
                newStoreColumns.add(fieldColumn);
                // add column
                columnComponent.addColumn(ddlList, table.getDsKey(), Boolean.FALSE, tableName,
                        fieldColumn, fieldColumnDefinition, modelField.getSummary(), null);
                ddlContext.refreshColumn(modelField, fieldColumnDefinition);
                ddlContext.changeColumn(modelField.getField());
            }
            ddlContext.updateFieldColumn(modelField, modelDefinition.getModule());
        }
        // 处理逻辑字段
        columnComponent.handleLogicField(ddlContext, ddlList, Boolean.FALSE, table.getDsKey(), modelDefinition, tableName, table.getColumnMap(), newStoreColumns);
    }

}
