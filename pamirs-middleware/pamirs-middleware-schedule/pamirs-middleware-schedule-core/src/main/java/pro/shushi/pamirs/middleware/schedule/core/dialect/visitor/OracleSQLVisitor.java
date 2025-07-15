package pro.shushi.pamirs.middleware.schedule.core.dialect.visitor;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * ORACLE SQL Visitor
 *
 * @author Adamancy Zhang at 16:28 on 2023-06-26
 */
public class OracleSQLVisitor extends MySqlASTVisitorAdapter {

    private static final String ORACLE_QUOTE = "\"";

    private static final String MYSQL_QUOTE = "`";

    protected static final Set<String> NOCHANGE_IDENTIFIER_SET = Sets.newHashSet("SYSTIMESTAMP", "ROWNUM");

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        String column = x.getName();
        if (NOCHANGE_IDENTIFIER_SET.contains(column)) {
            return true;
        }
        String formatColumn = quote(column);
        if (formatColumn == null) {
            return true;
        }
        x.setName(formatColumn);
        return true;
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        String alias = x.getAlias();
        if (StringUtils.isBlank(alias)) {
            return true;
        }
        String formatAlias = quote(alias);
        if (formatAlias == null) {
            return true;
        }
        x.setAlias(formatAlias);
        return true;
    }

    protected String quote(String column) {
        if (column.startsWith(ORACLE_QUOTE) && column.endsWith(ORACLE_QUOTE)) {
            return null;
        }
        if ("*".equals(column)) {
            return null;
        }
        if (column.contains(".")) {
            String[] split = column.split("\\.");
            for (int i = 0; i < split.length; i++) {
                if (!"*".equals(split[i]) && !NOCHANGE_IDENTIFIER_SET.contains(split[i])) {
                    split[i] = quote(split[i]);
                }
            }
            return String.join(".", split);
        }
        if (column.startsWith(MYSQL_QUOTE) && column.endsWith(MYSQL_QUOTE)) {
            return ORACLE_QUOTE + column.substring(1, column.length() - 1) + ORACLE_QUOTE;
        }
        return ORACLE_QUOTE + column + ORACLE_QUOTE;
    }
}