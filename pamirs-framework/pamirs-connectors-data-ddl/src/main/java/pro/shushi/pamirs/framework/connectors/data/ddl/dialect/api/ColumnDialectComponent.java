package pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.constant.SystemValueConstants;
import pro.shushi.pamirs.framework.connectors.data.ddl.enmu.DdlExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.DdlUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.constant.SqlConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.shushi.pamirs.meta.common.constants.VariableNameConstants.deprecated;

/**
 * 列操作方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ColumnDialectComponent {

    int defaultMaxDbIdentifierLength = 64;

    String DEPRECATED_TEMPLATE = "_d__1604380252586";

    Pattern DEFAULT_VALUE_PATTERN = Pattern.compile("DEFAULT\\s([^\\s.]*)");

    Pattern REMOVE_COLUMN_WIDTH_PATTERN = Pattern.compile("\\(.*\\)");

    String NOT_NULL = "NOT NULL";

    default int fetchMaxDbIdentifierLength() {
        return defaultMaxDbIdentifierLength;
    }

    default String columnPlaceholder(String column) {
        return column;
    }

    default String columnDefinition(FieldWrapper modelField) {
        if (StringUtils.isBlank(modelField.getColumnDefinition())) {
            TypeProcessor typeProcessor = Spider.getExtension(TypeProcessor.class, NamespaceConstants.spiDefault);
            String columnDefinition = typeProcessor.defaultColumnTypeFromTtype(modelField.getTtype(),
                    modelField.getLtype(), modelField.getMulti(), modelField.getBit(), modelField.getSize(), modelField.getDecimal());
            if (null != modelField.getIsPrimaryKey() && modelField.getIsPrimaryKey()) {
                columnDefinition += CharacterConstants.SEPARATOR_BLANK + ColumnDialectComponent.NOT_NULL;
            }
            String fieldCharsetString = CharacterConstants.SEPARATOR_EMPTY;
            if (needSetCharset(columnDefinition)) {
                fieldCharsetString = generateCharsetString(modelField.getCharset(), modelField.getCollation());
                if (StringUtils.isNotBlank(fieldCharsetString)) {
                    fieldCharsetString = CharacterConstants.SEPARATOR_BLANK + fieldCharsetString;
                }
            }
            return columnDefinition + fieldCharsetString;
        } else {
            return formatColumnDefinition(modelField.getColumnDefinition());
        }
    }

    default String columnDefinition(Column column, boolean changeCharset) {
        return columnDefinition(column, changeCharset, true);
    }

    default String columnDefinition(Column column, boolean changeCharset, boolean autoIncrement) {
        List<String> defs = new ArrayList<>();
        defs.add(StringUtils.upperCase(formatColumnType(column.getColumnType())));
        if (changeCharset) {
            String charsetString = generateCharsetString(column.getCharacterSetName(), column.getCollationName());
            if (StringUtils.isNotBlank(charsetString)) {
                defs.add(charsetString.toUpperCase());
            }
        }
        if (!SystemValueConstants.YES.equals(column.getNullable())) {
            defs.add(NOT_NULL);
        }
        if (null != column.getDefaultValue()) {
            defs.add("DEFAULT");
            if (Boolean.TRUE.toString().equals(column.getDefaultValue())) {
                defs.add("'1'");
            } else if (Boolean.FALSE.toString().equals(column.getDefaultValue())) {
                defs.add("'0'");
            } else if ("CURRENT_TIMESTAMP".equals(column.getDefaultValue())) {
                defs.add(column.getDefaultValue());
            } else {
                defs.add("'" + column.getDefaultValue() + "'");
            }
        }
        String extra = column.getExtra();
        if (null != extra) {
            String extraLowerCase = extra.toLowerCase();
            if (extraLowerCase.contains("on update current_timestamp")) {
                defs.add("ON UPDATE CURRENT_TIMESTAMP");
            } else if (autoIncrement && extraLowerCase.contains("auto_increment")) {
                defs.add("AUTO_INCREMENT");
            }
        }
        return StringUtils.join(defs, CharacterConstants.SEPARATOR_BLANK);
    }

    default String formatColumnDefinition(String columnDefinition) {
        String[] defs = columnDefinition.split(CharacterConstants.SEPARATOR_BLANK);
        defs[0] = formatColumnType(defs[0].trim());
        return StringUtils.join(defs, CharacterConstants.SEPARATOR_BLANK);
    }

    default String formatColumnType(String columnType) {
        String lowCaseColumnType = columnType.toLowerCase();
        if (!lowCaseColumnType.equals("tinyint(1)")
                && (lowCaseColumnType.contains("int") || lowCaseColumnType.startsWith("year"))) {
            return REMOVE_COLUMN_WIDTH_PATTERN.matcher(columnType)
                    .replaceAll(Matcher.quoteReplacement(CharacterConstants.SEPARATOR_EMPTY));
        } else {
            return columnType;
        }
    }

    default Column fillColumn(Column column, String columnDefinition) {
        String columnType = columnDefinition.split("\\s")[0];
        String nullable = columnDefinition.contains("NOT NULL") || columnDefinition.contains("not null") ? SystemValueConstants.NO : SystemValueConstants.YES;
        Matcher matcher = DEFAULT_VALUE_PATTERN.matcher(columnDefinition);
        String defaultValue = null;
        if (matcher.find()) {
            defaultValue = matcher.group(1);
        }
        String extra = columnDefinition.contains("ON UPDATE CURRENT_TIMESTAMP") ? "DEFAULT_GENERATED on update CURRENT_TIMESTAMP" : "";
        extra = columnDefinition.contains("AUTO_INCREMENT") ? "auto_increment" : extra;
        return column.setColumnType(columnType).setNullable(nullable).setDefaultValue(defaultValue).setExtra(extra);
    }

    default boolean needSetCharset(String columnDefinition) {
        columnDefinition = columnDefinition.toLowerCase();
        return columnDefinition.startsWith("varchar") || columnDefinition.startsWith("char") || columnDefinition.startsWith("text");
    }

    default boolean equalsCharset(FieldWrapper modelField, Column column) {
        if (!needSetCharset(column.getColumnType())) {
            return true;
        }
        String charset = column.getCharacterSetName();
        String collation = column.getCollationName();
        if (null == charset && null != collation) {
            charset = collation.split(CharacterConstants.SEPARATOR_UNDERLINE)[0];
        }
        String fieldCharset = modelField.getCharset();
        String fieldCollate = modelField.getCollation();
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(modelField.getModel());
        if (StringUtils.isBlank(fieldCharset)) {
            fieldCharset = Optional.ofNullable(pamirsTableInfo).map(PamirsTableInfo::getCharset).orElse(null);
        }
        if (StringUtils.isBlank(fieldCollate)) {
            fieldCollate = Optional.ofNullable(pamirsTableInfo).map(PamirsTableInfo::getCollate).orElse(null);
        }
        return null != charset && charset.equals(charset(fieldCharset)) && null != collation && collation.equals(collation(fieldCharset, fieldCollate));
    }

    default String charset(FieldWrapper modelField) {
        String charset = modelField.getCharset();
        return charset(charset);
    }

    default String charset(String charset) {
        return charset;
    }

    @SuppressWarnings("unused")
    default String collation(FieldWrapper modelField) {
        String charset = modelField.getCharset();
        String configCollate = modelField.getCollation();
        return collation(charset, configCollate);
    }

    default String collation(String charset, String collation) {
        return charset + CharacterConstants.SEPARATOR_UNDERLINE + collation;
    }

    default String generateCharsetString(String charset, String collation) {
        String charsetString = CharacterConstants.SEPARATOR_EMPTY;
        if (StringUtils.isNotBlank(charset)) {
            charsetString = "CHARACTER SET " + charset(charset);
            if (StringUtils.isNotBlank(collation)) {
                if (collation.startsWith(charset)) {
                    charsetString += (" COLLATE " + collation);
                } else {
                    charsetString += (" COLLATE " + collation(charset, collation));
                }
            }
        }
        return charsetString;
    }

    default boolean isAutoIncrementColumn(Column column) {
        return column.getExtra().contains(SqlConstants.AUTO_INCREMENT)
                || column.getExtra().contains(SqlConstants.AUTO_INCREMENT.toLowerCase());
    }

    default String createColumn(String column, String columnDefinition, String summary) {
        return DdlUtils.buildString(" `", checkColumnLength(column), "` ", columnDefinition, " COMMENT '", summary, "',\n");
    }

    default String addColumn(String table, String column, String columnDefinition, String summary, String previousColumn) {
        return DdlUtils.buildString("ALTER TABLE `", table, "` ADD COLUMN `",
                checkColumnLength(column), "` ", columnDefinition, " COMMENT '",
                summary, "';\n");
    }

    default String modifyColumn(String table, String column, String newName, String columnDefinition,
                                String summary, String previousColumn) {
        return DdlUtils.buildString("ALTER TABLE `", table, "` CHANGE `",
                columnPlaceholder(column), "` `", checkColumnLength(newName), "` ", columnDefinition, " COMMENT '",
                summary, "';\n");
    }

    default String checkColumnLength(String column) {
        String newName = columnPlaceholder(column);
        if (newName.length() > fetchMaxDbIdentifierLength()) {
            throw PamirsException.construct(DdlExpEnumerate.BASE_DDL_COLUMN_NAME_LENGTH_ERROR)
                    .appendMsg("column:" + column + ",new name:" + newName
                            + ",limit:" + fetchMaxDbIdentifierLength() + ",actual:" + newName.length()).errThrow();
        }
        return newName;
    }

    default String deleteColumn(String table, Column deleteColumn) {
        String deprecatedName = DdlUtils.fixIdentifyLength(deleteColumn.getColumnName(),
                fetchMaxDbIdentifierLength() - DEPRECATED_TEMPLATE.length(), false);
        // 删除字段
        deleteColumn.setNullable(SystemValueConstants.YES);
        return DdlUtils.buildString("ALTER TABLE `", table, "` CHANGE `",
                deleteColumn.getColumnName(), "`", CharacterConstants.SEPARATOR_BLANK, "`",
                deprecated, deprecatedName, CharacterConstants.SEPARATOR_UNDERLINE + System.currentTimeMillis(), "`", CharacterConstants.SEPARATOR_BLANK,
                columnDefinition(deleteColumn, false).replace(SqlConstants.AUTO_INCREMENT, CharacterConstants.SEPARATOR_EMPTY),
                " COMMENT '", deleteColumn.getColumnComment(), "，!!废弃字段';\n");
    }

}
