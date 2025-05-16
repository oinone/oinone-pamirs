package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.mysql;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicIndex;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Index;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Table;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.ddl.constants.SystemTableConstants;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.SchemaUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.MetaDataDialectService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql.ColumnMapper;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql.IndexMapper;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql.TableMapper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.ListUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MYSQL元数据方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect.component
@SPI.Service
@Component
public class MysqlMetaDataDialectService implements MetaDataDialectService {

    @Resource
    private TableMapper tableMapper;

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private IndexMapper indexMapper;

    @Override
    public Map<String/*schema#table*/, LogicTable> fetchLogicTableMap(Map<String/*schema#table*/, ModelTable> modelTableMap,
                                                                      Map<String/*schema*/, String/*ds*/> databaseMap,
                                                                      boolean supportDrop) {
        List<String> databaseList = ListUtils.toList(databaseMap.keySet());
        List<Table> tableList = tableMapper.selectTableListInDatabases(databaseList);
        if (CollectionUtils.isEmpty(tableList)) {
            return null;
        }
        Map<String/*schema#table*/, LogicTable> logicTableMap = new HashMap<>();
        Map<String/*schema#table#column*/, FieldColumn> fieldColumnMap = new HashMap<>();
        for (Table table : tableList) {
            String key = SchemaUtils.getSchemaTableKey(table.getTableSchema(), table.getTableName());
            ModelTable modelTable = Optional.ofNullable(modelTableMap).map(v -> v.get(key)).orElse(null);
            String module = ArrayUtils.contains(SystemTableConstants.tables, table.getTableName().toUpperCase())
                    ? null : (null == modelTable ? null : modelTable.getModule());
            String model = Optional.ofNullable(modelTable).map(ModelTable::getModel).orElse(null);
            LogicTable logicTable = new LogicTable();
            logicTable.setModule(module);
            logicTable.setModel(model);
            logicTable.setDsKey(databaseMap.get(table.getTableSchema()));
            logicTable.setTableSchema(table.getTableSchema());
            logicTable.setTableName(table.getTableName());
            logicTable.setTableComment(table.getTableComment());
            logicTable.setTableCollation(table.getTableCollation());
            logicTable.setCharacterSetName(table.getCharacterSetName());
            logicTable.setModelTable(modelTable);
            logicTableMap.putIfAbsent(key, logicTable);

            if (null != modelTable) {
                logicTable.setSharding(modelTable.getSharding());
                if (null != modelTable.getFieldColumnMap()) {
                    for (String field : modelTable.getFieldColumnMap().keySet()) {
                        FieldColumn fieldColumn = modelTable.getFieldColumnMap().get(field);
                        fieldColumnMap.put(SchemaUtils.getColumnKey(table.getTableSchema(), table.getTableName(), fieldColumn.getColumnName()), fieldColumn);
                    }
                }
            }
        }
        List<Column> columnList = columnMapper.selectColumnListInDatabases(databaseList);
        if (CollectionUtils.isNotEmpty(columnList)) {
            for (Column column : columnList) {
                FieldColumn fieldColumn = fieldColumnMap.get(SchemaUtils.getColumnKey(column.getTableSchema(), column.getTableName(), column.getColumnName()));
                String field = FieldColumn.isEmpty(fieldColumn) ? column.getColumnName() : fieldColumn.getField();
                logicTableMap.get(SchemaUtils.getSchemaTableKey(column.getTableSchema(), column.getTableName()))
                        .getColumnMap().put(field, column);
            }
        }
        List<Index> indexList = indexMapper.selectIndexListInDatabases(databaseList);
        for (Index index : indexList) {
            LogicTable logicTable = logicTableMap.get(SchemaUtils.getSchemaTableKey(index.getTableSchema(), index.getTableName()));
            boolean addFirst = true;
            if (MapUtils.isNotEmpty(logicTable.getIndexMap())) {
                LogicIndex logicIndex = logicTable.getIndexMap().get(index.getIndexName());
                if (null != logicIndex) {
                    logicIndex.getIndexList().add(index);
                    logicIndex.getIndexList().sort(Comparator.comparingInt(Index::getSeqInIndex));
                    logicIndex.setColumn(logicIndex.getIndexList().stream().map(Index::getColumnName).collect(Collectors.toList()));
                    addFirst = false;
                }
            }
            if (addFirst) {
                logicTable.getIndexMap().put(index.getIndexName(),
                        new LogicIndex()
                                .setIndexName(index.getIndexName())
                                .setUnique(index.getUnique())
                                .setTableName(index.getTableName())
                                .setIndexList(Lists.newArrayList(index))
                                .setColumn(Lists.newArrayList(index.getColumnName()))
                );
            }
        }
        return logicTableMap;
    }

}
