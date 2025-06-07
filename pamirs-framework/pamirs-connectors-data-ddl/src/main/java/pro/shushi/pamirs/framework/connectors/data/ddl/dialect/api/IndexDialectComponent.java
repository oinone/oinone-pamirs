package pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicIndex;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.DdlUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 索引操作方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface IndexDialectComponent {

    int defaultMaxDbIdentifierLength = 64;

    default int fetchMaxDbIdentifierLength() {
        return defaultMaxDbIdentifierLength;
    }

    default String indexName(String tableName, String completedTableName, String indexName, List<String> columns) {
        if (StringUtils.isNotBlank(indexName)) {
            return indexName;
        }
        indexName = DdlUtils.buildString(tableName,
                CharacterConstants.SEPARATOR_UNDERLINE, StringUtils.join(columns, CharacterConstants.SEPARATOR_UNDERLINE));
        return DdlUtils.fixIdentifyLength(indexName, fetchMaxDbIdentifierLength(), true);
    }

    default String primaryIndexName(String completedTableName) {
        return "PRIMARY";
    }

    default String generatePrimaryColumn(List<String> pkColumns, boolean addQuote) {
        return StringUtils.join(pkColumns, addQuote ? "`,`" : ",");
    }

    default String generatePrimaryColumn(List<String> pkColumns, boolean addQuote, LogicIndex logicIndex) {
        return generatePrimaryColumn(pkColumns, addQuote);
    }

    default String createPrimaryKey(String completedTableName, List<String> pkList) {
        return DdlUtils.buildString(" PRIMARY KEY (`", generatePrimaryColumn(pkList, Boolean.TRUE), "`)\n");
    }

    default void fixCreatePrimaryKey(List<String> ddlList) {
        String last = ddlList.remove(ddlList.size() - 1);
        ddlList.add(ddlList.size(), StringUtils.substringBeforeLast(last, CharacterConstants.SEPARATOR_COMMA) + "\n");
    }

    default String addPrimaryKey(String completedTableName, List<String> pkList) {
        return DdlUtils.buildString("ALTER TABLE `", completedTableName,
                "` ADD PRIMARY KEY (`", generatePrimaryColumn(pkList, Boolean.TRUE), "`);\n");
    }

    default String autoIncrement(ModelWrapper modelDefinition, FieldWrapper modelField, String columnDefinition, String keyGenerator) {
        return columnDefinition;
    }

    default String removeAutoIncrement(List<String/*columnName*/> deleteColumns, String columnName, String columnDefinition) {
        if (CollectionUtils.isEmpty(deleteColumns) || !deleteColumns.contains(columnName)) {
            return columnDefinition;
        }
        return columnDefinition.toUpperCase()
                .replace(" AUTO_INCREMENT", CharacterConstants.SEPARATOR_EMPTY);
    }

    default String createIndex(boolean unique, String completedTableName, String indexName, String[] indexColumns) {
        return DdlUtils.buildString("CREATE", unique ? " UNIQUE" : "", " INDEX `", indexName, "` ON `",
                completedTableName,
                "`(`", StringUtils.join(indexColumns, "`,`"), "`);\n");
    }

    default String dropIndex(String completedTableName, LogicIndex index) {
        return DdlUtils.buildString("DROP INDEX `", index.getIndexName(), "` ON `", completedTableName, "`;\n");
    }

}
