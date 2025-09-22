package pro.shushi.pamirs.middleware.schedule.core.dialect.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
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

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        for (int i = 0; i < x.getChildren().size(); i++) {
            if (x.getChildren().get(i) instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr expr = (SQLBinaryOpExpr) x.getChildren().get(i);
                if (SQLBinaryOperator.Modulus.equals(expr.getOperator())) {
                    SQLExpr left = expr.getLeft();
                    SQLExpr right = expr.getRight();
                    SQLMethodInvokeExpr mod = new SQLMethodInvokeExpr("MOD");
                    mod.addArgument(left);
                    mod.addArgument(right);
                    x.getChildren().set(i, mod);
                }
            }
        }
        return true;
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        String tableName = x.getTableName().getSimpleName().toUpperCase();
        if (tableName.startsWith("PAMIRS_SCHEDULE")) {
            String sequenceName = tableName + "_ID";
            x.getColumns().add(new SQLIdentifierExpr("id"));
            x.getValues().addValue(new SQLSequenceExpr(new SQLIdentifierExpr(sequenceName), SQLSequenceExpr.Function.NextVal));
        }
        return true;
    }

    protected String quote(String column) {
        if ("*".equals(column)) {
            return column;
        }
        if (column.startsWith(ORACLE_QUOTE) && column.endsWith(ORACLE_QUOTE)) {
            return null;
        }
        if (column.startsWith(MYSQL_QUOTE) && column.endsWith(MYSQL_QUOTE)) {
            return ORACLE_QUOTE + column.substring(1, column.length() - 1) + ORACLE_QUOTE;
        }
        return ORACLE_QUOTE + column + ORACLE_QUOTE;
    }
}