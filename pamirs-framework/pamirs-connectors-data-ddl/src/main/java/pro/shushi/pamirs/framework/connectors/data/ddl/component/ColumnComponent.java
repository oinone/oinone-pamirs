package pro.shushi.pamirs.framework.connectors.data.ddl.component;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.framework.connectors.data.ddl.check.ColumnChecker;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.ColumnDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.ColumnInfo;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 列操作组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class ColumnComponent {

    @Resource
    private PamirsMapperConfiguration pamirsMapperConfiguration;

    @Resource
    private ColumnChecker columnChecker;

    public void handleLogicField(DdlContext ddlContext, List<String> ddlList, boolean create, String dsKey,
                                 ModelWrapper modelDefinition,
                                 String table, Map<String, Column> columnMap, List<String> columns) {
        Set<String> existColumns = new HashSet<>();
        if (!MapUtils.isEmpty(columnMap)) {
            for (Column column : columnMap.values()) {
                existColumns.add(column.getColumnName());
            }
        }
        if (!CollectionUtils.isEmpty(columns)) {
            existColumns.addAll(columns);
        }
        List<ColumnInfo> columnInfoList = pamirsMapperConfiguration.getLogicColumnFetcher().fetchLogicColumnDefinitions(modelDefinition.getModel());
        for (ColumnInfo columnInfo : columnInfoList) {
            String column = columnInfo.getColumn();
            if (null != columnMap && columnMap.containsKey(column) && !existColumns.contains(column)) {
                modifyColumn(ddlList, dsKey, table,
                        columnMap.get(column).getColumnName(), column, columnInfo.getColumnDefinition(),
                        columnInfo.getSummary(), null);
                ddlContext.refreshLogicColumn(column, columnInfo.getColumnDefinition(), columnInfo.getSummary());
            } else if (!existColumns.contains(column) && !existColumns.contains(column.toUpperCase())) {
                addColumn(ddlList, dsKey, create, table, column, columnInfo.getColumnDefinition(),
                        columnInfo.getSummary(), null);
                ddlContext.refreshLogicColumn(column, columnInfo.getColumnDefinition(), columnInfo.getSummary());
            }
        }
    }

    public boolean isAutoIncrementColumn(String dsKey, Column column) {
        return Dialects.component(ColumnDialectComponent.class, dsKey).isAutoIncrementColumn(column);
    }

    public void addColumn(List<String> ddlList, String dsKey, boolean create, String table,
                          String column, String columnDefinition,
                          String summary, String previousColumn) {
        if (create) {
            ddlList.add(Dialects.component(ColumnDialectComponent.class, dsKey).createColumn(column, columnDefinition, summary));
        } else {
            ddlList.add(Dialects.component(ColumnDialectComponent.class, dsKey).addColumn(table, column, columnDefinition,
                    summary, previousColumn));
        }
    }

    public void modifyColumn(List<String> ddlList, String dsKey, String table,
                             String column, String newName, String columnDefinition,
                             String summary, String previousColumn) {
        ddlList.add(fetchModifyColumnDdl(dsKey, table, column, newName, columnDefinition, summary, previousColumn));
    }

    public String fetchModifyColumnDdl(String dsKey, String table,
                                       String column, String newName, String columnDefinition,
                                       String summary, String previousColumn) {
        return Dialects.component(ColumnDialectComponent.class, dsKey).modifyColumn(table, column, newName, columnDefinition,
                summary, previousColumn);
    }

    public Column fetchExistAutoIncrementColumn(String dsKey, Map<String/*field | columnName*/, Column> columnMap, String column) {
        Column existAutoIncrementColumn = null;
        for (String key : columnMap.keySet()) {
            Column temp = columnMap.get(key);
            if (column.equals(temp.getColumnName()) && isAutoIncrementColumn(dsKey, temp)) {
                existAutoIncrementColumn = temp;
            }
        }
        return existAutoIncrementColumn;
    }

    public void deleteColumn(DdlContext ddlContext, List<String> ddlList, String dsKey, LogicTable table) {
        if (Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign()) {
            return;
        }
        if (null == table.useModelTable().getSupportDrop() || !table.useModelTable().getSupportDrop()) {
            return;
        }
        ColumnDialectComponent columnDialectComponent = Dialects.component(ColumnDialectComponent.class, dsKey);
        Map<String/*field | column*/, FieldColumn> fieldColumnMap = ddlContext.useLogicTable().useModelTable().getFieldColumnMap();
        if (MapUtils.isEmpty(fieldColumnMap)) {
            return;
        }
        // 删除废弃的逻辑字段
        Set<String> deprecatedColumns = ddlContext.getDeprecatedColumns();
        Map<String/*field | column*/, Column> existColumnMap = Optional.ofNullable(ddlContext.getExistLogicTable())
                .map(LogicTable::getColumnMap).orElse(null);
        if (!CollectionUtils.isEmpty(deprecatedColumns) && null != existColumnMap) {
            Iterator<Map.Entry<String, Column>> iterator = existColumnMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Column> entry = iterator.next();
                String column = entry.getKey();
                if (deprecatedColumns.contains(column)) {
                    ddlList.add(columnDialectComponent.deleteColumn(table.getTableName(), entry.getValue()));
                    iterator.remove();
                }
            }
        }
        // 删除字段
        for (FieldColumn fieldColumn : fieldColumnMap.values()) {
            if (!columnChecker.drop(table.getModule(), table.getModel(), fieldColumn)) {
                continue;
            }
            if (null == fieldColumn.getUsing() || !fieldColumn.getUsing()) {
                Column deleteColumn = ddlContext.useLogicTable().getColumnMap().get(fieldColumn.getField());
                if (null == deleteColumn) {
                    continue;
                }
                ddlList.add(columnDialectComponent.deleteColumn(table.getTableName(), deleteColumn));
            }
        }
    }

    public String columnDefinition(String dsKey, FieldWrapper modelField) {
        return Dialects.component(ColumnDialectComponent.class, dsKey).columnDefinition(modelField);
    }

    public String columnDefinition(String dsKey, Column column, boolean changeCharset) {
        return Dialects.component(ColumnDialectComponent.class, dsKey).columnDefinition(column, changeCharset);
    }

    public String columnDefinition(String dsKey, Column column, boolean changeCharset, boolean autoIncrement) {
        return Dialects.component(ColumnDialectComponent.class, dsKey).columnDefinition(column, changeCharset, autoIncrement);
    }

    public String columnPlaceholder(String dsKey, String column) {
        return Dialects.component(ColumnDialectComponent.class, dsKey).columnPlaceholder(column);
    }

    public boolean isCharsetChange(String dsKey, FieldWrapper modelField, Column column) {
        return !Dialects.component(ColumnDialectComponent.class, dsKey).equalsCharset(modelField, column);
    }

}
