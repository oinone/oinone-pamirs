package pro.shushi.pamirs.framework.connectors.data.ddl.dialect.mysql;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;
import pro.shushi.pamirs.framework.connectors.data.constant.SystemValueConstants;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.ColumnDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 列操作组件
 *
 * @author d@shushi.pro
 * @author wangxian
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 *
 *
 * MySQL 族列操作组件（MariaDB / TiDB / OceanBase-MySQL 等亦按 {@code type=MySQL} 走本类）。
 *
 * <p><b>为何改在这里：</b>平台无独立 MariaDB 方言；Oracle/PG/MSSQL 等在 EE 中各自覆盖
 * {@code columnDefinition}/{@code fillColumn}，不受本类影响。标准 MySQL 8 的 COLUMN_DEFAULT
 * 仍是 JDBC null / 未加引号字面量 / {@code CURRENT_TIMESTAMP}，归一化分支对它们基本是 no-op。</p>
 *
 * <p><b>问题：</b>MariaDB 10.2.7+（含 12.3.2）与 MySQL 对
 * {@code INFORMATION_SCHEMA.COLUMNS.COLUMN_DEFAULT} 语义不一致。物理列按库值重建定义后，
 * 与模型侧对比（{@code ChangeColumnProcessor}）会一直不相等，每次启动重复 ALTER。
 * 更早 MariaDB（&lt;10.2.7）行为接近 MySQL，故历史用例不易暴露。</p>
 *
 * <p><b>差异与处理：</b></p>
 * <ul>
 *   <li>可空无默认 / {@code DEFAULT NULL}：MariaDB 返回字面量 {@code "NULL"}
 *       （HEX=4E554C4C，{@code IS NULL=0}），MySQL 为 JDBC null → 当作无默认。
 *       见 <a href="https://jira.mariadb.org/browse/MDEV-13341">MDEV-13341</a>、
 *       <a href="https://jira.mariadb.org/browse/MDEV-40428">MDEV-40428</a></li>
 *   <li>字符串默认已带引号（如 {@code 'abc'}）→ 不再二次加引号</li>
 *   <li>{@code current_timestamp()}（及带括号的 now/localtime 同义词）→ 规范为
 *       {@code CURRENT_TIMESTAMP}；仅作用于 datetime/timestamp/date，避免误伤字符串默认值</li>
 * </ul>
 */
@Slf4j
@Dialect.component
@SPI.Service(DataProductVersion.PRODUCT_MYSQL)
@Component
public class MysqlColumnComponent implements ColumnDialectComponent {

    private static final Pattern CURRENT_TIMESTAMP_PATTERN =
            Pattern.compile("^(CURRENT_TIMESTAMP|NOW|LOCALTIME|LOCALTIMESTAMP)(\\(\\d*\\))?$", Pattern.CASE_INSENSITIVE);

    @Override
    public String columnDefinition(Column column, boolean changeCharset, boolean autoIncrement) {
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
        String rawDefault = column.getDefaultValue();
        if (null != rawDefault && !isNullLiteralDefault(rawDefault)) {
            defs.add("DEFAULT");
            if (Boolean.TRUE.toString().equals(rawDefault)) {
                defs.add("'1'");
            } else if (Boolean.FALSE.toString().equals(rawDefault)) {
                defs.add("'0'");
            } else if (isTemporalColumnType(column.getColumnType()) && isCurrentTimestampDefault(rawDefault)) {
                defs.add(formatCurrentTimestampDefault(rawDefault));
            } else if (isQuotedLiteralDefault(rawDefault)) {
                defs.add(rawDefault);
            } else {
                defs.add("'" + rawDefault + "'");
            }
        }
        String extra = column.getExtra();
        if (null != extra) {
            String extraLowerCase = extra.toLowerCase();
            if (extraLowerCase.contains("on update current_timestamp")
                    || extraLowerCase.contains("on update now(")
                    || extraLowerCase.contains("on update localtime")) {
                defs.add("ON UPDATE CURRENT_TIMESTAMP");
            } else if (autoIncrement && extraLowerCase.contains("auto_increment")) {
                defs.add("AUTO_INCREMENT");
            }
        }
        return StringUtils.join(defs, CharacterConstants.SEPARATOR_BLANK);
    }

    @Override
    public Column fillColumn(Column column, String columnDefinition) {
        String columnType = columnDefinition.split("\\s")[0];
        String nullable = columnDefinition.contains("NOT NULL") || columnDefinition.contains("not null")
                ? SystemValueConstants.NO : SystemValueConstants.YES;
        Matcher matcher = DEFAULT_VALUE_PATTERN.matcher(columnDefinition);
        String defaultValue = null;
        if (matcher.find()) {
            defaultValue = matcher.group(1);
            if (isNullLiteralDefault(defaultValue)) {
                defaultValue = null;
            } else if (isCurrentTimestampDefault(defaultValue)) {
                defaultValue = formatCurrentTimestampDefault(defaultValue);
            }
        }
        String upper = columnDefinition.toUpperCase();
        String extra = upper.contains("ON UPDATE CURRENT_TIMESTAMP")
                || upper.contains("ON UPDATE NOW(")
                || upper.contains("ON UPDATE LOCALTIME")
                ? "DEFAULT_GENERATED on update CURRENT_TIMESTAMP" : "";
        if (upper.contains("AUTO_INCREMENT")) {
            extra = "auto_increment";
        }
        return column.setColumnType(columnType).setNullable(nullable).setDefaultValue(defaultValue).setExtra(extra);
    }

    private boolean isNullLiteralDefault(String defaultValue) {
        return "NULL".equalsIgnoreCase(defaultValue);
    }

    private boolean isQuotedLiteralDefault(String defaultValue) {
        return defaultValue.length() >= 2
                && defaultValue.charAt(0) == '\''
                && defaultValue.charAt(defaultValue.length() - 1) == '\'';
    }

    private boolean isTemporalColumnType(String columnType) {
        if (StringUtils.isBlank(columnType)) {
            return false;
        }
        String type = columnType.trim().toLowerCase();
        return type.startsWith("datetime") || type.startsWith("timestamp") || type.startsWith("date");
    }

    /**
     * 仅识别时间函数默认值：CURRENT_TIMESTAMP[ (n) ]；NOW/LOCALTIME/LOCALTIMESTAMP 必须带括号，
     * 避免 VARCHAR DEFAULT 'now' 被误判。
     */
    private boolean isCurrentTimestampDefault(String defaultValue) {
        if (StringUtils.isBlank(defaultValue)) {
            return false;
        }
        Matcher matcher = CURRENT_TIMESTAMP_PATTERN.matcher(defaultValue.trim());
        if (!matcher.matches()) {
            return false;
        }
        String name = matcher.group(1).toUpperCase();
        String precision = matcher.group(2);
        if ("CURRENT_TIMESTAMP".equals(name)) {
            return true;
        }
        // NOW/LOCALTIME/LOCALTIMESTAMP：MariaDB 总是带 ()，要求括号存在
        return precision != null;
    }

    private String formatCurrentTimestampDefault(String defaultValue) {
        Matcher matcher = CURRENT_TIMESTAMP_PATTERN.matcher(defaultValue.trim());
        if (!matcher.matches()) {
            return "CURRENT_TIMESTAMP";
        }
        String precision = matcher.group(2);
        if (StringUtils.isNotBlank(precision) && precision.length() > 2) {
            return "CURRENT_TIMESTAMP" + precision;
        }
        return "CURRENT_TIMESTAMP";
    }
}
