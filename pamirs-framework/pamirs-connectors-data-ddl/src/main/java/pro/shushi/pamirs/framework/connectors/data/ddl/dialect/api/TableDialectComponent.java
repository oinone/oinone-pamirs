package pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.enmu.DdlExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.DdlUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.SqlTemplate;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.meta.common.constants.VariableNameConstants.deprecated;

/**
 * 表操作方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TableDialectComponent {

    String PREFIX_TEMPLATE = "d12345_p_";

    String SHARDING_TEMPLATE = "_1024";

    String DEPRECATED_TEMPLATE = "_d__1604380252586";

    int defaultMaxDbIdentifierLength = 64 - PREFIX_TEMPLATE.length() - SHARDING_TEMPLATE.length();

    default int fetchMaxDbIdentifierLength() {
        return defaultMaxDbIdentifierLength;
    }

    default String tablePlaceholder(ModelWrapper modelDefinition) {
        String module = modelDefinition.getModule();
        return DataPrefixManager.tablePrefix(module, modelDefinition.getModel(), modelDefinition.getTable());
    }

    default String tablePlaceholder(LogicTable table) {
        return table.getTableName();
    }

    @SuppressWarnings("unused")
    default List<String> dropTable(LogicTable deprecatedTable) {
        List<String> ddlList = new ArrayList<>();
        if (deprecatedTable.getTableName().startsWith(deprecated)) {
            return null;
        }
        String tableName = deprecatedTable.getTableName();
        String deprecatedName = DdlUtils.fixIdentifyLength(tableName,
                fetchMaxDbIdentifierLength() - DEPRECATED_TEMPLATE.length(), true);
        ddlList.add(DdlUtils.buildString("ALTER TABLE `", tableName, "` COMMENT '",
                deprecatedTable.getTableComment(), "，!!废弃表';\n"));
        ddlList.add(DdlUtils.buildString("RENAME TABLE `", tableName, "` TO `",
                deprecated, deprecatedName, CharacterConstants.SEPARATOR_UNDERLINE + System.currentTimeMillis(), "`;\n"));
        return ddlList;
    }

    default String lock(ModelWrapper modelDefinition) {
        return DdlUtils.buildString("LOCK TABLES `", tablePlaceholder(modelDefinition), "` WRITE;\n");
    }

    default String unlock() {
        return DdlUtils.buildString("UNLOCK TABLES;\n");
    }

    default String rename(ModelWrapper modelDefinition, LogicTable table) {
        String newTableName = checkTableNameLength(modelDefinition);
        return DdlUtils.buildString("RENAME TABLE `", tablePlaceholder(table), "` TO `", newTableName, "`;\n");
    }

    default String changeRemark(ModelWrapper modelDefinition) {
        return DdlUtils.buildString("ALTER TABLE `", tablePlaceholder(modelDefinition), "` COMMENT '",
                modelDefinition.getRemark(), "';\n");
    }

    default boolean equalsCharset(ModelWrapper modelDefinition, String charset, String collation) {
        if (null == charset && null != collation) {
            charset = collation.split(CharacterConstants.SEPARATOR_UNDERLINE)[0];
        }
        return null != charset && charset.equals(charset(modelDefinition)) && null != collation && collation.equals(collation(modelDefinition));
    }

    default String charset(ModelWrapper modelDefinition) {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(modelDefinition.getModel());
        return pamirsTableInfo.getCharset();
    }

    default String collation(ModelWrapper modelDefinition) {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(modelDefinition.getModel());
        String charset = pamirsTableInfo.getCharset();
        String configCollate = pamirsTableInfo.getCollate();
        return charset + CharacterConstants.SEPARATOR_UNDERLINE + configCollate;
    }

    default String changeCharset(ModelWrapper modelDefinition) {
        return DdlUtils.buildString("ALTER TABLE `", tablePlaceholder(modelDefinition), "` CHARACTER SET = ",
                charset(modelDefinition), ", COLLATE = ", collation(modelDefinition), ";\n");
    }

    default String createTableHead(ModelWrapper modelDefinition) {
        String newTableName = checkTableNameLength(modelDefinition);
        return DdlUtils.buildString("CREATE TABLE IF NOT EXISTS `", newTableName, "`(\n");
    }

    default String checkTableNameLength(ModelWrapper modelDefinition) {
        String newTableName = tablePlaceholder(modelDefinition);
        int newTableNameLength = newTableName.length();
        int originTableNameLength = modelDefinition.getTable().length();
        if (originTableNameLength > fetchMaxDbIdentifierLength()) {
            int appendixLength = newTableNameLength - originTableNameLength;
            throw PamirsException.construct(DdlExpEnumerate.BASE_DDL_TABLE_NAME_LENGTH_ERROR)
                    .appendMsg("model:" + modelDefinition.getModel() + ",new name:" + newTableName
                            + ",limit:" + (fetchMaxDbIdentifierLength() + appendixLength) + ",actual:" + newTableNameLength)
                    .errThrow();
        }
        return newTableName;
    }

    default String createTableTail(ModelWrapper modelDefinition) {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(modelDefinition.getModel());
        String charset = pamirsTableInfo.getCharset();
        String collate = pamirsTableInfo.getCollate();
        return DdlUtils.buildString(")ENGINE=InnoDB DEFAULT CHARSET=", charset, " COLLATE=", charset, "_", collate, " COMMENT '",
                modelDefinition.getRemark(), "';\n");
    }

    default String lockTableCommandPrefix() {
        return SqlTemplate.LOCK_TABLES;
    }

    default String unlockTableCommandPrefix() {
        return SqlTemplate.UNLOCK_TABLES;
    }

    default void fixDdl(List<String> ddlList) {

    }
}
